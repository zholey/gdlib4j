/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

/**
 * 分页页面集
 * 
 * @author Lei
 * 
 * @param <E>
 */
public class PagingList<E> extends ArrayList<E> {
	private static final long serialVersionUID = 1L;

	private EventDispatcher<PagingListener, PagingEvent> dispatcher; // 事件派发器

	private int pageCount; // 总页数
	private int pageNum; // 页码
	private int pageCapacity; // 页容量(每页显示行数)

	/**
	 * 构造一个页容量为50的分页列表
	 */
	public PagingList() {
		this(50);
	}

	/**
	 * 构造一个包含初始数据，页容量为50的分页列表
	 */
	public PagingList(List<E> data) {
		this(50);

		addAll(data);
	}

	/**
	 * 构造一个指定页容量的分页列表
	 * 
	 * @param pageCapacity
	 */
	public PagingList(int pageCapacity) {
		dispatcher = new EventDispatcher<>();

		setPageCapacity(pageCapacity);
	}

	/**
	 * 默认构造一个分页列表
	 * 
	 * @param collection
	 */
	public PagingList(Collection<? extends E> collection) {
		this(50);

		addAll(collection);
	}

	/**
	 * 默认构造一个分页列表
	 * 
	 * @param collection
	 */
	public PagingList(int pageCapacity, Collection<? extends E> collection) {
		this(pageCapacity);

		addAll(collection);
	}

	/**
	 * 注册翻页事件侦听
	 * 
	 * @param listener
	 */
	public void addEventListener(PagingListener listener) {
		dispatcher.addEventListener(listener);
	}

	/**
	 * 删除翻页事件侦听
	 * 
	 * @param listener
	 */
	public void removeEventListener(PagingListener listener) {
		dispatcher.removeEventListener(listener);
	}

	/**
	 * 重新计算
	 */
	protected void refresh() {

		this.pageCapacity = this.pageCapacity > 0 ? this.pageCapacity : getRealSize();

		this.pageCount = (int) Math.ceil((double) getRealSize() / this.pageCapacity);

		if (this.pageNum > this.pageCount) {
			this.pageNum = this.pageCount;
		}

		if (this.pageNum < 1) {
			this.pageNum = 1;
		}
	}

	/**
	 * 换算成真实的索引
	 * 
	 * @param index
	 * @return
	 */
	protected int getRealIndex(int index) {
		return index + (getPageNum() - 1) * getPageCapacity();
	}

	@Override
	public boolean add(E e) {
		boolean resultCode = super.add(e);

		refresh();

		// 发送数据更新事件
		dispatcher.dispatchEvent("dataChanged", new PagingEvent(this));

		return resultCode;
	}

	@Override
	public boolean addAll(Collection<? extends E> collection) {
		boolean resultCode = super.addAll(collection);

		refresh();

		// 发送数据更新事件
		dispatcher.dispatchEvent("dataChanged", new PagingEvent(this));

		return resultCode;

	}

	@Override
	public boolean addAll(int start, Collection<? extends E> collection) {
		boolean resultCode = super.addAll(start, collection);

		refresh();

		// 发送数据更新事件
		dispatcher.dispatchEvent("dataChanged", new PagingEvent(this));

		return resultCode;
	}

	@Override
	public void clear() {
		super.clear();

		refresh();

		// 发送数据更新事件
		dispatcher.dispatchEvent("dataChanged", new PagingEvent(this));
	}

	@Override
	public E get(int index) {

		if (index < 0 || index >= getPageCapacity() || getRealIndex(index) >= getRealSize()) {
			throw new IndexOutOfBoundsException("index : " + index);
		}

		return super.get(getRealIndex(index));
	}

	@Override
	public int indexOf(Object e) {
		int index = super.indexOf(e);

		// 限定在当前页
		if (index < (getPageNum() - 1) * getPageCapacity() || index >= getPageNum() * getPageCapacity()) {
			return -1;
		}

		// 返回元素在当前页内的位置
		return index - (getPageNum() - 1) * getPageCapacity();
	}

	@Override
	public int lastIndexOf(Object e) {
		int index = super.lastIndexOf(e);

		// 限定在当前页
		if (index < (getPageNum() - 1) * getPageCapacity() || index >= getPageNum() * getPageCapacity()) {
			return -1;
		}

		// 返回元素在当前页内的位置
		return index - (getPageNum() - 1) * getPageCapacity();
	}

