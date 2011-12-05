package com.aug3.sys.rmi;

import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aug3.sys.cfg.ConfigConstants;
import com.aug3.sys.cfg.ConfigFinder;
import com.aug3.sys.cfg.ConfigMgr;
import com.aug3.sys.properties.BootProperties;

/**
 * Servlet to initialize and manage remote service
 * 
 * Add this to your web.xml file. Or we can define a service class that called
 * after the server is up and running and before the remote service is invoked,
 * and initialize the registry.
 * 
 * Note: There is no securitymanager present anywhere, since we have to
 * configure in the catalina.policy to integrate with tomcat.
 * 
 * if(System.getSecurityManager() == null) {
 * 
 * System.setSecurityManager(new RMISecurityManager());
 * 
 * }
 * 
 * @author xial
 * 
 */
public class RMIServlet extends HttpServlet {

	private static final long serialVersionUID = -6572930242396301594L;

	public static boolean isRegistered = false;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RMIServlet() {
		super();
	}

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {

		if (!isRegistered) {

			try {
				// ------configMgr--------
				IConfigMgr configMgr = new ConfigMgr();
				IConfigMgr stub_configMgr = (IConfigMgr) UnicastRemoteObject
						.exportObject(configMgr, 0);
				Registry registry = LocateRegistry
						.createRegistry(BootProperties.getInstance()
								.getProperty(ConfigConstants.CONFIG_RMI_PORT,
										1099));
				registry.rebind(IConfigMgr.SERVICE_NAME, stub_configMgr);

				IConfigFinder configFinder = new ConfigFinder();
				IConfigMgr stub_configFinder = (IConfigMgr) UnicastRemoteObject
						.exportObject(configFinder, 0);
				registry.rebind(IConfigFinder.SERVICE_NAME, stub_configFinder);

				System.out.println("Remote service bound");
				isRegistered = true;
			} catch (Exception e) {
				System.err.println("Remote service exception:");
				e.printStackTrace();
			}
		}
	}

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void service(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {
	}

	public void destroy() {
		if (isRegistered) {
			Registry registry = null;
			try {
				registry = LocateRegistry.getRegistry(BootProperties
						.getInstance().getProperty(
								ConfigConstants.CONFIG_RMI_PORT,
								ConfigConstants.CONFIG_RMI_PORT_DEFAULT));
			} catch (RemoteException e1) {
			}
			try {
				UnicastRemoteObject.unexportObject(registry, false);
			} catch (final NoSuchObjectException e) {
			}
			try {
				registry.unbind(IConfigMgr.SERVICE_NAME);
				registry.unbind(IConfigFinder.SERVICE_NAME);
			} catch (final AccessException e) {
			} catch (final RemoteException e) {
			} catch (final NotBoundException e) {
			}
		}

	}

}
