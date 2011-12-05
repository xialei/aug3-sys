package com.aug3.sys.util;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aug3.sys.CommonRuntimeException;
import com.aug3.sys.log.MLogger;

/**
 * This is used in conjunction with Printable for debugging. These classes make
 * it easy to print java objects. The static printObject() methods are for
 * clients and the non-static ones are for Printable classes to use in their
 * print() method.
 * 
 * @author xial
 */
public class Printer {

	private PrintWriter printWriter;
	private int indent;

	// maps class names to the Printable objects which should print them.
	// Map[ class name (String) --> PrintProxy ]
	private Map<String, PrintProxy> printProxies;

	/**
	 * Print an object.
	 */
	public static void printObject(Object obj) {
		printObject(null, obj);
	}

	/**
	 * Print an object.
	 */
	public static void printObject(String label, Object obj) {
		printObject(label, obj, System.out);
	}

	/**
	 * Print an object to a PrintStream.
	 */
	public static void printObject(String label, Object obj, PrintStream out) {
		// print the to the PrintStream
		out.print(getText(label, obj));
	}

	// these indicate whether to write a debug, info or error message to the
	// MLogger.
	public static final int DEBUG = 1;
	public static final int INFO = 2;
	public static final int ERROR = 3;

	/**
	 * Print an object to a MLogger.
	 */
	public static void printObject(MLogger log, String label, Object obj) {
		printObject(log, label, obj, DEBUG);
	}

	/**
	 * Print an object to a MLogger.
	 */
	public static void printObject(MLogger log, String label, Object obj,
			int msgType) {
		switch (msgType) {
		case DEBUG:
			log.debug(getText(label, obj));
			break;
		case INFO:
			log.info(getText(label, obj));
			break;
		case ERROR:
			log.error(getText(label, obj));
			break;

		default:
			throw new CommonRuntimeException("printObject() Bad msgType: "
					+ msgType);
		}
	}

	/**
	 * Print an object.
	 */
	public static String getText(String label, Object obj) {
		// create a StringWriter
		StringWriter sw = new StringWriter();

		// generate text
		Printer printer = new Printer(sw);
		printer.print(label, obj);

		// return the text
		return sw.getBuffer().toString();
	}

	/**
	 * private Constructor
	 */
	private Printer(StringWriter sw) {
		printWriter = new PrintWriter(sw);
		indent = 0;
		printProxies = new HashMap<String, PrintProxy>();
	}

	/**
	 * Print an object. Used by Printable classes to print their sub-components
	 */
	public void print(String label, Object obj) {
		print(label, obj, false);
	}

	/**
	 * Print an int.
	 */
	public void print(String label, int i) {
		print(label, new Integer(i), true);
	}

	/**
	 * Print a boolean.
	 */
	public void print(String label, boolean b) {
		print(label, Boolean.valueOf(b), true);
	}

	/**
	 * Print a long.
	 */
	public void print(String label, long l) {
		print(label, new Long(l), true);
	}

	/**
	 * Print a double.
	 */
	public void print(String label, double d) {
		print(label, new Double(d), true);
	}

	/**
	 * Print a float.
	 */
	public void print(String label, float f) {
		print(label, new Float(f), true);
	}

