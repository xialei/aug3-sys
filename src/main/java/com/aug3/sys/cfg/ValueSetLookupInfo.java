package com.aug3.sys.cfg;

import java.io.Serializable;

import com.aug3.sys.AppContext;

/**
 * This class contains parameters for looking up configuration values.
 * 
 * @author xial
 */
@SuppressWarnings("serial")
public class ValueSetLookupInfo extends AppContext implements Serializable {

	// note, keep this member protected, the extended classes need access to it
	protected String keyString;

	private String configTypeName;
	private int storageType;

	// ==========================================================================
	// constructors
	// ==========================================================================
	/**
	 * Copy constructor
	 * */
	public ValueSetLookupInfo(ValueSetLookupInfo li) {
		super(li);
		this.keyString = li.keyString;
		this.configTypeName = li.configTypeName;
		this.storageType = li.storageType;
	}

	/**
	 * constructor that takes AppContext, and the configuration type
	 */
	public ValueSetLookupInfo(AppContext ctx, String configTypeName) {
		this(ctx, configTypeName, ConfigConstants.STORAGE_TYPE_UNKNOWN);
	}

	/**
	 * constructor that takes AppContext, the configuration type, and the
	 * storage type
	 */
	public ValueSetLookupInfo(AppContext ctx, String sConfigTypeName,
			int iStorageType) {
		super(ctx);

		// TODO: unify the key string to avoid creating duplicated ValueSet in
		// cache
		configTypeName = sConfigTypeName;
		if (configTypeName == null) {
			configTypeName = "";
		}

		String orgName = getOrgName();
		StringBuffer keySB = new StringBuffer();
		keySB.append(configTypeName);
		keySB.append(ConfigConstants.KEY_SEP);
		if (!orgName.trim().equals("")) {
			keySB.append(ConfigConstants.KEY_SEP);
			keySB.append(orgName);
		}

		keyString = keySB.toString();

		storageType = iStorageType;

	}

	/**
	 * constructor from config type and org name
	 */
	public ValueSetLookupInfo(String configTypeName, String orgName) {
		this(configTypeName, orgName, ConfigConstants.STORAGE_TYPE_UNKNOWN);
	}

	/**
	 * constructor from config type, org name, and storage type.
	 */
	public ValueSetLookupInfo(String configTypeName, String orgName,
			int storageType) {
		this(new AppContext(orgName), configTypeName, storageType);
	}

	// ==========================================================================
	// override for cache key
	// ==========================================================================
	/**
	 * Overrides equals() method for hashing
	 */
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof ValueSetLookupInfo))
			return false;

		// compare key strings
		return ((ValueSetLookupInfo) obj).getKeyString().equals(getKeyString());
	}

	/**
	 * Overrides hashCode() method for hashing
	 */
	public int hashCode() {
		return getKeyString().hashCode();
	}

	// ==========================================================================
	// Getter/Setter
	// ==========================================================================
	/** Returns the configuration type name of this lookup object */
	public String getConfigTypeName() {
		return configTypeName;
	}

	/** Returns the organization name of this lookup object */
	public String getOrgName() {
		return super.getOrganization();
	}

	/** Returns the storage medium type of this lookup object */
	public int getStorageType() {
		return storageType;
	}

	/** Returns the cache key string for cache management */
	public String getKeyString() {
		return keyString;
	}

	/** Sets the storage medium type of this lookup object */
	public void setStorageType(int iStorageType) {
		storageType = iStorageType;
	}

	// ======================================================================
	// HELPER METHODS
	// ======================================================================

	/**
	 * Returns this instance's key string, qualified with the entry key value.
	 */
	String getKeyString(String objectKey) {
		return keyString + "#" + objectKey;
	}
}
