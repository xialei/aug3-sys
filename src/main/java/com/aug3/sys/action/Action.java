package com.aug3.sys.action;

import java.util.Map;

/**
 * This is the interface that all action implementations should implement.
 * Actions are executed when events happen to the system. The events can be a
 * user input, a timer trigger, or an email response. The actions can be sending
 * an email notification, logging an audit log entry, do a business computation.
 * 
 * Different action implementations are used to perform what users intend to do.
 * The system provides some default implementations that can be customized by
 * providing parameters. If the users need to have more extensive and
 * sophisticated customizations, they can choose to do it via the
 * <code>JavaScriptAction</code> or implement their own Java action
 * implementations.
 * <p>
 * 
 * @author xial
 */
public interface Action extends Cloneable {

	/**
	 * Initializes the instance. Used by the ActionMgr.
	 * 
	 * @throws Exception
	 */
	void init() throws Exception;

	/**
	 * Returns a new or singleton instance of the implementation. Used by the
	 * ActionMgr.
	 * 
	 * @return an instance that can carry the execution
	 * @throws Exception
	 */
	Action getInstance() throws Exception;

	/**
	 * This method is invoked when an action is invoked.
	 * 
	 * @param Params
	 *            the invocation parameters
	 * @return if the execution returns any object
	 * @throws Exception
	 */
	Object perform(Map Params) throws Exception;

	/**
	 * Terminates the execution of this action. This is the counter-act of the
	 * #perform(Map) method. It should cleanly clean up all resources allocated
	 * by the #perform(Map) method, stop everything #perform(Map) is doing and
	 * make it return immediately.
	 */
	void terminate() throws Exception;

	/**
	 * Adds a static, predefined argument to the Action instance. An argument
	 * has a name and a value. They are stored with the action as a map.
	 * <p>
	 * 
	 * The predefined arguments are defined with the Action definition in the
	 * XML configurations, such as the action repository. These arguments apply
	 * to all invocations to all instances.
	 * <p>
	 * 
	 * The predefined arguments can be over-ridden by arguments defined in the
	 * invocation context.
	 * 
	 * @param name
	 *            the name of the argument
	 * @param val
	 *            the value of the argument
	 */
	void addArg(String name, Object val);

	/**
	 * Adds a list of static, predefined arguments to the Action instance.
	 * 
	 * @param val
	 *            the value of the argument
	 * @see #addArg(String, Object)
	 */
	void addArgs(Map val);

	/**
	 * Returns the redefined arguments of the action.
	 * 
	 * @return a map of the predefined arguments
	 * @see #addArg(String, Object) for details about predefined arguments.
	 */
	Map getArgs();

	/**
	 * Sets the reference name for this action implementation. The default
	 * reference name is the implementation class name. Users can assign
	 * reference name so it can be referenced in scripting.
	 * 
	 * @param refName
	 *            the reference name to the action implementation
	 */
	void setRefName(String refName);

	/**
	 * Returns the reference name for this action implementation. The default
	 * reference name is the implementation class name. Users can assign
	 * reference name so it can be referenced in scripting.
	 * 
	 * @return the reference name to the action implementation
	 */
	String getRefName();

	/**
	 * Clones this action implementation instance.
	 * 
	 * @return a clone of this action implementation instance
	 */
	Object clone();

}
