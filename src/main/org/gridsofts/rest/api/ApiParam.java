/**
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.rest.api;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

/**
 * API请求参数
 *
 * @author lei
 */
public class ApiParam implements Serializable {
	private static final long serialVersionUID = 1L;
	private Header header; //

	public Header getHead() {
		return header;
	}

	public void setHead(Header header) {
		this.header = header;
	}

	/**
	 * 用来对请求头的参数进行统一定义
	 * 
	 * @author lei
	 */
	public static class Header implements Serializable {
		private static final long serialVersionUID = 1L;

		private String acceptApiVersion; // 当前请求的API版本号

		private String tokenId; // 用户登录后获取的统一认证Token；可用于进行“单点登录”的验证
		private String userId;// 交易平台系统ID

		private String platformType; // 接口调用平台的类型（ios, android , wechat）
		private String platformVersion; // 接口调用平台的版本（8.1, 4.2 , x.x）

		private String callTime; // 接口本地调用时间戳

		private String mobileBrand; // 手机品牌（小米、华为...）
		private String mobileStandard; // 手机网络版本 (GSM ; G ; CDMA ; GSM
										// TD-SCDMA….)

		private String appVersionNo; // APP小版本号（1.0、2.0）
		private String channelCode; // 渠道码

		private String sign; // 校验码

		public Header fillBy(HttpServletRequest request) {

			this.setAcceptApiVersion(request.getHeader("Accept-APIVersion"));
			this.setTokenId(request.getHeader("tokenId"));
			this.setUserId(request.getHeader("userId"));
			this.setPlatformType(request.getHeader("platformType"));
			this.setPlatformVersion(request.getHeader("platformVersion"));
			this.setCallTime(request.getHeader("callTime"));
			this.setMobileBrand(request.getHeader("mobileBrand"));
			this.setMobileStandard(request.getHeader("mobileStandard"));
			this.setAppVersionNo(request.getHeader("appVersionNo"));
			this.setChannelCode(request.getHeader("channelCode"));
			this.setSign(request.getHeader("sign"));

			return this;
		}

		/**
		 * @return the acceptApiVersion
		 */
		public String getAcceptApiVersion() {
			return acceptApiVersion;
		}

		/**
		 * @param acceptApiVersion
		 *            the acceptApiVersion to set
		 */
		public void setAcceptApiVersion(String acceptApiVersion) {
			this.acceptApiVersion = acceptApiVersion;
		}

		/**
		 * @return the tokenId
		 */
		public String getTokenId() {
			return tokenId;
		}

		/**
		 * @param tokenId
		 *            the tokenId to set
		 */
		public void setTokenId(String tokenId) {
			this.tokenId = tokenId;
		}

		/**
		 * @return the userId
		 */
		public String getUserId() {
			return userId;
		}

		/**
		 * @param userId
		 *            the userId to set
		 */
		public void setUserId(String userId) {
			this.userId = userId;
		}

		/**
		 * @return the platformType
		 */
		public String getPlatformType() {
			return platformType;
		}

		/**
		 * @param platformType
		 *            the platformType to set
		 */
		public void setPlatformType(String platformType) {
			this.platformType = platformType;
		}

		/**
		 * @return the platformVersion
		 */
		public String getPlatformVersion() {
			return platformVersion;
		}

		/**
		 * @param platformVersion
		 *            the platformVersion to set
		 */
		public void setPlatformVersion(String platformVersion) {
			this.platformVersion = platformVersion;
		}

		/**
		 * @return the callTime
		 */
		public String getCallTime() {
			return callTime;
		}

		/**
		 * @param callTime
		 *            the callTime to set
		 */
		public void setCallTime(String callTime) {
			this.callTime = callTime;
		}

		/**
		 * @return the mobileBrand
		 */
		public String getMobileBrand() {
			return mobileBrand;
		}

		/**
		 * @param mobileBrand
		 *            the mobileBrand to set
		 */
		public void setMobileBrand(String mobileBrand) {
			this.mobileBrand = mobileBrand;
		}

		/**
		 * @return the mobileStandard
		 */
		public String getMobileStandard() {
			return mobileStandard;
		}

		/**
		 * @param mobileStandard
		 *            the mobileStandard to set
		 */
		public void setMobileStandard(String mobileStandard) {
			this.mobileStandard = mobileStandard;
		}

		/**
		 * @return the appVersionNo
		 */
		public String getAppVersionNo() {
			return appVersionNo;
		}

		/**
		 * @param appVersionNo
		 *            the appVersionNo to set
		 */
		public void setAppVersionNo(String appVersionNo) {
			this.appVersionNo = appVersionNo;
		}

		/**
		 * @return the channelCode
		 */
		public String getChannelCode() {
			return channelCode;
		}

		/**
		 * @param channelCode
		 *            the channelCode to set
		 */
		public void setChannelCode(String channelCode) {
			this.channelCode = channelCode;
		}

		/**
		 * @return the sign
		 */
		public String getSign() {
			return sign;
		}

		/**
		 * @param sign
		 *            the sign to set
		 */
		public void setSign(String sign) {
			this.sign = sign;
		}
	}
}