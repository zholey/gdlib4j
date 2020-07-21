/**
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.json;

import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * 自定义Jackson功能模块
 * 
 * @author lei
 */
public class CustomJacksonModule extends SimpleModule {
	private static final long serialVersionUID = 1L;

	@Override
	public void setupModule(SetupContext context) {
		context.addDeserializers(new LocalDateTimeDeserializers());
	}
}
