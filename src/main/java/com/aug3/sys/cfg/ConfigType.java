package com.aug3.sys.cfg;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

import com.aug3.sys.util.Printable;
import com.aug3.sys.util.Printer;
import com.aug3.sys.util.StringUtil;

/**
 * Each type of configuration data is represented by a ConfigType object. For
 * example, workflow templates, appconfig.properties and notification
 * definitions all have their own ConfigType.
 * <p>
 * 
 * The type definitions are defined in the ConfigTypes.xml file. The file
 * defines all the configuration types in the system. It is read at system
 * startup time. The <code>StorageAdapterProxy</code> keeps type mapping and
 * dispatch lookup calls to the storage adapter implementation.
 * <p>
 * 
 * @see StorageAdapterProxy
 */
@SuppressWarnings("serial")
public class ConfigType implements Serializable, Printable {

	private String name;
	private String displayName;
	private boolean loadAll;
	private boolean editable;
	private String fileValueGetterClassName;
	private String dbValueGetterClassName;
	private String ldapValueGetterClassName;
	private String fileValueSetterClassName;
	private String dbValueSetterClassName;
	private String ldapValueSetterClassName;
	private int storageType;

	private final static String TAG_NAME = "name";
	private final static String TAG_DISP_NAME = "displayName";
	private final static String TAG_STORAGE_TYPE = "defaultStorage";
	private final static String TAG_LOAD_ALL = "loadAll";
	private final static String TAG_EDITABLE = "editable";
	private final static String TAG_FILE_VALUE_GETTER = "fileValueGetter";
	private final static String TAG_DB_VALUE_GETTER = "dbValueGetter";
	private final static String TAG_LDAP_VALUE_GETTER = "ldapValueGetter";
	private final static String TAG_FILE_VALUE_SETTER = "fileValueSetter";
	private final static String TAG_DB_VALUE_SETTER = "dbValueSetter";
	private final static String TAG_LDAP_VALUE_SETTER = "ldapValueSetter";

	private static Map<String, Integer> STORAGE_TYPES = new HashMap<String, Integer>();

	// static initializer
	static {
		STORAGE_TYPES.put("database",
				Integer.valueOf(ConfigConstants.STORAGE_TYPE_DB));
		STORAGE_TYPES.put("file",
				Integer.valueOf(ConfigConstants.STORAGE_TYPE_FILE));
		STORAGE_TYPES.put("ldap",
				Integer.valueOf(ConfigConstants.STORAGE_TYPE_LDAP));
	}

	// ==========================================================================
	// constructors
	// ==========================================================================
	/**
	 * Constructor taking an XML Element
	 */
	ConfigType(Element elem) {
		// retrieve attributes for the config type element
		name = elem.getAttribute(TAG_NAME);
		displayName = elem.getAttribute(TAG_DISP_NAME);
		fileValueGetterClassName = getAttrEmptyNull(elem, TAG_FILE_VALUE_GETTER);
		dbValueGetterClassName = getAttrEmptyNull(elem, TAG_DB_VALUE_GETTER);
		ldapValueGetterClassName = getAttrEmptyNull(elem, TAG_LDAP_VALUE_GETTER);
		fileValueSetterClassName = getAttrEmptyNull(elem, TAG_FILE_VALUE_SETTER);
		dbValueSetterClassName = getAttrEmptyNull(elem, TAG_DB_VALUE_SETTER);
		ldapValueSetterClassName = getAttrEmptyNull(elem, TAG_LDAP_VALUE_SETTER);
		loadAll = Boolean.parseBoolean(getAttrEmptyNull(elem, TAG_LOAD_ALL));
		editable = Boolean.parseBoolean(getAttrEmptyNull(elem, TAG_EDITABLE));

		// set default for display name
		if (StringUtil.isBlank(displayName))
			displayName = name;

		// get the default storage type
		String st = getAttrEmptyNull(elem, TAG_STORAGE_TYPE);
		Integer iType = null;

		if (st != null)
			iType = STORAGE_TYPES.get(st.toLowerCase());

		storageType = null == iType ? ConfigConstants.STORAGE_TYPE_FILE : iType
				.intValue();

		// get the editable flag
		String se = getAttrEmptyNull(elem, TAG_EDITABLE);

		if (se != null)
			editable = Boolean.valueOf(se).booleanValue();
	}

	// ==========================================================================
	// public methods
	// ==========================================================================
	/**
	 * Returns the name of the configuration type
	 * 
	 * @return the name of the configuration type.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the display name of the configuration type
	 * 
	 * @return the display name of the configuration type.
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Returns flag to indicate whether this configuration type is editable from
	 * the UI.
	 * 
	 * @return editable flag
	 */
	public boolean getEditable() {
		return editable;
	}

	/**
	 * Printable interface method. The object writes itself to the Printer.
	 * 
	 * @param printer
	 */
	public void print(Printer printer) {
		printer.print("name", name);
		printer.print("displayName", displayName);
		printer.print("loadAll", loadAll);
		printer.print("editable", editable);
		printer.print("fileValueGetterClassName", fileValueGetterClassName);
		printer.print("dbValueGetterClassName", dbValueGetterClassName);
		printer.print("ldapValueGetterClassName", ldapValueGetterClassName);
		printer.print("fileValueSetterClassName", fileValueSetterClassName);
		printer.print("dbValueSetterClassName", dbValueSetterClassName);
		printer.print("ldapValueSetterClassName", ldapValueSetterClassName);
		printer.print("storageType", storageType);
	}

	// ==========================================================================
	// non-public methods
	// ==========================================================================
	/**
	 * Returns an attribute value from an XML element, converting "" to null.
	 * 
	 * @param e
	 *            - an XML element
	 * @param a
	 *            - the attribute name in the XML element
	 * @return the attribute value
	 */
	String getAttrEmptyNull(Element e, String attr) {
		String val = e.getAttribute(attr);
		return StringUtil.isBlank(val) ? null : val;
	}

	// ==========================================================================
	// member accessors
	// ==========================================================================
	/** Returns flag indicating whether all values are loaded at once */
	boolean getLoadAll() {
		return loadAll;
	}

	/** Returns the file version of the value getter implementation class */
	String getFileValueGetterClassName() {
		return fileValueGetterClassName;
	}

	/** Returns the db version of the value getter implementation class */
	String getDbValueGetterClassName() {
		return dbValueGetterClassName;
	}

	/** Returns the LDAP version of the value getter implementation class */
	String getLdapValueGetterClassName() {
		return ldapValueGetterClassName;
	}

	/** Returns the file version of the value setter implementation class */
	String getFileValueSetterClassName() {
		return fileValueSetterClassName;
	}

	/** Returns the db version of the value setter implementation class */
	String getDbValueSetterClassName() {
		return dbValueSetterClassName;
	}

	/** Returns the LDAP version of the value setter implementation class */
	String getLdapValueSetterClassName() {
		return ldapValueSetterClassName;
	}

	/** Returns the storage medium type */
	int getStorageType() {
		return storageType;
	}
}