package com.aug3.sys.util;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 * This is a decorator for Context objects that will attempt a lookup twice
 * before passing on failures. All other methods are not changed.
 * 
 * @author xial
 */
public class RetryContext implements Context {

	/** number of lookup attempts before giving up */
	private static final int MAX_TRIES = 2;

	/** The decorated context object */
	private Context decoratedCtx;

	// ---------------------------------------------------------------------
	// CONSTRUCTORS
	// ---------------------------------------------------------------------
	public RetryContext(Context ctx) {
		decoratedCtx = ctx;
	}

	// ---------------------------------------------------------------------
	// PUBLIC METHODS
	// ---------------------------------------------------------------------
	/**
	 * Attempts to lookup the object MAX_TIMES before giving up. It is the case
	 * of JBoss that the first lookup to a restarted jboss jndi server will
	 * fail. Using this lookup code will prevent that from happening.
	 * 
	 * @param s
	 *            the jndi name of the object being retrieved.
	 * @return
	 * @throws NamingException
	 */
	public Object lookup(String s) throws NamingException {
		int tries = MAX_TRIES;
		Object lookupResult = null;
		while (tries > 0) {
			try {
				lookupResult = decoratedCtx.lookup(s);
				break;
			} catch (NamingException e) {
				tries--;
				if (tries == 0) {
					throw e;
				}
			}
		}
		return lookupResult;
	}

	// ---------------------------------------------------------------------
	// DELEGATED PUBLIC METHODS
	// ---------------------------------------------------------------------
	public void close() throws NamingException {
		decoratedCtx.close();
	}

	public String getNameInNamespace() throws NamingException {
		return decoratedCtx.getNameInNamespace();
	}

	public void destroySubcontext(String s) throws NamingException {
		decoratedCtx.destroySubcontext(s);
	}

	public void unbind(String s) throws NamingException {
		decoratedCtx.unbind(s);
	}

	public Hashtable getEnvironment() throws NamingException {
		return decoratedCtx.getEnvironment();
	}

	public void destroySubcontext(Name name) throws NamingException {
		decoratedCtx.destroySubcontext(name);
	}

	public void unbind(Name name) throws NamingException {
		decoratedCtx.unbind(name);
	}

	public Object lookupLink(String s) throws NamingException {
		return decoratedCtx.lookupLink(s);
	}

	public Object removeFromEnvironment(String s) throws NamingException {
		return decoratedCtx.removeFromEnvironment(s);
	}

	public void bind(String s, Object o) throws NamingException {
		decoratedCtx.bind(s, o);
	}

	public void rebind(String s, Object o) throws NamingException {
		decoratedCtx.rebind(s, o);
	}

	public Object lookup(Name name) throws NamingException {
		return decoratedCtx.lookup(name);
	}

	public Object lookupLink(Name name) throws NamingException {
		return decoratedCtx.lookupLink(name);
	}

	public void bind(Name name, Object o) throws NamingException {
		decoratedCtx.bind(name, o);
	}

	public void rebind(Name name, Object o) throws NamingException {
		decoratedCtx.rebind(name, o);
	}

	public void rename(String s, String s1) throws NamingException {
		decoratedCtx.rename(s, s1);
	}

	public Context createSubcontext(String s) throws NamingException {
		return decoratedCtx.createSubcontext(s);
	}

	public Context createSubcontext(Name name) throws NamingException {
		return decoratedCtx.createSubcontext(name);
	}

	public void rename(Name name, Name name1) throws NamingException {
		decoratedCtx.rename(name, name1);
	}

	public NameParser getNameParser(String s) throws NamingException {
		return decoratedCtx.getNameParser(s);
	}

	public NameParser getNameParser(Name name) throws NamingException {
		return decoratedCtx.getNameParser(name);
	}

	public NamingEnumeration list(String s) throws NamingException {
		return decoratedCtx.list(s);
	}

	public NamingEnumeration listBindings(String s) throws NamingException {
		return decoratedCtx.listBindings(s);
	}

	public NamingEnumeration list(Name name) throws NamingException {
		return decoratedCtx.list(name);
	}

	public NamingEnumeration listBindings(Name name) throws NamingException {
		return decoratedCtx.listBindings(name);
	}

	public Object addToEnvironment(String s, Object o) throws NamingException {
		return decoratedCtx.addToEnvironment(s, o);
	}

	public String composeName(String s, String s1) throws NamingException {
		return decoratedCtx.composeName(s, s1);
	}

	public Name composeName(Name name, Name name1) throws NamingException {
		return decoratedCtx.composeName(name, name1);
	}

}
