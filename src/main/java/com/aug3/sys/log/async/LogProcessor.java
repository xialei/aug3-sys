package com.aug3.sys.log.async;

/**
 * This class is used to process log, writing log to persist text file.
 * 
 * You can deploy this process to distributed machine for scale.
 * 
 * We will provide a shell script to start the process.
 * 
 * @author xial
 * 
 */
public class LogProcessor {

	public static void main(String args[]) {

		MsgReceiver thread = null;

		if (args.length == 0) {
			thread = new MsgReceiver();
		} else if (args.length == 1) {
			thread = new MsgReceiver(args[0].trim());
		} else {
			throw new IllegalArgumentException(
					"only one parameter is accepted, which is the subject of the message queue");
		}

		thread.start();
	}
}
