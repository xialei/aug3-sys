package com.aug3.sys.cfg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * This is the message object used by the configuration update <code>
 * UpdateNotifier</code>. The message contains information for matching client
 * callbacks. The callbacks get invoked only if the message contents matches.
 * <p>
 * 
 * When client registered callbacks, they have an option to register it with a
 * matcher. The matcher is actually an <code>UpdateNoficication </code> too. If
 * a callback doesn't have a matcher, it gets invoked no matter what. If the
 * matcher has a matcher, the matcher will be used. Clients do not need to
 * specify all values in the matcher. The matching logic will only match those
 * values that are set. Simply put, the more values been specified, the match
 * will be more specific and the callback will less likely gets invoked.
 * <p>
 * Note that clients can also specify some custom values too with the message.
 * The custom values are not used for matching. They are passed to the callbacks
 * for custom processing logics.
 * <p>
 * 
 * @author xial
 */
@SuppressWarnings("serial")
public class UpdateNotification implements Serializable {

	// ==========================================================================
	// constants
	// ==========================================================================

	public static final long CONFIG_CHANGE_CODE = 1111;
	public static final long SERVER_CHANGE_CODE = 2222;

	// ==========================================================================
	// private members
	// ==========================================================================
	private String hostName;
	private String procName;
	private String configType;
	private String callbackToken;
	private int procInstance = -1;
	private List<Integer> procType = new ArrayList<Integer>();
	private String customStrVal1;
	private String customStrVal2;
	private long customLongVal1;
	private long customLongVal2;
	private String[] customStrArrayVal;

	// ==========================================================================
	// public methods
	// ==========================================================================
	/**
	 * Given a callback registration, check whether the registration matches.
	 * This is used by the <code>UpdateNotifier<code> to determine whether to
	 * invoke a registered callback.<p>
	 * 
	 * @return whether this callback should be invoked.
	 */
	boolean match(UpdateNotification un) {
		// match target host name
		if (null != un.hostName && !un.hostName.equalsIgnoreCase(hostName)) {
			return false;
		}

		// match target process name
		if (null != un.procName && !un.procName.equalsIgnoreCase(procName)) {
			return false;
		}

		// match target config type
		if (null != un.configType
				&& !un.configType.equalsIgnoreCase(configType)) {
			return false;
		}

		// match the token registered with the callback
		if (null != un.callbackToken
				&& !un.callbackToken.equalsIgnoreCase(callbackToken)) {
			return false;
		}

		// match the target DTProc type
		if ((!un.procType.isEmpty()) && (!procType.containsAll(un.procType))) {
			return false;
		}

		// match the target DTProc instance
		if (-1 != un.procInstance && un.procInstance != procInstance) {
			return false;
		}

		return true;
	}

	// ==========================================================================
	// member accessors
	// ==========================================================================
	public void setHostName(String val) {
		hostName = val;
	}

	public void setProcName(String val) {
		procName = val;
	}

	public void setConfigType(String val) {
		configType = val;
	}

	public void setCallbackToken(String val) {
		callbackToken = val;
	}

	public void setProcType(Integer... vals) {
		procType = Arrays.asList(vals);
	}

	public void setProcType(List<Integer> val) {
		procType = val;
	}

	public void setProcInstance(int val) {
		procInstance = val;
	}

	/**
	 * Sets a custom string value. The value is not used for matching callback
	 * invocation. It is passed to the callback for custom processing logic.
	 */
	public void setCustomStrVal1(String val) {
		customStrVal1 = val;
	}

	/**
	 * @return a custom string value. The value is not used for matching
	 *         callback invocation. It is passed to the callback for custom
	 *         processing logic.
	 */
	public String getCustomStrVal1() {
		return customStrVal1;
	}

	/**
	 * Sets a custom string value. The value is not used for matching callback
	 * invocation. It is passed to the callback for custom processing logic.
	 */
	public void setCustomStrVal2(String val) {
		customStrVal2 = val;
	}

	/**
	 * @return a custom string value. The value is not used for matching
	 *         callback invocation. It is passed to the callback for custom
	 *         processing logic.
	 */
	public String getCustomStrVal2() {
		return customStrVal2;
	}

	/**
	 * Sets a custom long value. The value is not used for matching callback
	 * invocation. It is passed to the callback for custom processing logic.
	 */
	public void setCustomLongVal1(long val) {
		customLongVal1 = val;
	}

	/**
	 * @return a custom long value. The value is not used for matching callback
	 *         invocation. It is passed to the callback for custom processing
	 *         logic.
	 */
	public long getCustomLongVal1() {
		return customLongVal1;
	}

	/**
	 * Sets a custom long value. The value is not used for matching callback
	 * invocation. It is passed to the callback for custom processing logic.
	 */
	public void setCustomLongVal2(long val) {
		customLongVal2 = val;
	}

	/**
	 * @return a custom long value. The value is not used for matching callback
	 *         invocation. It is passed to the callback for custom processing
	 *         logic.
	 */
	public long getCustomLongVal2() {
		return customLongVal2;
	}

	/**
	 * Sets a custom string array value. The value is not used for matching
	 * callback invocation. It is passed to the callback for custom processing
	 * logic.
	 */
	public void setCustomStrArrayVal(String[] val) {
		customStrArrayVal = val;
	}

	/**
	 * @return a custom string array value. The value is not used for matching
	 *         callback invocation. It is passed to the callback for custom
	 *         processing logic.
	 */
	public String[] getCustomStrArrayVal() {
		return customStrArrayVal;
	}
}
