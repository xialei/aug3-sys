package com.aug3.sys.cache.memcached;

import com.danga.MemCached.SockIOPool;

/**
 * An MBean for managing the memcached pool. It can reset the system, 
 * as well as print the current configuration being used by the pool.
 * 
 * @author xial
 */
public class MemCachedPoolMgr implements MemCachedPoolMgrMBean {

	@Override
	public void reset() {
		SockIOPool pool = SockIOPool.getInstance();
		if (pool.isInitialized()) {
			pool.shutDown();
		}
		MemCachedConfig config = new MemCachedConfig();
		pool.setServers(config.getServers());
		pool.setWeights(config.getWeights());
		pool.setInitConn(config.getMinConnections());
		pool.setMinConn(config.getMinConnections());
		pool.setMaxConn(config.getMaxConnections());
		pool.setMaxIdle(1000 * 60 * 60 * 6);//set the max idle time for a connection
		
		// 设置主线程睡眠时间
		// 每隔30秒醒来  然后
		// 开始维护 连接数大小
		// pool.setMaintSleep( 30 );

		// 设置tcp 相关的树形
		// 关闭nagle算法
		// 设置 读取 超时3秒钟  set the read timeout to 3 secs
		// 不设置连接超时
		// pool.setNagle( false );
		// pool.setSocketTO( 3000 );
		// pool.setSocketConnectTO( 0 );

		pool.initialize();
	}

	@Override
	public String currentConfig() {
		MemCachedConfig config = new MemCachedConfig();
		return config.toString();
	}

}
