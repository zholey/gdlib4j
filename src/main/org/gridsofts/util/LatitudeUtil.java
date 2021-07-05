/**
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.util;

/**
 * 经纬度计算相关的方法
 * 
 * @author lei
 */
public class LatitudeUtil {
	public static final double EARTH_RADIUS = 6371004.0D;

	/**
	 * 计算两个经纬度的近似距离；（单位：米）
	 * 
	 * @param lon1 第一点的经度
	 * @param lat1 第一点的纬度
	 * @param lon2 第二点的经度
	 * @param lat2 第二点的纬度
	 * @return
	 */
	public static double distance(double lon1, double lat1, double lon2, double lat2) {
		return distance(lon1, lat1, lon2, lat2, EARTH_RADIUS);
	}

	/**
	 * 计算两个经纬度的近似距离；（单位：米）
	 * 
	 * @param lon1        第一点的经度
	 * @param lat1        第一点的纬度
	 * @param lon2        第二点的经度
	 * @param lat2        第二点的纬度
	 * @param earthRadius 地球半径
	 * @return
	 */
	public static double distance(double lon1, double lat1, double lon2, double lat2, double earthRadius) {

		double radLat1 = Math.toRadians(lat1);
		double radLat2 = Math.toRadians(lat2);

		double radLon1 = Math.toRadians(lon1);
		double radLon2 = Math.toRadians(lon2);

		double a = radLat1 - radLat2;
		double b = radLon1 - radLon2;

		double c = 2.0D * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2.0D), 2.0D)
				+ Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2.0D), 2.0D)));

		return c * earthRadius;
	}
}
