package com.aug3.sys.cache.memcached;

import java.util.Arrays;
import java.util.Properties;

import com.aug3.sys.properties.LazyPropLoader;

/**
 * This class loads and parses the configuration information for memcached.
 * Configuration information is fetched from the class resource
 * <code>memcached.properties</code> which should be placed in the classpath.
 * 
 * The available configuration parameters are:
 * 
 * <table>
 * <tr>
 * <th>property</th>
 * <th>description</th>
 * <th>default</th>
 * </tr>
 * <tr>
 * <td>memcached.servers</td>
 * <td>Comma-separated list of servers being used.</td>
 * <td>localhost</td>
 * </tr>
 * <tr>
 * <td>memcached.servers.weights</td>
 * <td>Comma-separated list of the weight associated with each server.</td>
 * <td>[1,1,...] for each server in the list</td>
 * </tr>
 * <tr>
 * <td>memcached.connections.min</td>
 * <td>minimum number of connections to use.</td>
 * <td>5</td>
 * </tr>
 * <tr>
 * <td>memcached.connections.max</td>
 * <td>maximum number of connections to use</td>
 * <td>50</td>
 * </tr>
 * </table>
 * 
 * @author xial
 */
public class MemCachedConfig {

	// configuration properties
	private static final String CONFIG_RESOURCE = "/memcached.properties";
	private static final String SERVERS = "memcached.servers";
	private static final String WEIGHTS = "memcached.servers.weight";
	private static final String MIN_CONNECTIONS = "memcached.connections.min";
	private static final String MAX_CONNECTIONS = "memcached.connections.max";

	// default values
	private static final String SERVERS_DEFAULT = "localhost";
	private static final int MIN_CONNECTIONS_DEFAULT = 5;
	private static final int MAX_CONNECTIONS_DEFAULT = 50;

	private Properties config = new LazyPropLoader(CONFIG_RESOURCE);

	String[] getServers() {
		String servers = config.getProperty(SERVERS, SERVERS_DEFAULT);
		return servers.split(",");
	}

	Integer[] getWeights() {
		String weights = config.getProperty(WEIGHTS);
		if (weights != null) {
			return toIntArray(weights.split(","));
		} else {
			return generateArraysOf1s(getServers().length);
		}
	}

	private Integer[] toIntArray(String[] values) {
		Integer[] weights = new Integer[values.length];
		for (int i = 0; i < values.length; i++) {
			weights[i] = Integer.valueOf(values[i]);
		}
		return weights;
	}

	/**
	 * Returns an array of 1s of size <em>length</em>.
	 * 
	 * @param length
	 *            the size of the array
	 * 
	 * @return an array consisting of <em>length</em> 1s.
	 */
	private Integer[] generateArraysOf1s(int length) {
		Integer[] weights = new Integer[length];
		for (int i = 0; i < length; i++) {
			weights[i] = new Integer(1);
		}
		return weights;
	}

	int getMinConnections() {
		return getInt(MIN_CONNECTIONS, MIN_CONNECTIONS_DEFAULT);
	}

	int getMaxConnections() {
		return getInt(MAX_CONNECTIONS, MAX_CONNECTIONS_DEFAULT);
	}

	private int getInt(String property, int defaultValue) {
		String val = config.getProperty(property);
		return (val != null) ? Integer.parseInt(val) : defaultValue;
	}

	/**
	 * Returns the string representation of the configuration in a long,
	 * multi-line form.
	 */
	public String toString() {
		StringBuilder props = new StringBuilder();
		props.append("servers=" + Arrays.toString(getServers()) + "\n");
		props.append("weights=" + Arrays.toString(getWeights()) + "\n");
		props.append("conn(min)=" + getMinConnections() + "\n");
		props.append("conn(max)=" + getMaxConnections() + "\n");
		return props.toString();
	}

}
