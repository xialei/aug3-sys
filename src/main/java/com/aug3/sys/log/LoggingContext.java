package com.aug3.sys.log;

import com.aug3.sys.AppContext;
import com.aug3.sys.CommonException;
import com.aug3.sys.util.StringUtil;

/**
 * The purpose of this class is to provide a light DTO to pass message
 * attributes the MLogger (log4j) logging framework.
 * 
 * @author xial
 */
public class LoggingContext {

	// Constants representing message tokens
	private static final String TOKEN_DELIM = ":"; // Delimiter
	private static final String TOKEN_CONTEXT = "%s"; //

	private static MLogger log = null;

	/**
	 * Module-level variables
	 */
	// Default message: This is used if the message is not defined in the
	// properties file.
	public static String MSG_DEFAULTMASK = TOKEN_CONTEXT;
	public static final int DEFAULT_INTEGER = 0;
	private transient int m_iMessageID = DEFAULT_INTEGER;
	private transient String m_strCustomer = "";
	private transient String m_strUser = "";
	private transient String m_strComponent = "";
	private transient String m_strSubComponent = "";
	private transient String m_strCategory = "";
	private transient String m_strMessageDetails = "";

	LoggingContext(AppContext ctx) {
		if (ctx == null)
			return;
		setCustomer(ctx.getOrganization());
		// User user = ctx.getUser();
		// if (user != null)
		// setUser(user.getMail());
	}

	/**
	 * Set a lookup error code. This message code is a key into the logging
	 * messages property file. If a message code is not found, a default message
	 * will be used.
	 */
	public void setMessageID(int val) {
		m_iMessageID = val;
		// TODO: Perform a properties lookup and populate more attributes

	}

	/**
	 * Get the current lookup error code.
	 */
	public int getMessageID() {
		return m_iMessageID;
	}

	public void setMessageCategory(String val) {
		m_strCategory = val;
	}

	public String getMessageCategory() {
		return StringUtil.coalesce(m_strCategory, "");
	}

	/**
	 * Setter: This attribute is set if you created this instance with a
	 * DTContext.
	 */
	public void setCustomer(String val) {
		m_strCustomer = val;
	}

	/**
	 * Getter: This attribute is set if you created this instance with a
	 * DTContext.
	 */
	public String getCustomer() {
		return StringUtil.coalesce(m_strCustomer, "");
	}

	/**
	 * Setter: This attribute is set if you created this instance with a
	 * DTContext.
	 */
	public void setUser(String val) {
		m_strUser = val;
	}

	/**
	 * Getter: This attribute is set if you created this instance with a
	 * DTContext.
	 */
	public String getUser() {
		return StringUtil.coalesce(m_strUser, "");
	}

	public void setComponent(String val) {
		m_strComponent = val;
	}

	public String getComponent() {
		return StringUtil.coalesce(m_strComponent, "");
	}

	public void setSubComponent(String val) {
		m_strSubComponent = val;
	}

	public String getSubComponent() {
		return StringUtil.coalesce(m_strSubComponent, "");
	}

	protected void setMessageDetails(String val) {
		m_strMessageDetails = val;
	}

	protected String getMessageDetails() {
		return StringUtil.coalesce(m_strMessageDetails, "");
	}

	public String toString() {
		return getFormattedMessage();
	}

	public String getFormattedMessage() {
		StringBuffer sb = null;
		// Fetch template
		String strMsg = getMask(getMessageID());
		if (strMsg == null) {
			// No message found in lookup so just print passed in message
			strMsg = getMessageDetails();
		} else {
			// Build %s string
			sb = new StringBuffer();
			sb.append(getMessageID() + TOKEN_DELIM);
			sb.append(getMessageCategory() + TOKEN_DELIM);
			sb.append(getCustomer() + TOKEN_DELIM);
			sb.append(getUser() + TOKEN_DELIM);
			sb.append(getComponent() + TOKEN_DELIM);
			sb.append(getSubComponent() + TOKEN_DELIM);
			// Replace token in return string
			strMsg = strMsg.replaceAll(TOKEN_CONTEXT, sb.toString());
			// Append message details
			if (!getMessageDetails().equals(""))
				strMsg = strMsg + TOKEN_DELIM + getMessageDetails();
		}
		return strMsg;
	}

	/**
	 * Internal helper to fetch a message mask
	 */
	private String getMask(int iMessageID) {
		LogProperties prop = null;
		String strVal = null;
		try {
			prop = LogProperties.getInstance();
		} catch (CommonException e) {
			warn(e.getMessage(), e);
		}
		if (prop != null)
			strVal = prop.getProperty(iMessageID);
		return strVal;
	}

	private static void warn(String msg, Exception e) {
		if (log == null)
			log = MLogger.getLog(LoggingContext.class);
		log.error(msg);
	}

}
