package com.aug3.sys.log.async;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.aug3.sys.jms.MQConnectionCreator;
import com.aug3.sys.log.LogConfig;

public class MsgReceiver extends Thread implements ExceptionListener {

	private String subject;

	public MsgReceiver() {

		this.subject = new LogConfig().getProperty(
				"asynclog.mq.destination.subject", "DEFAULT_SUBJECT");

	}

	public MsgReceiver(String subject) {
		this.subject = subject;
	}

	public void run() {

		Connection connection = null;
		Session session = null;
		Destination destination = null;
		MessageConsumer consumer = null;

		Message message = null;

		try {
			connection = MQConnectionCreator.createConnection();

			connection.setExceptionListener(this);

			connection.start();

			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			destination = session.createQueue(subject);

			// create a MessageConsumer from the Session to the queue
			consumer = session.createConsumer(destination);

			TextMessage txtMsg;

			while (true) {

				// wait for a message
				message = consumer.receive(1000);

				if (message != null) {
					if (message instanceof TextMessage) {
						txtMsg = (TextMessage) message;
						LogWritter.writeLog(txtMsg.getText());
					}
				}

			}

		} catch (JMSException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					consumer.close();
				} catch (JMSException e) {
					e.printStackTrace();
					consumer = null;
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

	@Override
	public void onException(JMSException arg0) {

		System.out.println("JMSException occured. Shutting down client.");
	}

}
