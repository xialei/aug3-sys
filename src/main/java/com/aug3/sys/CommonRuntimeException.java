package com.aug3.sys;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * This class is identical to CommonException except that it extends
 * RuntimeException so the methods throwing it don't have to declare it in its
 * signature.
 * 
 * @author xial
 */
public class CommonRuntimeException extends RuntimeException implements
		Externalizable {

	private Throwable linkedException;
	private int errCode;
	private String message;
	private Boolean isLinkedException = Boolean.FALSE;

	// =========================================================================
	// constructors
	// =========================================================================
	public CommonRuntimeException(String why, int errCode, Throwable t) {

		this.message = why;
		this.errCode = errCode;
		this.linkedException = t;

		this.isLinkedException = linkedException != null ? Boolean.TRUE
				: Boolean.FALSE;
	}

	public CommonRuntimeException() {
		this(null, 0, null);
	}

	public CommonRuntimeException(String why) {
		this(why, 0, null);
	}

	public CommonRuntimeException(String why, int errCode) {
		this(why, errCode, null);
	}

	public CommonRuntimeException(String why, Throwable t) {
		this(why, 0, t);
	}

	// =========================================================================
	// member accessors
	// =========================================================================
	public String getMessage() {
		return message;
	}

	public int getErrCode() {
		return errCode;
	}

	/**
	 * Returns the linked exception of the exception
	 */
	public Throwable getCause() {
		return linkedException;
	}

	/**
	 * Add extra message to the original message of the exception. The new
	 * message is added after a line break.
	 * 
	 * @param msg
	 *            the new message to be added to the exception
	 * @return this exception
	 */
	public CommonRuntimeException addMessage(String msg) {
		if (null != message) {
			message += "\n" + msg;
		} else {
			message = msg;
		}

		return this;
	}

	/**
	 * Print an exceptions stack trace into a string.
	 * 
	 * @param t
	 *            the exception
	 * @return the stack trace in a string
	 */
	public static String getStackTrace(Throwable t) {
		// sanity check
		if (null == t)
			return null;

		// prepare buffer for writing the stack trace
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);

		// dump the stack trace to the writer buffer
		t.printStackTrace(pw);

		// get the string from the writer buffer
		String s = sw.toString();

		// close the writer buffer
		try {
			pw.close();
		} catch (Exception e) {
		}

		// return the result
		return s;
	}

	// =========================================================================
	// overrides base class methods
	// =========================================================================
	/**
	 * Converts the exception to a string. Note this only gives the error
	 * message for the outside most exception. To get the linked exception
	 * information, use <code>printStackTrace()</code> or <code>
	 * Util.getStackTrace(Exception)</code> instead.
	 */
	public String toString() {
		StringBuffer s = new StringBuffer();

		s.append(getClass().getName()).append(":");
		s.append("code: ").append(errCode).append("; ");
		s.append("reason: ").append(message).append("\n");

		return s.toString();
	}

	/**
	 * Prints the exception stack trace to the standard error output. For the
	 * outer container exceptions, only the error message is printed. For the
	 * inner most exception, the whole stack trace is printed.
	 */
	public void printStackTrace() {
		synchronized (System.err) {
			_printStackTrace(System.err);
		}
	}

	/**
	 * Prints the exception stack trace to the standard error output.
	 * 
	 * @param s
	 *            the output
	 * @see #printStackTrace()
	 */
	public void printStackTrace(PrintStream s) {
		synchronized (s) {
			_printStackTrace(s);
		}
	}

	/**
	 * Prints the exception stack trace to the standard error output.
	 * 
	 * @param s
	 *            the output
	 * @see #printStackTrace()
	 */
	public void printStackTrace(PrintWriter s) {
		synchronized (s) {
			_printStackTrace(s);
		}
	}

	/**
	 * print the stack trace to the specified output
	 * 
	 * @param output
	 */
	private void _printStackTrace(Object output) {
		/**
		 * isLinkedExcpetion can be true and linkedException can be null, only
		 * when the exception is serialized/deserialized, in all other case both
		 * can be either (true and not null) or (false and null)
		 */
		// if there's a linked exception, print the error message only.
		// don't print the whole stack trace.
		if (null != linkedException) {
			if (output instanceof PrintStream) {
				// print the error message only
				((PrintStream) output).println(this);

				// print the linked exception stack
				linkedException.printStackTrace((PrintStream) output);
			} else if (output instanceof PrintWriter) {
				// print the error message only
				((PrintWriter) output).println(this);

				// print the linked exception stack
				linkedException.printStackTrace((PrintWriter) output);
			}
		} else if (!isLinkedException.booleanValue()) {
			// if there's no linked exception, use the super class's
			// print stack trace method. otherwise it's an infinite loop
			if (output instanceof PrintStream) {
				super.printStackTrace((PrintStream) output);
			} else if (output instanceof PrintWriter) {
				super.printStackTrace((PrintWriter) output);
			}
		} else {
			if (output instanceof PrintStream) {
				((PrintStream) output).println(this);
			} else if (output instanceof PrintWriter) {
				((PrintWriter) output).println(this);
			}
		}
	}

	// =========================================================================
	// implement the serialization functions
	// =========================================================================

	/**
	 * The object implements the writeExternal method to save its contents by
	 * calling the methods of DataOutput for its primitive values or calling the
	 * writeObject method of ObjectOutput for objects, strings, and arrays.
	 * <p>
	 * 
	 * @param out
	 *            the stream to write the object to
	 * @throws IOException
	 *             Includes any I/O exceptions that may occur
	 */
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {

		out.writeBoolean(isLinkedException.booleanValue());
		out.writeInt(errCode);

		/**
		 * if linked error than get printstack of linked exception only
		 */
		if (isLinkedException.booleanValue())
			out.writeObject(message + "\n\n" + getStackTrace(linkedException));
		else
			out.writeObject(message + "\n" + getStackTrace(this));

	}

	/**
	 * The object implements the readExternal method to restore its contents by
	 * calling the methods of DataInput for primitive types and readObject for
	 * objects, strings and arrays. The readExternal method must read the values
	 * in the same sequence and with the same types as were written by
	 * writeExternal.
	 * <p>
	 * 
	 * @param in
	 *            the stream to read data from in order to restore the object
	 * 
	 * @throws IOException
	 *             if I/O errors occur
	 * @throws ClassNotFoundException
	 *             If the class for an object being restored cannot be found.
	 */
	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {

		isLinkedException = (in.readBoolean()) ? Boolean.TRUE : Boolean.FALSE;
		errCode = in.readInt();
		message = (String) in.readObject();

	}
}
