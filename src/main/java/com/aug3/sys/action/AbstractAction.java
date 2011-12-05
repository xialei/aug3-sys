package com.aug3.sys.action;

import java.util.HashMap;
import java.util.Map;

/**
 * This class provides a skeletal implementation of the Action interface.
 * 
 * @author xial
 */
public abstract class AbstractAction implements Action {

	@SuppressWarnings("rawtypes")
	private Map args = new HashMap();
	private String refName = getClass().getName();

	@Override
	public void init() throws Exception {
	}

	@Override
	public Action getInstance() throws Exception {
		return this;
	}

	@Override
	public Object perform(Map Params) throws Exception {
		throw new UnsupportedOperationException("Method "
				+ getClass().getName()
				+ ".perform() has not been implemented yet.");
	}

	@Override
	public void terminate() throws Exception {
	}

	@Override
	public void addArg(String name, Object val) {
		args.put(name, val);

	}

	@Override
	public void addArgs(Map val) {
		args.putAll(val);

	}

	@Override
	public Map getArgs() {
		return args;
	}

	@Override
	public void setRefName(String refName) {
		this.refName = refName;
	}

	@Override
	public String getRefName() {
		return refName;
	}

	public Object clone() {
		try {
			AbstractAction a = (AbstractAction) super.clone();
			a.args = new HashMap();
			return a;
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

}
