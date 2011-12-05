package com.aug3.sys;

/**
 * 
 * @author xial
 *
 * @param <X>
 */
public class DefaultValueThreadLocal<X> extends ThreadLocal<X> {

	private X initialValue;

	DefaultValueThreadLocal(X defaultValue) {
		initialValue = defaultValue;
	}

	@Override
	protected X initialValue() {
		return initialValue;
	}

}