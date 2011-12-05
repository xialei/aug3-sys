package com.aug3.sys.cfg;

import org.apache.log4j.Logger;

import com.aug3.sys.action.Action;

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
public class UpdateNotifier implements ConfigConstants {

	/**
	 * the key to retrieve <code>UpdateNotification</code> from the callback
	 * action parameters
	 */
	public final static String NOTIFICATION = "__notification__";

	static boolean useJMS = true;

	private static final Logger LOG = Logger.getLogger(UpdateNotifier.class);

	static {
		if (System.getProperty("useJMS") == null)
			useJMS = false;
	}

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
		if (useJMS)
			UpdateNotifier_JMS.publish(un);

	}

	/**
	 * Cleans up the JMS resources created for the update notifier.
	 */
	public static void shutdown() {
		if (useJMS)
			UpdateNotifier_JMS.shutdown();
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

		if (useJMS)
			UpdateNotifier_JMS.addCallback(matcher, ation, priority);
	}

	/**
	 * Registers a callback for the configuration update notification, with
	 * default token and priority.
	 * 
	 * @param a
	 *            the callback implementation
	 */
	public static void addCallback(Action a) {

		if (useJMS)
			UpdateNotifier_JMS.addCallback(a);
	}

	/**
	 * Starts the JMS sessions and the update notification subscriber. When a
	 * subscriber is registered, a background daemon thread starts listen. This
	 * daemon listening thread prevents the VM to exit. For those processes that
	 * needs to listen for update notifications, such as <code>PreForker</code>
	 * they should call this method in the beginning of the process.
	 */
	public static void start() throws ConfigException {
		if (useJMS)
			UpdateNotifier_JMS.start();

	}

	/**
	 * Re-starts the JMS sessions and the update notification subscriber. This
	 * method is used in case the server is down and the client needs to
	 * automatically re-connect. In the current implementation, in the entire
	 * system only the heartbeat thread in the AppProc component will need this
	 * functionality. Please don't call this method in you code.
	 */
	public static void restart() throws Exception {

		if (useJMS)
			UpdateNotifier_JMS.restart();

	}

	// ----------------------------------------------------------------------
	// HELPER METHODS
	// ----------------------------------------------------------------------

	/**
	 * command line parsing helper. parses command line arguments. strings
	 * starting with a "-" are treated as argument names. if the string after an
	 * argument name is not a name itself, it is returned as the value, else a
	 * "" is returned. if the name is not present at all, a null is returned.
	 */
	private static String getArgValue(String param, String args[]) {
		String p;

		for (int i = 0; i < args.length; i++) {
			if (args[i].length() < 2)
				continue;

			if (!args[i].startsWith("-"))
				continue;

			p = args[i].substring(1, args[i].length());

			if (p.equalsIgnoreCase(param)) {
				if ((i < args.length - 1) && (!args[i + 1].startsWith("-")))
					return args[i + 1];
				return "";
			}
		}

		return null;
	}

	// prints the usage information of the command line tool
	private static void usage() {
		StringBuffer buf = new StringBuffer();
		buf.append("usage: cfgupdate.sh(cmd) [-argument_names argument_values]\n\n");
		buf.append("This command line utility is used to publish a configuration update\n");
		buf.append("notification.  When it's invoked without arguments, it publishes a generic\n");
		buf.append("notification.  In that case, all callbacks registered in all processes on all\n");
		buf.append("machines are invoked.  If it is invoked with arguments, then only callbacks\n");
		buf.append("registered with matching parameters get invoked.  Note that the more\n");
		buf.append("argument values you speficy, the more specific the matching will be.  For\n");
		buf.append("example, if you only specify the host name, then all proc instances of all\n");
		buf.append("types on the machine is impacted.\n\n");
		buf.append("The following is a list of valid arguments.\n\n");
		buf.append("\thelp            prints this help message\n");
		buf.append("\thostName        the target host name, e.g. production-job01\n");
		buf.append("\tprocName        the process name, e.g., AppProc\n");
		buf.append("\tconfigType      the configuration type, e.g., company\n");
		buf.append("\tcallbackToken   a custom matching token\n");
		buf.append("\tprocType		  the target proc type\n");
		buf.append("\tprocInstance    the target proc instance\n");
		buf.append("\tcustomStr1      a custom string value\n");
		buf.append("\tcustomStr2      a custom string value\n");
		buf.append("\tcustomLong1     a custom number value\n");
		buf.append("\tcustomLong2     a custom number value\n\n");
		buf.append("The following is an example of publishing a message to alter loggin level\n");
		buf.append("of the export bots instance 0 on production-job01:\n\n");
		buf.append("\tcfgupdate.cmd -hostname production-job01 -procname proc -proctype 4\n");
		buf.append("\t-procinstance 0 -customstr1 com.companyName -customstr2 DEBUG\n\n");
		buf.append("And this is an example of turnning the logging off (log only warning or error\n");
		buf.append("messages) of all export bots:\n\n");
		buf.append("\tcfgupdate.cmd -procname proc -proctype 4 -customstr1 com.companyName\n");
		buf.append("\t-customstr2 WARN\n\n");
		buf.append("And this is an example of telling the whole world to re-configure itself\n\n");
		buf.append("\tcfgupdate.cmd\n");

		LOG.info(buf);
	}

	// ==========================================================================
	// command line utility to broadcast configuration update notification
	// ==========================================================================
	/**
	 * Used to broadcast a configuration update notification
	 */
	public static void main(String[] args) throws Exception {
		// if there's no arguments, publish a generic update so all callbacks
		// get invoked
		try {
			if (args.length < 1) {
				UpdateNotifier.publish(null);
				UpdateNotifier.shutdown();
			} else if (null != getArgValue("help", args)) {
				usage();
			} else {
				UpdateNotification un = new UpdateNotification();
				String s;

				if (null != (s = getArgValue("hostname", args))) {
					un.setHostName(s);
				}

				if (null != (s = getArgValue("procname", args))) {
					un.setProcName(s);
				}

				if (null != (s = getArgValue("proctype", args))) {
					un.setProcType(Integer.parseInt(s));
				}

				if (null != (s = getArgValue("procinstance", args))) {
					un.setProcInstance(Integer.parseInt(s));
				}

				if (null != (s = getArgValue("customstr1", args))) {
					un.setCustomStrVal1(s);
				}

				if (null != (s = getArgValue("customstr2", args))) {
					un.setCustomStrVal2(s);
				}

				if (null != (s = getArgValue("customlong1", args))) {
					un.setCustomLongVal1(Long.parseLong(s));
				}

				if (null != (s = getArgValue("customlong2", args))) {
					un.setCustomLongVal2(Long.parseLong(s));
				}

				UpdateNotifier.publish(un);
				UpdateNotifier.shutdown();
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

}
