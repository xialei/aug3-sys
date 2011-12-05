package com.aug3.sys.action;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents an action configuration entry. It contains the
 * implementation class name of the action and a map of parameters.
 * 
 * @author xial
 */
public class ActionDef {

	private String implName;
	private Map params;

	public ActionDef() {
		params = new HashMap();
	}

	public ActionDef(String implName, Map params) {
		this.implName = implName;
		this.params = params;
	}

	public void put(String name, String val) {
		params.put(name, val);
	}

	@Override
	public int hashCode() {
		return implName.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof ActionDef && equals((ActionDef) o);
	}

	public boolean equals(ActionDef def) {
		return implName.equals(def.implName) && params.equals(def.params);
	}

	public String getImplName() {
		return implName;
	}

	public void setImplName(String implName) {
		this.implName = implName;
	}

	public Map getParams() {
		return params;
	}

	public void setParams(Map params) {
		this.params = params;
	}

}
