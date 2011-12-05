package com.aug3.sys.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import com.aug3.sys.CommonException;

/**
 * This class contains utilities to transform Java object to and from serialized
 * form.
 * 
 * @author xial
 */
public class ObjectIO {

	/**
	 * 
	 * Returns an deep clone object with the given original object. The object
	 * must be serializable.
	 * 
	 * @param Object
	 *            originalObject
	 * @return Object new cloned object
	 * @throws CommonException
	 */
	public static Object easyClone(Object originalObject)
			throws CommonException {
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);

			oos.writeObject(originalObject);
			oos.flush();

			ByteArrayInputStream bis = new ByteArrayInputStream(
					bos.toByteArray());
			ois = new ObjectInputStream(bis);

			return ois.readObject();

		} catch (Exception e) {
			throw new CommonException(
					"Exception in ObjectCloner : [ObjectIO.easyClone] ", e);
		} finally {
			try {
				oos.close();
			} catch (IOException e) {
				throw new CommonException("Error closing ObjectOutputStream.",
						e);
			}
			try {
				ois.close();
			} catch (IOException e) {
				throw new CommonException("Error closing ObjectInputStream.", e);
			}
		}

	}

	/**
	 * Converts an object into a byte array. The object has to implement either
	 * <code>Serializable</code> or <code>Externalizable</code>.
	 * 
	 * @param obj
	 *            the object to be serialized
	 * @return byte[] converted byte array
	 * @throws CommonException
	 */
	public static byte[] toByteArray(Object o) throws CommonException {
		return toByteArray(o, 1024);
	}

	/**
	 * Converts an object into a byte array. The object has to implement either
	 * <code>Serializable</code> or <code>Externalizable</code>.
	 * 
	 * @param obj
	 *            the object to be serialized
	 * @param size
	 *            the initial size of the buffer, the default is 1024
	 * @return byte[] converted byte array
	 * @throws CommonException
	 */
	public static byte[] toByteArray(Object obj, int size)
			throws CommonException {
		// sanity check the input
		if (obj == null)
			return null;
		try {
			ByteArrayOutputStream bs = new ByteArrayOutputStream(size);
			ObjectOutputStream os = new ObjectOutputStream(bs);

			os.writeObject(obj);
			os.close();
			return bs.toByteArray();
		} catch (IOException e) {
			throw new CommonException("failed to convert object to byte array",
					e);
		}
	}

	/**
	 * Converts a byte array into an object. The object has to implement either
	 * <code>Serializable</code> or <code>Externalizable</code>.
	 * 
	 * @param bytes
	 *            the byte buffer representing the serialized object
	 * @return Object the de-serialized object, null if failed to de-serialized
	 * @throws DTException
	 */
	public static Object fromByteArray(byte[] bytes) throws CommonException {
		// sanity check the input
		if ((bytes == null) || (bytes.length == 0))
			return null;

		try {
			ByteArrayInputStream bs = new ByteArrayInputStream(bytes);
			ObjectInputStream is = new ObjectInputStream(bs);

			Object o = is.readObject();
			is.close();

			return o;
		} catch (Exception e) {
			throw new CommonException("failed to convert byte array to object",
					e);
		}
	}

	/**
	 * Pretty print an object as a string by recursing down all it's members
	 * 
	 * @param obj
	 *            Object to be formatted.
	 * @param name
	 *            Name of object for first level
	 * @param prefix
	 *            String to be prefixed. Used for indentation
	 * @param grow
	 *            String to be prefixed on each level. Used on each recursion to
	 *            grow indent.
	 * 
	 *            example: objectAsStringBuffer(obj, name, "", "        ")
	 */
	static public StringBuffer objectAsStringBuffer(Object obj, String name,
			String prefix, String grow) {
		StringBuffer sb = new StringBuffer();
		char frameleft = '{';
		char frameright = '}';
		String preamble = prefix + name + " = ";

		// check for deep recursion
		if (prefix.length() > 80)
			return printFramed(sb, preamble,
					"*** RECURSION TOO DEEP (CYCLE?) ***", frameleft,
					frameright);

		// null is special case
		if (obj == null)
			return printFramed(sb, preamble, "null", frameleft, frameright);

		// nice stack trace (before toString(), since it has one)
		if (obj instanceof Throwable) {
			Throwable t = (Throwable) obj;
			return printFramed(sb, preamble, CommonException.getStackTrace(t),
					frameleft, frameright);
		}

		// iterate over if array
		if (obj instanceof Object[]) {
			Object[] oarr = (Object[]) obj;
			if (oarr.length == 0)
				return sb.append(preamble + "{ ARRAY [] }\n");
			sb.append(preamble + "{ ARRAY (" + oarr.length + " items)[\n");
			for (int i = 0; i < oarr.length; ++i) {
				sb.append(objectAsStringBuffer(oarr[i], name + "[" + i + "]",
						prefix + grow, grow));
			}
			return sb.append(prefix + "] }\n");
		}

		// iterate over if Collection
		if (obj instanceof Collection) {
			Collection<?> c = (Collection<?>) obj;
			if (c.size() == 0)
				return sb.append(preamble + "{ COLLECTION [] }\n");
			sb.append(preamble + "{ COLLECTION (" + c.size() + " items) [\n");
			Iterator<?> i = c.iterator();
			int count = 0;
			while (i.hasNext()) {
				sb.append(objectAsStringBuffer(i.next(), name + "[" + ++count
						+ "]", prefix + grow, grow));
			}
			return sb.append(prefix + "] }\n");
		}

		// if toString() has no @, just take it
		if (obj.toString().indexOf('@') == -1)
			return printFramed(sb, preamble, obj.toString(), frameleft,
					frameright);

		// loop over the properties of this obj
		try {
			sb.append(preamble + "{\n");
			BeanInfo bi = null;
			bi = Introspector.getBeanInfo(obj.getClass());
			PropertyDescriptor[] pds = bi.getPropertyDescriptors();
			for (int i = 0; i < pds.length; ++i) {
				name = pds[i].getName();
				Object value = pds[i].getReadMethod().invoke(obj,
						(Object[]) null);
				sb.append(objectAsStringBuffer(value, name, prefix + grow, grow));
			}
			return sb.append(prefix + "}\n");
		} catch (Exception e) {
			sb.append(preamble + "{ *** EXCEPTION DURING RECURSION ***\n");
			sb.append(objectAsStringBuffer(e, e.getClass().getName(), prefix
					+ grow, grow));
			return sb.append(prefix + "}\n");
		}
	}

	/**
	 * Pretty print a string, framed nicely with nice indent.
	 * 
	 * @param sb
	 *            StringBuffer to print to
	 * @param prefix
	 *            String to be prefixed. Used for indentation.
	 * @param item
	 *            String to be shown.
	 * @param frameleft
	 *            Left framing character
	 * @param frameright
	 *            Right framing character
	 */
	static public StringBuffer printFramed(StringBuffer sb, String prefix,
			String item, char frameleft, char frameright) {

		// get a blank line to work with
		StringBuffer blankPrefix = new StringBuffer();

		for (int i = 0; i < prefix.length(); ++i)
			blankPrefix.append(' ');

		// find the longest line
		int maxsize = 0;
		StringTokenizer counter = new StringTokenizer(item, "\n");
		while (counter.hasMoreTokens()) {
			String token = counter.nextToken();
			maxsize = (maxsize < token.length()) ? token.length() : maxsize;
		}

		// print it out
		StringTokenizer st = new StringTokenizer(item, "\n");
		if (st.hasMoreTokens()) {
			StringBuffer line = new StringBuffer();
			line.append(prefix).append(frameleft).append(st.nextToken());
			while (line.length() <= maxsize + blankPrefix.length())
				line.append(' ');
			sb.append(line).append(frameright).append("\n");
		}
		while (st.hasMoreTokens()) {
			StringBuffer line = new StringBuffer();
			line.append(blankPrefix).append(frameleft).append(st.nextToken());
			while (line.length() <= maxsize + blankPrefix.length())
				line.append(' ');
			sb.append(line).append(frameright).append("\n");
		}

		return sb;
	}

	/**
	 * Compress an uncompressed byte array
	 * 
	 * @param input
	 *            byte array
	 * @return compressed byte array
	 */
	public static byte[] deflate(byte[] bytArrInput) throws CommonException {
		// Use maximum compression
		Deflater deflater = new Deflater();
		deflater.setLevel(Deflater.BEST_COMPRESSION);
		deflater.setInput(bytArrInput);
		deflater.finish();

		ByteArrayOutputStream stream = new ByteArrayOutputStream(
				bytArrInput.length);

		byte[] buf = new byte[4096];
		while (!deflater.finished()) {
			int count = deflater.deflate(buf);
			stream.write(buf, 0, count);
		}
		try {
			stream.close();
		} catch (IOException e) {
			throw new CommonException("Error closing a byte array stream.", e);
		}
		return stream.toByteArray();
	}

	/**
	 * Expand a compressed byte array
	 * 
	 * @param input
	 *            byte array
	 * @return compressed byte array
	 */
	public static byte[] inflate(byte[] bytArrInput) throws CommonException {

		Inflater inflater = new Inflater();
		inflater.setInput(bytArrInput);

		ByteArrayOutputStream bos = new ByteArrayOutputStream(
				bytArrInput.length);

		// Inflate the data
		byte[] buf = new byte[4096];
		while (!inflater.finished()) {
			try {
				int count = inflater.inflate(buf);
				bos.write(buf, 0, count);
			} catch (DataFormatException e) {
				throw new CommonException(
						"Error encountered inflating a byte array.", e);
			}
		}
		try {
			bos.close();
		} catch (IOException e) {
			throw new CommonException("Error closing a byte array stream.", e);
		}

		return bos.toByteArray();
	}

}
