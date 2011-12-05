package com.aug3.sys.util;

/**
 * Object which can print other objects implement this
 */
public interface PrintProxy {
	/**
	 * Write the object to the Printer.
	 */
	void print(Printer printer, Object obj);
}