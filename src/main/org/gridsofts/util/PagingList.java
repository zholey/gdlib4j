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
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.gridsofts.event.Event;
import org.gridsofts.event.EventDispatcher;
import org.gridsofts.event.EventType;

/**
 * 分页页面集
 * 
 * @author Lei
 * 
 * @param <E>
 */
public class PagingList<E> implements List<E>, Serializable {
	private static final long serialVersionUID = 1L;

	private EventDispatcher evtDispatcher; // 事件派发器
	
	private ArrayList<E> actualList = new ArrayList<>();

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
		evtDispatcher = new EventDispatcher();

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
		evtDispatcher.addEventListener(PagingEvent.Action, listener);
	}

	/**
	 * 删除翻页事件侦听
	 * 
	 * @param listener
	 */
	public void removeEventListener(PagingListener listener) {
		evtDispatcher.removeEventListener(listener);
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

	public boolean add(E e) {
		boolean resultCode = actualList.add(e);

		refresh();

		// 发送数据更新事件
		evtDispatcher.dispatchEvent(PagingEvent.Action, "dataChanged", new PagingEvent(this));

		return resultCode;
	}

	public boolean addAll(Collection<? extends E> collection) {
		boolean resultCode = actualList.addAll(collection);

		refresh();

		// 发送数据更新事件
		evtDispatcher.dispatchEvent(PagingEvent.Action, "dataChanged", new PagingEvent(this));

		return resultCode;

	}

	public boolean addAll(int start, Collection<? extends E> collection) {
		boolean resultCode = actualList.addAll(start, collection);

		refresh();

		// 发送数据更新事件
		evtDispatcher.dispatchEvent(PagingEvent.Action, "dataChanged", new PagingEvent(this));

		return resultCode;
	}

	public void clear() {
		actualList.clear();

		refresh();

		// 发送数据更新事件
		evtDispatcher.dispatchEvent(PagingEvent.Action, "dataChanged", new PagingEvent(this));
	}

	public E get(int index) {

		if (index < 0 || index >= getPageCapacity() || getRealIndex(index) >= getRealSize()) {
			throw new IndexOutOfBoundsException("index : " + index);
		}

		return actualList.get(getRealIndex(index));
	}

	public int indexOf(Object e) {
		int index = actualList.indexOf(e);

		// 限定在当前页
		if (index < (getPageNum() - 1) * getPageCapacity() || index >= getPageNum() * getPageCapacity()) {
			return -1;
		}

		// 返回元素在当前页内的位置
		return index - (getPageNum() - 1) * getPageCapacity();
	}

	public int lastIndexOf(Object e) {
		int index = actualList.lastIndexOf(e);

		// 限定在当前页
		if (index < (getPageNum() - 1) * getPageCapacity() || index >= getPageNum() * getPageCapacity()) {
			return -1;
		}

		// 返回元素在当前页内的位置
		return index - (getPageNum() - 1) * getPageCapacity();
	}

	public E remove(int index) {
		E e = actualList.remove(index);

		refresh();

		// 发送数据更新事件
		evtDispatcher.dispatchEvent(PagingEvent.Action, "dataChanged", new PagingEvent(this));

		return e;
	}

	public boolean remove(Object e) {
		boolean resultCode = actualList.remove(e);

		refresh();

		// 发送数据更新事件
		evtDispatcher.dispatchEvent(PagingEvent.Action, "dataChanged", new PagingEvent(this));

		return resultCode;
	}

	public E set(int index, E e) {

		if (index < 0 || index >= getPageCapacity() || getRealIndex(index) >= getRealSize()) {
			throw new IndexOutOfBoundsException("index : " + index);
		}

		E resultObj = actualList.set(getRealIndex(index), e);

		// 发送数据更新事件
		evtDispatcher.dispatchEvent(PagingEvent.Action, "dataChanged", new PagingEvent(this));

		return resultObj;
	}

	/**
	 * 返回当前页尺寸，迭代分页结果时可用此方法。
	 */
	public int size() {

		if (this.pageNum * this.pageCapacity > getRealSize()) {
			return getRealSize() - (this.pageNum - 1) * this.pageCapacity;
		}

		return this.pageCapacity;
	}

	public Iterator<E> iterator() {
		return getClip().iterator();
	}

	/**
	 * 返回当前页面片断
	 * 
	 * @return
	 */
	public List<E> getClip() {

		List<E> clipList = new ArrayList<>();

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
		return actualList.size();
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
		evtDispatcher.dispatchEvent(PagingEvent.Action, "pageChanged", new PagingEvent(this));

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
		evtDispatcher.dispatchEvent(PagingEvent.Action, "pageChanged", new PagingEvent(this));

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
	public static class PagingEvent extends Event {
		private static final long serialVersionUID = 1L;

		public static final EventType<PagingEvent> Action = new EventType<>(PagingEvent.class, "PagingAction");

		public PagingEvent(Object source) {
			super(source);
		}
	}

	@Override
	public boolean isEmpty() {
		return getClip().isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return getClip().contains(o);
	}

	@Override
	public Object[] toArray() {
		return getClip().toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return getClip().toArray(a);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return getClip().containsAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return actualList.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return actualList.retainAll(c);
	}

	@Override
	public void add(int index, E element) {
		actualList.add(index, element);
	}

	@Override
	public ListIterator<E> listIterator() {
		return getClip().listIterator();
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		return getClip().listIterator(index);
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		return getClip().subList(fromIndex, toIndex);
	}
}
