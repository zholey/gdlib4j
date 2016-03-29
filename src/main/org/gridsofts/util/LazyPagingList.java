/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.util;

import java.util.List;

/**
 * 可延迟加载数据的分页页面集
 * 
 * @author Lei
 * 
 * @param <E>
 */
public class LazyPagingList<E> extends PagingList<E> {
	private static final long serialVersionUID = 1L;

	private ILazyService<E> service;

	private int realSize;

	/**
	 * 创建默认页容量为50的分页列表
	 * 
	 * @param service
	 */
	public LazyPagingList(ILazyService<E> service) {
		this(service, 50, true);
	}

	public LazyPagingList(ILazyService<E> service, int pageCapacity) {
		this(service, pageCapacity, true);
	}

	public LazyPagingList(ILazyService<E> service, int pageCapacity, boolean nowLoad) {
		super(pageCapacity);

		this.service = service;

		// 获取总记录数
		realSize = service.getRealSize();
		
		refresh();

		// 立即加载第一页数据
		if (nowLoad) {
			loadPageData();
		}
	}

	/**
	 * 逐页加载数据，直到没有数据可供加载。
	 */
	private boolean loadPageData() {

		if (super.getRealSize() < getRealSize()) {

			return addAll(service.getClip(super.getRealSize(), getPageCapacity()));
		}

		return false;
	}

	@Override
	public E get(int index) {

		// 如果请求的内容在合理范围之内，并且当前没有相应的记录，则启动查询
		if (index >= 0 && getRealIndex(index) < getRealSize()) {

			// 逐页查询
			while (getRealIndex(index) >= super.getRealSize()) {

				// 防止查询不到数据时出现死循环
				if (!loadPageData()) {
					break;
				}
			}

			return super.get(index);
		}

		return null;
	}

	@Override
	public PagingList<E> setPageNum(int pageNum) {

		super.setPageNum(pageNum);

		// 目标页数据起始位置
		int offsetOfPage = (getPageNum() - 1) * getPageCapacity();
		// 目标页数据截止位置
		int limitOfPage = getPageNum() * getPageCapacity();

		// 单向逐页查询至当前页码
		while (getRealSize() > offsetOfPage && super.getRealSize() < limitOfPage) {

			// 防止查询不到数据时出现死循环
			if (!loadPageData()) {
				break;
			}
		}

		return this;
	}

	@Override
	public int getRealSize() {
		return realSize;
	}

	/**
	 * 延迟加载服务接口
	 * 
	 * @author Lei
	 * 
	 */
	public static interface ILazyService<E> {

		/**
		 * 获取总记录行数
		 * 
		 * @return
		 */
		public int getRealSize();

		/**
		 * 获取页面片断
		 * 
		 * @param start
		 * @param limit
		 * @return
		 */
		public List<E> getClip(int start, int limit);
	}
}
