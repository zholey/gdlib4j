/**
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.rest.api.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用来对RESTFul API接口进行描述
 *
 * @author lei
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ java.lang.annotation.ElementType.METHOD })
public @interface ApiMethod {

	/**
	 * 方法调用之前的日志
	 * 
	 * @return
	 */
	boolean beforeLog() default true;

	/**
	 * 方法调用之后的日志
	 * 
	 * @return
	 */
	boolean afterLog() default true;
}
