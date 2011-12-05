package com.aug3.sys.action;

import java.util.Map;

/**
 * An implementation of Action that does nothing. Very useful when you need to
 * stick an action somewhere but don't want anything to happen.
 * 
 * @author xial
 */
public class NullAction extends AbstractAction {

	/**
	 * This method does absolutely nothing. Really. It's happy just being. 
	 */
	public Object perform(Map params) {
		return null;
	}
}