	/**
	 * Print an object. Used by Printable classes to print their sub-components
	 */
	private void print(String label, Object obj, boolean primitive) {
		// indent
		for (int i = 0; i < indent; i++)
			printWriter.print("    ");

		// print label
		if (label != null) {
			printWriter.print(label);
			printWriter.print(": ");
		}

		// print the class name
		if (obj != null) {
			printWriter.print("(");
			printWriter.print(getClassName(obj, primitive));
			printWriter.print(") ");
		}

		// print the object properly, depending on the type
		if (obj == null) {
			printWriter.println("null");
			return;
		}

		// See if there's a PrintProxy
		PrintProxy printProxy = getPrintProxy(obj.getClass());
		if (printProxy != null) {
			printWriter.println();
			++indent;
			printProxy.print(this, obj);
			--indent;
		}

		// Printable
		else if (obj instanceof Printable) {
			printWriter.println();
			++indent;
			((Printable) obj).print(this);
			--indent;
		}

		else if (obj instanceof String) {
			if (((String) obj).equals(""))
				printWriter.print("<empty>");
			else
				printWriter.print(obj);
			printWriter.println();
		}

		// Map
		else if (obj instanceof Map) {
			printWriter.println();

			Map map = (Map) obj;
			Object[] keys = map.keySet().toArray();
			for (int i = 0; i < keys.length; i++) {
				++indent;
				print("[" + keys[i].toString() + "]", map.get(keys[i]));
				--indent;
			}
		}

		// List
		else if (obj instanceof List) {
			printWriter.println();

			List v = (List) obj;
			for (int i = 0; i < v.size(); i++) {
				++indent;
				print("[" + i + "]", v.get(i));
				--indent;
			}
		}

		// Object Array
		else if (obj instanceof Object[]) {
			printWriter.println();

			Object[] array = (Object[]) obj;
			for (int i = 0; i < array.length; i++) {
				++indent;
				print("[" + i + "]", array[i]);
				--indent;
			}
		}

		// int Array
		else if (obj instanceof int[]) {
			printWriter.println();

			int[] array = (int[]) obj;
			for (int i = 0; i < array.length; i++) {
				++indent;
				print("[" + i + "]", array[i]);
				--indent;
			}
		}

		// long Array
		else if (obj instanceof long[]) {
			printWriter.println();

			long[] array = (long[]) obj;
			for (int i = 0; i < array.length; i++) {
				++indent;
				print("[" + i + "]", array[i]);
				--indent;
			}
		}

		// boolean Array
		else if (obj instanceof boolean[]) {
			printWriter.println();

			boolean[] array = (boolean[]) obj;
			for (int i = 0; i < array.length; i++) {
				++indent;
				print("[" + i + "]", array[i]);
				--indent;
			}
		}

		// char Array
		else if (obj instanceof char[]) {
			printWriter.println();

			char[] array = (char[]) obj;
			for (int i = 0; i < array.length; i++) {
				++indent;
				print("[" + i + "]", array[i]);
				--indent;
			}
		}

		// double Array
		else if (obj instanceof double[]) {
			printWriter.println();

			double[] array = (double[]) obj;
			for (int i = 0; i < array.length; i++) {
				++indent;
				print("[" + i + "]", array[i]);
				--indent;
			}
		}

		// just use toString()
		else
			printWriter.println(obj.toString());

	}

	/**
	 * Gets an object's class name, stripping off common prefixes.
	 */
	private String getClassName(Object obj, boolean primitive) {
		if (primitive) {
			if (obj instanceof Integer)
				return "int";
			if (obj instanceof Boolean)
				return "boolean";
			if (obj instanceof Long)
				return "long";
			if (obj instanceof Double)
				return "double";
			if (obj instanceof Float)
				return "float";
		}

		String className = obj.getClass().getName();
		if (className.startsWith("java.lang."))
			return className.substring("java.lang.".length());
		if (className.startsWith("java.util."))
			return className.substring("java.util.".length());
		return className;
	}

	/**
	 * Tries to find a PrintProxy for the given class. If there isn't one, it
	 * recursively tries the superclass.
	 */
	private PrintProxy getPrintProxy(Class cl) {
		if (cl == null)
			return null;

		// See if there's a PrintProxy for the class
		String className = cl.getName();
		PrintProxy printProxy = (PrintProxy) printProxies.get(className);
		if (printProxy != null)
			return printProxy;
		else
			return getPrintProxy(cl.getSuperclass());
	}

	/**
	 * Register a Printable to print objects of the given className
	 */
	public void setPrintableForClass(PrintProxy printProxy, String className) {
		printProxies.put(className, printProxy);
	}

}
