package com.aug3.sys.cfg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

import org.apache.log4j.Logger;

import com.aug3.sys.AppSystem;
import com.aug3.sys.ServiceLocator;
import com.aug3.sys.action.Action;
import com.aug3.sys.jms.JMSConnectionCreator;
import com.aug3.sys.jms.JMSProvider;
import com.aug3.sys.properties.BootProperties;

/**
 * 
 * This class implements the capability for multicasting configuration updates.
 * A command line utility is implemented to call the <code>notify()</code>
 * method to multicast a change notification. Client registers callbacks with
 * the <code>addCallback()</code> method. When the multicast listener receives
 * the message, it will invoke the registered callbacks sequentially.
 * 
 * The listener will be a singleton in each JVM. Both the server side and the
 * client side caches automatically register callbacks for cache refreshing.
 * Other clients can register more callbacks. For example, the Job Scheduler
 * will need to register a callback so when bots instance is changed it can
 * dynamically adjust it's scheduling strategy.
 * 
 * The clients create callbacks by implement the com.hp.sys.action.Action
 * interface.
 * 
 * @author xial
 */
public class UpdateNotifier_JMS extends UpdateNotifier implements
		MessageListener, ConfigConstants {

	// ==========================================================================
	// constant definitions
	// ==========================================================================
	/**
	 * the key to retrieve <code>UpdateNotification</code> from the callback
	 * action parameters
	 */
	public final static String NOTIFICATION = "__notification__";

	private final static String CONN_FACTORY_PROP = "cfg.update.jms.factory";
	private final static String CONN_JNDI = "GlobalTopicConnectionFactory";

	private final static String TOPIC_PROP = "cfg.update.jms.topic";

	private final static String TOPIC_JNDI = "ConfigUpdate";

	// logger instance
	private static final Logger LOG = Logger
			.getLogger(UpdateNotifier_JMS.class);

	// ==========================================================================
	// private members
	// ==========================================================================
	// singleton instance, each JVM should have only one UpdateNotifier
	static UpdateNotifier_JMS updateNotifier;
	private static boolean shutdownHookAdded;

	private boolean jmsStarted;
	private TopicConnection topicConn;
	private TopicSession topicSession;
	private TopicPublisher publisher;
	private TopicSubscriber topicSubscriber;
	private ObjectMessage objMsg;
	private List<Callback> callbacks = new Vector<Callback>();
	private List<Callback> procStatusCallbacks = Collections
			.synchronizedList(new ArrayList<Callback>());

	private String factoryJndiName = BootProperties.getInstance().getProperty(
			CONN_FACTORY_PROP, CONN_JNDI);
	private String _topicName = BootProperties.getInstance().getProperty(
			TOPIC_PROP, TOPIC_JNDI);

	// ==========================================================================
	// public methods
	// ==========================================================================
	/**
	 * Publishes a update notification.
	 * 
	 * @param un
	 *            an update notification message
	 * @throws ConfigException
	 */
	public static void publish(UpdateNotification un) throws ConfigException {
		// initialize the singleton
		init();

		// start the jms session so we can publish, note that we don't want to
		// subscribe if this is the commond line publish tool
		updateNotifier.startJMS(true);

		updateNotifier.pPublish(un);
	}

	/**
	 * Cleans up the JMS resources created for the update notifier.
	 */
	public static void shutdown() {
		// initialize the singleton
		init();
		updateNotifier.pShutdown();
	}

	/**
	 * Registers a callback for the configuration update notification.
	 * 
	 * @param matcher
	 *            the matcher for arranging the callbacks
	 * @param ation
	 *            the callback implementation
	 * @param priority
	 *            the priority for arranging the callbacks
	 * @see #addCallback(Action)
	 */
	public static void addCallback(UpdateNotification matcher, Action ation,
			int priority) {

		// initialize the singleton
		init();
		updateNotifier.pAddCallback(matcher, ation, priority);
	}

	/**
	 * Registers a callback for the configuration update notification, with
	 * default token and priority.
	 * 
	 * @param a
	 *            the callback implementation
	 * @see #addCallback(UpdateNotification, Action, int)
	 */
	public static void addCallback(Action a) {
		addCallback(null, a, UPDATE_NOTIFY_CALLBACK_PRIORITY_NORMAL);
	}

	/**
	 * add product status callback to the the list, each callback action
	 * registered will be called when the product status message will be
	 * received.
	 * 
	 */
	public static void addDTProcStatusCallback(Action a) {
		// initialize the singleton
		init();
		updateNotifier.pAddDTProdStatusCallback(a);
	}

	/**
	 * Starts the JMS sessions and the update notification subscriber. When a
	 * subscriber is registered, a background daemon thread starts listen. This
	 * daemon listening thread prevents the VM to exit. So for clients that run
	 * and exit, such as command line utilities <code>ImportPrefPage</code>,
	 * they should not all this method. For those processes that needs to listen
	 * for update notifications, such as <code>PreForker</code> they should call
	 * this method in the beginning of the process.
	 */
	public static void start() {
		// initialize the singleton
		init();

		// initialize JMS if not initialized yet.
		updateNotifier.startJMS(false);
	}

	/**
	 * Re-starts the JMS sessions and the update notification subscriber. This
	 * method is used in case the server is down and the client needs to
	 * automatically re-connect. In the current implementation, in the entire
	 * system only the heartbeat thread in the DTProc component will need this
	 * functionality. Please don't call this method in you code.
	 */
	public static void restart() throws Exception {
		// shutdown the old resources
		updateNotifier.pShutdown();

		// restart the jms session
		start();

		// invoke callbacks registered for the config update notification
		updateNotifier.invokeCallbacks(null);
	}

	// ==========================================================================
	// implement MessageListener interface
	// ==========================================================================
	/**
	 * Receives Messages from JMS (MessageListener interface)
	 * 
	 * @param msg
	 *            Message object received from JMS
	 */
	public void onMessage(Message msg) {

		try {
			// sanity check
			if (!(msg instanceof ObjectMessage))
				return;

			ObjectMessage objMsg = (ObjectMessage) msg;
			Object innerObj = objMsg.getObject();

			/*
			 * we should be checking for ProcStatuMessage, but since it will
			 * create circular package dependency, it is not checked. This part
			 * requires to be re-written to remove such dependency.
			 */
			if (innerObj != null && !(innerObj instanceof UpdateNotification)) {
				invokeDTProcStatusCallbacks(innerObj);
				return;
			}

			UpdateNotification un = (UpdateNotification) objMsg.getObject();

			// invoke callbacks registered for the config update notification
			invokeCallbacks(un);
		} catch (Exception e) {
			error("failed to invoke callback for update notification", e);
		}
	}

	// ==========================================================================
	// private helpers
	// ==========================================================================
	// invoke callbacks registered for the config update notification
	private void invokeCallbacks(UpdateNotification un) throws Exception {
		// prepare the arguments for invoking the callbacks
		Map<String, UpdateNotification> args = new HashMap<String, UpdateNotification>();
		args.put(NOTIFICATION, un);

		synchronized (callbacks) {
			// loop through the callbacks and invoke them
			for (Callback callback : callbacks) {

				// match the message with the registered matcher, if one exists.
				// messages without matchers will always match
				if ((null != un) && (callback.matcher != null)
						&& (!callback.matcher.match(un))) {
					continue;
				}
				callback.callback.perform(args);

			}
		}
	}

	// invoke callbacks registered for the proc status notification
	private void invokeDTProcStatusCallbacks(Object procStatusMsg)
			throws Exception {
		// prepare the arguments for invoking the callbacks
		Map<String, Object> args = new HashMap<String, Object>();
		args.put(NOTIFICATION, procStatusMsg);

		synchronized (procStatusCallbacks) {
			// loop through the callbacks and invoke them
			for (Callback c : procStatusCallbacks) {
				c.callback.perform(args);
			}
		}
	}

	// initialize the singleton
	private synchronized static void init() {
		if (null == updateNotifier) {
			// initialize the singleton
			updateNotifier = new UpdateNotifier_JMS();
		}
	}

	public UpdateNotifier_JMS() {
		// init();
	}

	/**
	 * initialize the jms topic connection, and the topic listener. the
	 * subscriber will create a non-daemon listener thread, causing the VM not
	 * able to exit. so subscribe only when the process does not exit, such as
	 * the app server or proc
	 * 
	 * @param noSubscriber
	 */
	private synchronized void startJMS(boolean noSubscriber) {
		if (jmsStarted)
			return;

		try {
			// initializes the publisher
			JMSConnectionCreator connCreator = new JMSConnectionCreator();
			topicConn = connCreator.createTopicConnection(factoryJndiName);
			topicSession = topicConn.createTopicSession(false,
					TopicSession.AUTO_ACKNOWLEDGE);
			Topic topic = topicSession.createTopic(_topicName);
			publisher = topicSession.createPublisher(topic);
			JMSProvider provider = ServiceLocator.getJMSProvider();
			provider.configure(publisher);
			objMsg = topicSession.createObjectMessage();

			// initializes the subscriber
			if (!noSubscriber) {
				topicSubscriber = topicSession.createSubscriber(topic);
				topicSubscriber.setMessageListener(this);
			}

			// start the session
			topicConn.start();
		} catch (Exception e) {
			if (AppSystem.inServer()) {
				error("failed to start JMS resources [" + TOPIC_JNDI + "]", e);
			} else {
				warn("failed to start JMS resources [" + TOPIC_JNDI + "] " + e);
			}
		}

		// add hooks to clean up jms resources
		if (!shutdownHookAdded) {
			addShutdownHook();
			shutdownHookAdded = true;
		}

		// cannot use logging facility here, it's not fully initialized yet.
		// it will cause notification callbacks been called multiple times.
		LOG.info("configuration update notifier successfully initialized");

		// set the flag so we don't create the singleton resources again
		jmsStarted = true;
	}

	// publish the message to the JMS topic
	private synchronized void pPublish(UpdateNotification un)
			throws ConfigException {
		try {
			// set the notification to the JMS object message
			objMsg.setObject(un);

			publisher.publish(objMsg);
		} catch (JMSException e) {
			throw new ConfigException("failed to publish notification", e);
		}
	}

	// cleans up the JMS resources created for the update notifier.
	private synchronized void pShutdown() {
		// check whether we need to do shutdown
		if (!jmsStarted)
			return;

		// reset the flag so we can initialize the JMS resources
		jmsStarted = false;

		try {
			topicSubscriber.close();
		} catch (Exception e) {
		}
		try {
			topicSession.close();
		} catch (Exception e) {
		}

		try {
			if (null != topicConn)
				topicConn.close();
		} catch (JMSException e) {
			warn("failed to shutdown UpdateNotifier. " + e);
		}
	}

	// registers a callback to for the configuration update notification.
	private void pAddCallback(UpdateNotification matcher, Action a, int prio) {
		synchronized (callbacks) {
			if (UPDATE_NOTIFY_CALLBACK_PRIORITY_HIGH == prio) {
				callbacks.add(0, new Callback(matcher, a));
			} else {
				callbacks.add(new Callback(matcher, a));
			}
		}
		// startJMS(false);
	}

	/**
	 * registers a callback to for the configuration update notification.
	 */
	private synchronized void pAddDTProdStatusCallback(Action a) {
		synchronized (procStatusCallbacks) {
			procStatusCallbacks.add(new Callback(null, a));
		}
	}

	// add the shutdown hook to clean up jms resources
	private void addShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				pShutdown();
			}
		});
	}

	// log a warning message
	private static void warn(String msg) {
		LOG.warn(msg);
	}

	// log a warning message
	private static void error(String msg, Throwable e) {
		LOG.error(msg);
		if (e != null) {
			LOG.error(e);
		}
	}

	// ==========================================================================
	// private data structure
	// ==========================================================================
	private class Callback {
		private UpdateNotification matcher;
		private Action callback;

		private Callback(UpdateNotification un, Action action) {
			matcher = un;
			callback = action;
		}
	}

}