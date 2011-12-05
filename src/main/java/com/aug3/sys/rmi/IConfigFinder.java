package com.aug3.sys.rmi;

import java.rmi.Remote;

/**
 * This is the RMI to the LocalConfigFinder class
 * 
 * delegates all the work to the LocalConfigFinder singleton.
 * 
 * @author xial
 */
public interface IConfigFinder extends Remote {

	public static final String SERVICE_NAME = "configFinder";

	/**
	 * 
	 * @return the active config server url
	 * 
	 */
	public String getServerUrl();

	public void reset();

}
