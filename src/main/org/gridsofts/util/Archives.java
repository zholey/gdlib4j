/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * 实现了序列化对象存（取）档方法的方便类。
 * 
 * @author zholey
 * @version 1.0
 */

public class Archives implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final int IS_READONLY = 1;

	public static final int IS_WRITEONLY = 2;

	public static final int IS_READWRITE = 3;

	private String arcPath;

	private File arcFile;

	private ObjectInputStream ois;

	private ObjectOutputStream oos;

	/**
	 * 此构造方法根据一个指定的存档路径，构造此存档的文件
	 * 
	 * @param arcPath
	 *            存档文件的存放路径
	 */
	public Archives(String arcPath) {
		this.arcPath = arcPath;
		this.arcFile = new File(this.arcPath);
	}

	/**
	 * 此构造方法需要一个存档文件对象
	 * 
	 * @param file
	 *            存档文件对象
	 */
	public Archives(File file) {
		if (file != null) {
			this.arcFile = file;
		}
	}

	/**
	 * 此方法从存档文件中读取第一个对象，并返回。
	 * 
	 * @return 读取到的对象，如果存档文件不存在或不是文件，则返回NULL。
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Object load() throws IOException, ClassNotFoundException {
		if (!this.arcFile.exists() || !this.arcFile.isFile()) {
			return null;
		}

		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
				this.arcFile));
		Object obj = ois.readObject();
		ois.close();

		return obj;
	}

	public <E> E load(Class<E> e) throws IOException, ClassNotFoundException,
			ClassCastException {
		if (!this.arcFile.exists() || !this.arcFile.isFile()) {
			return null;
		}

		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
				this.arcFile));
		Object obj = ois.readObject();
		ois.close();

		return e.cast(obj);
	}

	public static Object load(File arcFile) throws IOException,
			ClassNotFoundException {
		if (!arcFile.exists() || !arcFile.isFile()) {
			return null;
		}

		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
				arcFile));
		Object obj = ois.readObject();
		ois.close();

		return obj;
	}

	public static <E> E load(File arcFile, Class<E> e) throws IOException,
			ClassNotFoundException, ClassCastException {
		if (!arcFile.exists() || !arcFile.isFile()) {
			return null;
		}

		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
				arcFile));
		Object obj = ois.readObject();
		ois.close();

		return e.cast(obj);
	}

	/**
	 * 此方法可以把一个可序列化的对象保存到存档文件中。
	 * 
	 * @param obj
	 *            要保存到存档文件的对象
	 * @throws IOException
	 */
	public <E> void save(E e) throws IOException {
		File dFile = new File(this.arcFile.getParent());
		if (!dFile.exists()) {
			dFile.mkdirs();
		}
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
				this.arcFile.getPath()));
		oos.writeObject(e);
		oos.close();
	}

	public static <E> void save(E e, File arcFile) throws IOException {
		File dFile = new File(arcFile.getParent());
		if (!dFile.exists()) {
			dFile.mkdirs();
		}

		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
				arcFile.getPath()));
		oos.writeObject(e);
		oos.close();
	}

	/**
	 * 打开存档文件，准备从（向）存档中顺序读取（写入）多个对象。<br>
	 * <br>
	 * 需要与{@link #close() close}方法配全使用。
	 * 
	 * @return 存档打开状态
	 * @throws IOException
	 */
	public int open() throws IOException {
		File dFile = new File(this.arcFile.getParent());
		if (!dFile.exists()) {
			dFile.mkdirs();
		}
		if (this.arcFile.exists()) {
			this.ois = new ObjectInputStream(new FileInputStream(this.arcFile));
			this.oos = new ObjectOutputStream(new FileOutputStream(this.arcFile
					.getPath(), true));
			return Archives.IS_READWRITE;
		} else {
			this.oos = new ObjectOutputStream(new FileOutputStream(this.arcFile
					.getPath(), true));
			return Archives.IS_WRITEONLY;
		}
	}

	/**
	 * 向已打开的存档文件中写入一个对象。
	 * 
	 * @param obj
	 *            需要写入的对象
	 * @throws IOException
	 */
	public <E> void appendObj(E e) throws IOException {
		this.oos.writeObject(e);
	}

	/**
	 * 从已打开的存档文件中读取下一个对象。
	 * 
	 * @return 读取的对象
	 * @throws Exception
	 */
	public Object nextObj() throws Exception {
		if (this.ois == null) {
			return null;
		}
		Object obj = this.ois.readObject();
		return obj;
	}

	public <E> E nextObj(Class<E> e) throws IOException,
			ClassNotFoundException, ClassCastException {
		if (this.ois == null) {
			return null;
		}
		Object obj = this.ois.readObject();
		return e.cast(obj);
	}

	/**
	 * 关闭存档文件。<br>
	 * <br>
	 * 此方法需要与{@link #open() open}方法配合使用。
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		if (this.ois != null) {
			this.ois.close();
		}
		if (this.oos != null) {
			this.oos.close();
		}
	}
}
