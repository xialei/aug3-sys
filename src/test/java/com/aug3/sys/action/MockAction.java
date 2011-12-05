package com.aug3.sys.action;

import java.util.Map;

import com.aug3.sys.action.AbstractAction;

public class MockAction extends AbstractAction {

	public boolean performed = false;

	public Object perform(Map parms) {
		performed = true;
		return null;
	}
}
