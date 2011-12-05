package com.aug3.sys;

import java.io.Serializable;
import java.util.HashMap;

import com.aug3.sys.util.StringUtil;

/**
 * This class is used to carry information through out a HTTP session request.
 * For example for a request that needs execute a database transaction, the user
 * information who initiated the request is needed to obtain a JDBC connection.
 * This class can carry the user information through EJB invocations all the way
 * to the database. This technique is generally much more efficient and flexible
 * than using session EJBs. It can also be used as a transient cache through the
 * request.
 * <p>
 * 
 * All infrastructure APIs, such as workflow engine or EJB implementations, are
 * required to take <code>AppContext</code> as mandatory argument.
 * <p>
 * 
 * Note also that all values saved into the context must be serializable,
 * because it's passed through remote APIs.
 * <p>
 * 
 * @author xial
 */
public class AppContext extends HashMap implements Cloneable, Serializable {

	private static final long serialVersionUID = 2978681637843656022L;

	/**
	 * The default organization for AppContext. corresponds to track-level
	 * information.
	 */
	private static final String DEFAULT_ORG = AppConstants.DEFAULT_ORGANIZATION;

	private String _orgID;
	private int _appID;
	private User _user;
	private String _sessionID;
	private String _track;

	// ==========================================================================
	// constructors
	// ==========================================================================
	/** default constructor */
	public AppContext() {
		this(DEFAULT_ORG);
	}

	/**
	 * constructor that takes organization (company) name
	 * 
	 * @param orgID
	 *            the organization (company) name
	 */
	public AppContext(String orgID) {
		this(orgID, 0);
	}

	/**
	 * constructor that takes organization (company) name, and application ID
	 * 
	 * @param orgID
	 *            the organization (company) name
	 * @param appID
	 *            the application ID (price, promo, assortment, etc.)
	 */
	public AppContext(String orgID, int appID) {
		setOrganization(orgID);
		_appID = appID;
		_track = AppSystem.getTrackName();
		assert _track != null : "track name should always be defined!";
	}

	/**
	 * constructor that takes another AppContext, sort of like a clone.
	 * 
	 * @param ctx
	 *            another AppContext
	 */
	public AppContext(AppContext ctx) {
		_orgID = ctx._orgID;
		_appID = ctx._appID;
		_user = ctx._user;
		_sessionID = ctx._sessionID;
		_track = ctx._track;
	}

	public AppContext(String org, Long userStr) {
		setOrganization(org);
		User user = new User();
		user.setId(userStr);
		this.setUser(user);
	}

	// ==========================================================================
	// public methods
	// ==========================================================================
	/**
	 * Creates and returns a copy of this object. It does copy of all the
	 * properties that have explicit accessor method. And then it use the
	 * default clone of <code>HashMap</code> to clone the custom properties.
	 */
	public Object clone() {
		return super.clone();
	}

	/**
	 * Gets the user object which should be serializable. The onus is on the
	 * subsystem using the method to cast the user object accordingly. This
	 * abstraction is necessary so that cyclic build dependencies are avoided.
	 * 
	 * @return a Serializable user Object
	 */
	public User getUser() {
		return _user;
	}

	/**
	 * Sets the user object in the context
	 * 
	 * @param user
	 *            a Serializable Object
	 */
	public void setUser(User user) {
		_user = user;
	}

	/**
	 * Gets the session ID string
	 * 
	 * @return String session ID
	 */
	public String getSessionID() {
		return _sessionID;
	}

	/**
	 * Sets the session ID in the context
	 * 
	 * @param sessionID
	 *            - a String parameter got from the HTTP session object
	 */
	public void setSessionID(String sessionID) {
		_sessionID = sessionID;
	}

	/**
	 * Returns the organization (company) name stored with this context. If no
	 * organization is associated with this context, but a user is, returns that
	 * user's organization.
	 * 
	 * @return the organization (company) name
	 */
	public String getOrganization() {
		return StringUtil.isBlank(_orgID) ? ((getUser() != null) ? getUser()
				.getOrgId().toString() : DEFAULT_ORG) : _orgID;
	}

	/**
	 * Sets the organization (company) name to this context
	 * 
	 * @param orgID
	 *            the organization (company) name
	 */
	public void setOrganization(String orgID) {
		_orgID = (orgID == null) ? DEFAULT_ORG : orgID;
	}

	/**
	 * Returns the application ID (price, promo, and assortment, etc.) stored
	 * with this context
	 * 
	 * @return the application ID
	 */
	public int getAppID() {
		return _appID;
	}

	/**
	 * Sets the application ID to this context
	 * 
	 * @param appID
	 *            the application ID
	 */
	public void setAppID(int appID) {
		_appID = appID;
	}

	public String getTrackName() {
		return this._track;
	}

	/**
	 * Returns true when this context is not associated with a company. A
	 * context associated with a company will have a valid company name. one
	 * that is not associated has a value of null, "" or "defaultcompany.com"
	 * 
	 * @return true if this context is NOT associated with a company.
	 * @return
	 */
	public boolean isTrackLevel() {
		String o = this.getOrganization();
		return (o == null) || (o.equals(DEFAULT_ORG));
	}
}
