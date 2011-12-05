package com.aug3.sys.log.async;

import java.util.logging.Level;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.aug3.sys.jms.MQConnectionCreator;

public class JMSLogger extends AsyncLogger {
	private Connection connection;
	private Session session;
	private Queue destination;
	private MessageProducer producer;

	private String subject;

	public JMSLogger(String subject, String moduleName, String tagName) {
		super(moduleName, tagName);

		this.subject = subject;
	}

	protected void logImpl(Level level, String message) {
		try {
			connection = MQConnectionCreator.createConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			destination = session.createQueue(subject);
			producer = session.createProducer(destination);

			TextMessage textMessage = session.createTextMessage(message);
			producer.send(textMessage);
		} catch (JMSException e) {
			handleJMSException(e);
		} finally {
			close();
		}
	}

	private void handleJMSException(JMSException e) {
		e.printStackTrace();
	}

	public synchronized void close() {
		if (connection != null) {
			try {
				producer.close();
			} catch (JMSException e) {
				e.printStackTrace();
				producer = null;
			}
			try {
				session.close();
			} catch (JMSException e) {
				e.printStackTrace();
				session = null;
			}
			try {
				connection.close();
			} catch (JMSException e) {
				e.printStackTrace();
				connection = null;
			}
		}
	}

}