	@Override
	public E remove(int index) {
		E e = super.remove(index);

		refresh();

		// 发送数据更新事件
		dispatcher.dispatchEvent("dataChanged", new PagingEvent(this));

		return e;
	}

	@Override
	public boolean remove(Object e) {
		boolean resultCode = super.remove(e);

		refresh();

		// 发送数据更新事件
		dispatcher.dispatchEvent("dataChanged", new PagingEvent(this));

		return resultCode;
	}

	@Override
	protected void removeRange(int start, int end) {
		super.removeRange(start, end);

		refresh();

		// 发送数据更新事件
		dispatcher.dispatchEvent("dataChanged", new PagingEvent(this));
	}

	@Override
	public E set(int index, E e) {

		if (index < 0 || index >= getPageCapacity() || getRealIndex(index) >= getRealSize()) {
			throw new IndexOutOfBoundsException("index : " + index);
		}

		E resultObj = super.set(getRealIndex(index), e);

		// 发送数据更新事件
		dispatcher.dispatchEvent("dataChanged", new PagingEvent(this));

		return resultObj;
	}

	/**
	 * 返回当前页尺寸，迭代分页结果时可用此方法。
	 */
	@Override
	public int size() {

		if (this.pageNum * this.pageCapacity > getRealSize()) {
			return getRealSize() - (this.pageNum - 1) * this.pageCapacity;
		}

		return this.pageCapacity;
	}

	@Override
	public Iterator<E> iterator() {
		return getClip().iterator();
	}

	/**
	 * 返回当前页面片断
	 * 
	 * @return
	 */
	public List<E> getClip() {

		List<E> clipList = new ArrayList<E>();

		for (int i = 0; i < size(); i++) {
			clipList.add(get(i));
		}

		return clipList;
	}

	/**
	 * 返回总长度，不考虑分页。
	 * 
	 * @return
	 */
	public int getRealSize() {
		return super.size();
	}

	/**
	 * 首页
	 */
	public PagingList<E> firstPage() {
		return setPageNum(1);
	}

	/**
	 * 上一页
	 */
	public PagingList<E> prevPage() {
		return setPageNum(getPageNum() - 1);
	}

	/**
	 * 下一页
	 */
	public PagingList<E> nextPage() {
		return setPageNum(getPageNum() + 1);
	}

	/**
	 * 末页
	 */
	public PagingList<E> lastPage() {
		return setPageNum(getPageCount());
	}

	/**
	 * 总页数
	 * 
	 * @return
	 */
	public int getPageCount() {
		return pageCount;
	}

	/**
	 * 页码
	 * 
	 * @return
	 */
	public int getPageNum() {
		return pageNum;
	}

	public PagingList<E> setPageNum(int pageNum) {
		this.pageNum = pageNum;
		refresh();

		// 发送数据更新事件
		dispatcher.dispatchEvent("pageChanged", new PagingEvent(this));

		return this;
	}

	/**
	 * 页容量
	 * 
	 * @return
	 */
	public int getPageCapacity() {
		return pageCapacity;
	}

	/**
	 * 设置页容量
	 * 
	 * @param pageCapacity
	 */
	public PagingList<E> setPageCapacity(int pageCapacity) {
		this.pageCapacity = pageCapacity;

		refresh();

		// 发送数据更新事件
		dispatcher.dispatchEvent("pageChanged", new PagingEvent(this));

		return this;
	}

	/**
	 * 翻页事件监听
	 * 
	 * @author Lei
	 * 
	 */
	public static interface PagingListener extends EventListener {

		/**
		 * 数据发生更改
		 * 
		 * @param event
		 */
		public void dataChanged(PagingEvent event);

		/**
		 * 页数等内容发生更改
		 * 
		 * @param event
		 */
		public void pageChanged(PagingEvent event);
	}

	/**
	 * 翻页事件监听适配器（空）
	 * 
	 * @author Lei
	 * 
	 */
	public static class PagingAdapter implements PagingListener, Serializable {
		private static final long serialVersionUID = 1L;

		public void dataChanged(PagingEvent event) {
		}

		public void pageChanged(PagingEvent event) {
		}
	}

	/**
	 * 翻页事件
	 * 
	 * @author Lei
	 * 
	 */
	public static class PagingEvent extends EventObject {
		private static final long serialVersionUID = 1L;

		public PagingEvent(Object source) {
			super(source);
		}
	}
}
