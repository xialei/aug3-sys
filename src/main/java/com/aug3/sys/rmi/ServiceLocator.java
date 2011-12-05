package com.aug3.sys.rmi;

import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * This class returns a new instance of the requested remote service.
 * 
 * Services include RMI
 * 
 * @author xial
 */
public class ServiceLocator {

	/**
	 * Returns an Remote implementation.
	 * 
	 */
	public static Remote locate(String host, int port, String serviceName) {

		Remote remoteService = null;
		try {
			Registry registry = LocateRegistry.getRegistry(host, port);

			remoteService = registry.lookup(serviceName);

		} catch (Exception e) {
			System.err.println("Remoteservice exception:");
			e.printStackTrace();
		}
		return remoteService;

	}

}