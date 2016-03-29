/**
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 异步任务处理服务
 *
 * @author lei
 */
public class AsyncService {
	private ExecutorService executorService;

	private int poolSize = 3;

	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}

	/**
	 * 启动异步服务
	 */
	public void startup() {
		executorService = Executors.newFixedThreadPool(poolSize);
	}

	/**
	 * 停止提供服务
	 */
	public void shutdown() {
		executorService.shutdown();
	}

	/**
	 * 调度异步任务
	 * 
	 * @param asyncTask
	 */
	public void execute(Runnable asyncTask) {
		executorService.execute(asyncTask);
	}
}
