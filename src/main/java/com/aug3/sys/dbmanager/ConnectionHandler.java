package com.aug3.sys.dbmanager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.logicalcobwebs.proxool.ProxoolException;
import org.logicalcobwebs.proxool.configuration.JAXPConfigurator;

import com.aug3.sys.util.StringUtil;

/**
 * 
 * @author xial
 * 
 * Please be careful when you want to use this class. For proxool only!
 * 
 *         ConnectionHandler is used to handle database connections;
 * 
 *         We choose proxool as the connection pool. To use this, user need to
 *         initilize the proxool configuration. We have two ways to achieve
 *         this: (1). invoke static method configProxool with proxool.xml as the
 *         parameter (2). initialize proxool configure in web.xml <servlet>
 *         <servlet-name> proxoolInitialServlet </servlet-name> <servlet-class>
 *         org.logicalcobwebs.proxool.configuration.ServletConfigurator
 *         </servlet-class> <init-param> <param-name> xmlFile </param-name>
 *         <param-value> WEB-INF/db-proxool.xml </param-value> </init-param>
 *         <load-on-startup> 1 </load-on-startup> </servlet>
 *         
 * @version 0.1
 */
public class ConnectionHandler {

	static {
		try {
			JAXPConfigurator.configure(
					ConnectionHandler.class.getResource("/db-proxool.xml")
							.getFile(), false);
			// The false means non-validating
		} catch (ProxoolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * configure proxool by specified xmlfile This method should be invoke only
	 * once.
	 * 
	 * 
	 * @param xmlFileName
	 */
	public static void configProxool(String xmlFileName) {

		String proxoolConfigFile = StringUtil.isBlank(xmlFileName) ? "/db-proxool.xml"
				: xmlFileName;

		try {
			JAXPConfigurator.configure(proxoolConfigFile, false);
			// The false means non-validating
		} catch (ProxoolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param dbName
	 *            For example: proxool.mysql-log
	 * @return
	 */
	public static Connection getConnection(String dbName) {

		Connection connection = null;

		try {
			connection = DriverManager.getConnection(dbName);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return connection;

	}

	public static void releaseConnection(Connection conn) {

		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static void releaseStatement(Statement ps) {

		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * For test only
	 */
	public static void dummyMethod() {
		System.out.println("Only for test.");
	}

}
