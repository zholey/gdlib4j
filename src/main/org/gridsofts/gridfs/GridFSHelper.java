/**
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.gridfs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.MongoDbFactory;

import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

/**
 * 用于将向MongoDB读写文件的工具类；使用时请注入 mongoDbFactory
 */
public class GridFSHelper {
	private static Logger logger = LoggerFactory.getLogger(GridFSHelper.class);

	private MongoDbFactory mongoDbFactory;

	public void setMongoDbFactory(MongoDbFactory mongoDbFactory) {
		this.mongoDbFactory = mongoDbFactory;
	}

	/**
	 * 保存文件到mongodb中
	 *
	 * @param file
	 *            文件
	 */
	public String save(File file) {
		return save(file, "fs");
	}

	/**
	 * 保存文件到mongodb中
	 *
	 * @param file
	 *            文件
	 * @param bucket
	 *            保持的类型
	 */
	public String save(File file, String bucket) {
		logger.debug("saving to MongoDB [{}] [{}]", file.getName(), bucket);

		String fileNames = null;

		try {
			// 文件操作是在DB的基础上实现的，与表和文档没有关系
			GridFS gridFS = new GridFS(mongoDbFactory.getDb(), bucket);
			GridFSInputFile gridfile = gridFS.createFile(file);

			gridfile.setId(UUID.randomUUID().toString().replace("-", ""));
			gridfile.setFilename(file.getName());

			gridfile.save();

			logger.debug("saved.");
		} catch (IOException e) {
			logger.error("保存文件到mongodb中异常 - {}", e.getMessage(), e);
		}

		return fileNames;

	}

	/**
	 * 从mongodb中获取需要的文件
	 *
	 * @param fileId
	 * @return
	 */
	public InputStream getInputStream(String fileId) {
		return getInputStream(fileId, "fs");
	}

	/**
	 * 从mongodb中获取需要的文件
	 *
	 * @param fileId
	 * @param bucket
	 * @return
	 */
	public InputStream getInputStream(String fileId, String bucket) {
		logger.info("reading from MongoDB [{}] [{}]", fileId, bucket);

		// 文件操作是在DB的基础上实现的，与表和文档没有关系
		GridFS gridFS = new GridFS(mongoDbFactory.getDb(), bucket);

		GridFSDBFile gridfile = gridFS.findOne(fileId);
		if (gridfile != null) {
			return gridfile.getInputStream();
		}

		return null;
	}
}
