/**
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.rest.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.gridsofts.rest.api.annotation.ApiMethod;
import org.gridsofts.util.Encrypt;
import org.gridsofts.web.util.WebContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用于过滤非法API请求
 *
 * @author lei
 */
public class ApiMethodAdvice {
	private Logger logger = LoggerFactory.getLogger(ApiMethodAdvice.class);

	private String apiHeaderNames;
	private String signName;
	private String authKey;

	/**
	 * @param apiHeaderNames
	 *            the apiHeaderNames to set
	 */
	public void setApiHeaderNames(String apiHeaderNames) {
		this.apiHeaderNames = apiHeaderNames;
	}

	/**
	 * @param signName
	 *            the signName to set
	 */
	public void setSignName(String signName) {
		this.signName = signName;
	}

	/**
	 * @param authKey
	 *            the authKey to set
	 */
	public void setAuthKey(String authKey) {
		this.authKey = authKey;
	}

	/**
	 * 手动控制调用核心业务逻辑，以及调用前和调用后的处理,
	 * 
	 * 注意：当核心业务抛异常后，立即退出，转向After Advice 执行完毕After Advice，再转到Throwing Advice
	 * 
	 * @param pjp
	 * @return
	 * @throws Throwable
	 */
	public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
		HttpServletRequest request = WebContext.getRequest();

		Object[] args = joinPoint.getArgs();
		Object returnObj = null;

		// 对传入参数进行预处理
		ApiParam.Header headerObj = new ApiParam.Header().fillBy(request);
		if (args != null) {

			// 查找ApiParam对象
			for (Object argObj : args) {

				if (argObj != null && ApiParam.class.isAssignableFrom(argObj.getClass())) {
					((ApiParam) argObj).setHead(headerObj);
				}
			}
		}

		// 查找当前正在环绕的方法
		Signature joinSignature = joinPoint.getSignature();
		if (joinSignature != null && joinSignature instanceof MethodSignature) {
			MethodSignature methodSignature = (MethodSignature) joinSignature;

			ApiMethod apiAnnotation = methodSignature.getMethod().getAnnotation(ApiMethod.class);

			if (apiAnnotation == null) {
				return joinPoint.proceed(args);
			}

			// 如果方法标了ApiLogger，则需要记录日志
			if (apiAnnotation.beforeLog()) {
				logger.info("调用接口: {}[{}] {}", new Object[] { request.getRequestURI(), methodSignature.getName(),
						ToStringBuilder.reflectionToString(args) });
				logger.info("ApiParam.Header: {}", ToStringBuilder.reflectionToString(headerObj));
			}

			try {

				// 如果方法标了ApiSecurity，则需要进行验证
				// 计算校验码，如果不符则立即跳出
				String calcedSignVal = calcSignString(request);
				if (calcedSignVal == null || !calcedSignVal.equals(headerObj.getSign())) {
					return null;
				}

				// 调用核心逻辑
				returnObj = joinPoint.proceed(args);

			} catch (Throwable e) {
				logger.error("ApiError: {}", e.getMessage(), e);
			}

			if (apiAnnotation.afterLog()) {
				logger.info("接口返回: {}", ToStringBuilder.reflectionToString(returnObj));
			}
		}

		return returnObj;
	}

	/**
	 * 核心业务逻辑调用正常退出后，不管是否有返回值，正常退出后，均执行此Advice
	 * 
	 * @param joinPoint
	 */
	public void doReturn(JoinPoint joinPoint) {
	}

	/**
	 * 核心业务逻辑调用异常退出后，执行此Advice，处理错误信息
	 * 
	 * @param joinPoint
	 * @param ex
	 */
	public void doThrowing(JoinPoint joinPoint, Throwable ex) {
	}

	/**
	 * 计算某个请求的校验码
	 * 
	 * @param request
	 * @return
	 */
	private String calcSignString(HttpServletRequest request) {

		String[] ApiHeaderNames = apiHeaderNames.replaceAll("\\s+", "").split(",+");

		List<String> keysList = new ArrayList<String>();
		Map<String, String> valMap = new HashMap<String, String>();

		for (String headerName : ApiHeaderNames) {
			keysList.add(headerName);

			if (signName.equalsIgnoreCase(headerName)) {
				valMap.put(headerName, authKey);
			} else {
				valMap.put(headerName, request.getHeader(headerName));
			}
		}

		Enumeration<?> parameterNames = request.getParameterNames();
		if (parameterNames != null) {
			while (parameterNames.hasMoreElements()) {
				String parameterName = parameterNames.nextElement().toString();

				keysList.add(parameterName);
				valMap.put(parameterName, request.getParameter(parameterName));
			}
		}

		Collections.sort(keysList);

		StringBuffer paramStrBuffer = new StringBuffer();
		for (String key : keysList) {

			if (!"".equals(paramStrBuffer.toString())) {
				paramStrBuffer.append("&");
			}
			paramStrBuffer.append(key + "=" + (valMap.get(key) == null ? "" : valMap.get(key)));
		}
		
		String calcedSignVal = Encrypt.md5(paramStrBuffer.toString());
		logger.debug("ParamStr: {}, CalcedSignVal: {}", paramStrBuffer.toString(), calcedSignVal);
		
		return calcedSignVal;
	}
}
