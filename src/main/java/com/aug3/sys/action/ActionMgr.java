package com.aug3.sys.action;

import java.util.Map;

import com.aug3.sys.CommonException;

/**
 * This is a helper class for clients to invoke actions.
 * 
 * @author xial
 */
public class ActionMgr {

	@SuppressWarnings("rawtypes")
	public static Object perform(String impl, Map params) throws Exception {

		Action a = getImpl(impl);

		return a.perform(params);

	}

	/**
	 * Invokes an implementation with the invocation context. The caller has to
	 * make sure all required parameters are already in the invocation context.
	 * 
	 * @param actDef
	 *            the implementation class plus invocation parameters
	 * @param parms
	 *            the invocation parameters
	 * @return if the execution returns any object
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object perform(ActionDef def, Map params) throws Exception {
		Action a = getImpl(def.getImplName());

		if (def.getParams() != null) {
			params.putAll(def.getParams());
		}

		return a.perform(params);
	}

	/**
	 * 
	 * @param impl
	 *            -- the implementation class name
	 * @return
	 * @throws Exception
	 */
	public static Action getImpl(String impl) throws Exception {

		// load the implementation class
		Class<?> clazz = null;
		try {
			clazz = Class.forName(impl);

		} catch (Exception e) {
			throw new CommonException(
					"Fail to load Action implementation class: [" + impl + "]",
					e);
		}

		// create an instance of the implementation class
		Action a = (Action) clazz.newInstance();

		a.init();

		// return the instance to the user, note the implementation has
		// the choice to return singleton or new instance
		return a.getInstance();
	}

}
