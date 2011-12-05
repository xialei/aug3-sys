package com.aug3.sys.cfg;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.aug3.sys.util.Printable;
import com.aug3.sys.util.Printer;

/**
 * This class is used to return a set of configuration values. Retrieving the
 * whole set saves time in accessing the backing storage and in shipping the
 * values cross-wire. Client can then retrieve individual values from the set by
 * specifying the configuration name.
 * <p>
 * 
 * @see ValueSetter
 * 
 * @author xial
 */
@SuppressWarnings("serial")
public class ValueSet implements Printable, Serializable {

	private String configTypeName;

	private Map<String, Object> valueMap = new HashMap<String, Object>();
	private Map<String, String> descMap = new HashMap<String, String>();
	private List<Object> valueList = new Vector<Object>();
	private List<String> nameList = new Vector<String>();
	private Set<String> resetSet = new HashSet<String>();
	private Set<String> updateSet = new HashSet<String>();

	// ==========================================================================
	// constructors
	// ==========================================================================
	/**
	 * constructor taking the configuration type name and the retrieving flag.
	 * <p>
	 * This is usually used by the storage adapter only.
	 * <p>
	 * 
	 * @param configTypeName
	 *            - the configure type name
	 */
	public ValueSet(String configTypeName) {
		this.configTypeName = configTypeName;
	}

	/**
	 * Simple constructors, supports inheritance as necessary.
	 */
	protected ValueSet() {
	}

	// ==========================================================================
	// public methods
	// ==========================================================================
	/**
	 * Updates a value to the set. Supposedly the updates will eventually get
	 * saved to the backing storage.
	 * <p>
	 * 
	 * This is usually used by the configuration UI.
	 * <p>
	 * 
	 * @param key
	 *            - the configuration key
	 * @param val
	 *            - the configuration value
	 */
	public void updateValue(String key, Object val) {
		updateSet.add(key);
		putValue(key, val);
	}

	/**
	 * Resets a configuration value to its default. Usually, when a
	 * configuration value is updated, the new value is saved to a company
	 * specific storage. This reset will take the updated value from the company
	 * specific storage so the lookup will return the default value.
	 * <p>
	 * 
	 * This is usually used by the configuration UI.
	 * <p>
	 * 
	 * @param key
	 *            - the configuration key
	 */
	public void resetValue(String key) {
		resetSet.add(key);
	}

	/**
	 * Returns the set of keys that have their associated values updated.
	 * <p>
	 * This is usually used by the storage adapter only.
	 * <p>
	 * 
	 * @return the key set
	 */
	public Set<String> getUpdateSet() {
		return updateSet;
	}

	/**
	 * Returns the set of keys that have their associated values reset to
	 * default.
	 * <p>
	 * 
	 * This is usually used by the storage adapter only.
	 * <p>
	 * 
	 * @return the key set
	 */
	public Set<String> getResetSet() {
		return resetSet;
	}

	/**
	 * Adds a value to the set.
	 * <p>
	 * 
	 * This is usually used by the storage adapter only.
	 * <p>
	 * 
	 * @param key
	 *            - the configuration key
	 * @param val
	 *            - the configuration value
	 */
	public void putValue(String key, Object val) {
		putValue(key, val, "");
	}

	/**
	 * Adds a value to the set.
	 * <p>
	 * 
	 * This is usually used by the storage adapter only.
	 * <p>
	 * 
	 * @param key
	 *            - the configuration key
	 * @param val
	 *            - the configuration value
	 * @param desc
	 *            - the description of this value
	 */
	public void putValue(String key, Object val, String desc) {
		descMap.put(key, desc);

		// if the name wasn't already in the set, add it to the end of the List
		Object oldValue = valueMap.put(key, val);

		if (null == oldValue) {
			nameList.add(key);
			valueList.add(val);
		} else {
			// if the name was already in the set, replace it in the List
			for (int i = 0; i < nameList.size(); i++) {
				if (key.equals(nameList.get(i))) {
					valueList.set(i, val);
					break;
				}
			}
		}
	}

	/**
	 * Adds an integer value to the set.
	 * <p>
	 * 
	 * This is usually used by the storage adapter only.
	 * <p>
	 * 
	 * @param key
	 *            - the configuration key
	 * @param val
	 *            - the configuration value
	 */
	public void putValue(String key, int val) {
		putValue(key, new Integer(val));
	}

	/**
	 * Adds an long value to the set.
	 * <p>
	 * 
	 * This is usually used by the storage adapter only.
	 * <p>
	 * 
	 * @param key
	 *            - the configuration key
	 * @param val
	 *            - the configuration value
	 */
	public void putValue(String key, long val) {
		putValue(key, new Long(val));
	}

	/**
	 * Adds an boolean value to the set.
	 * <p>
	 * 
	 * This is usually used by the storage adapter only.
	 * <p>
	 * 
	 * @param key
	 *            - the configuration key
	 * @param val
	 *            - the configuration value
	 */
	public void putValue(String key, boolean val) {
		putValue(key, Boolean.valueOf(val));
	}

	/**
	 * Returns the value for the given name. The type of object returned depends
	 * on the ValueSet's ConfigType.
	 * <p>
	 * 
	 * Here is an example usage:
	 * 
	 * <pre>
	 * 
	 *  ValueSet dtProps = . . .
	 *  String traceLevel = (String) dtProps.getValue("trace.level");
	 * </pre>
	 * <p>
	 * 
	 * @param key
	 *            - the key of the configuration
	 * @return the configuration value
	 */
	public Object getValue(String key) {
		return valueMap.get(key);
	}

	/**
	 * Returns the description of the named value for the given name.
	 * <p>
	 * 
	 * @param key
	 *            - the key of the configuration
	 * @return the description of the named value
	 */
	public String getDescription(String key) {
		return (String) descMap.get(key);
	}

	/**
	 * Returns a List[Object] containing all the values. For some configure types,
	 * the order matters.
	 * <p>
	 * 
	 * @return the list of configuration values
	 */
	public List<Object> getAllValues() {
		return valueList;
	}

	/**
	 * Returns a List[String] containing all the value names. Corresponds to
	 * getAllValues().
	 * <p>
	 * 
	 * @return the keys in the set
	 */
	public List<String> getAllNames() {
		return nameList;
	}

	/**
	 * Returns the configuration type name associated with the configuration
	 * value set.
	 * <p>
	 * 
	 * @return the name of the configuration type
	 */
	public String getConfigTypeName() {
		return configTypeName;
	}

	/**
	 * Remove the value based on the key
	 * 
	 * @param key
	 *            - the key of the configuration
	 */
	public void removeValue(String key) {
		descMap.remove(key);
		resetSet.remove(key);

		// if the name wasn't already in the set, add it to the end of the List
		valueMap.remove(key);
		// if the name was already in the set, replace it in the List
		for (int i = 0; i < nameList.size(); i++) {
			if (key.equals(nameList.get(i))) {
				nameList.remove(i);
				valueList.remove(i);
				break;
			}
		}

	}

	/**
	 * Printable interface method. The object writes itself to the Printer.
	 * <p>
	 * 
	 * @param printer
	 */
	public void print(Printer printer) {
		printer.print("configTypeName", configTypeName);
		printer.print("valueMap", valueMap);
		printer.print("valueList", valueList);
		printer.print("nameList", nameList);
	}

	/**
	 * @return a map of all the key<-> value pairs in the table.
	 */
	public Map<String, Object> getValueMap() {
		return valueMap;
	}
}
