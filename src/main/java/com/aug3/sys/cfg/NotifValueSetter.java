package com.aug3.sys.cfg;

/**
 * NotifValueSetter is a decorator for ValueSetter objects that adds update
 * notification to it.
 * 
 * @author xial
 */
class NotifValueSetter implements ValueSetter {

	// ---------------------------------------------------------------------
	// INSTANCE FIELDS
	// ---------------------------------------------------------------------

	private ValueSetter writer;

	// ---------------------------------------------------------------------
	// CONSTRUCTORS
	// ---------------------------------------------------------------------

	NotifValueSetter(ValueSetter writer) {
		this.writer = writer;
	}

	// ---------------------------------------------------------------------
	// PUBLIC METHODS
	// ---------------------------------------------------------------------

	public synchronized void setValueSet(ValueSetLookupInfo li, ValueSet vs)
			throws Exception {
		writer.setValueSet(li, vs);
		updateNotify(li);
	}

	public synchronized void setValue(ValueSetLookupInfo li, String key,
			Object val) throws Exception {
		writer.setValue(li, key, val);
		updateNotify(li);
	}

	// ---------------------------------------------------------------------
	// HELPER METHODS
	// ---------------------------------------------------------------------

	/**
	 * Sends an update notification where the custom string1 value is set to the
	 * ValueSetLookup info's key.
	 * 
	 * @param li
	 *            the lookup info for the updated valueset.
	 * @throws ConfigException
	 */
	private void updateNotify(ValueSetLookupInfo li) throws ConfigException {
		UpdateNotification notif = new UpdateNotification();
		notif.setCustomLongVal1(UpdateNotification.CONFIG_CHANGE_CODE);
		notif.setCustomStrVal1(li.getKeyString());
		UpdateNotifier.publish(notif);
	}
}
