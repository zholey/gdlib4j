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

	private static double rad(double d) {
		return d * 3.141592653589793D / 180.0D;
	}

	/**
	 * 计算两个经纬度间的距离；（单位：可能是米）
	 * 
	 * @param lon1
	 * @param lat1
	 * @param lon2
	 * @param lat2
	 * @return
	 */
	public static double distance(double lon1, double lat1, double lon2, double lat2) {

		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);

		double a = radLat1 - radLat2;
		double b = rad(lon1) - rad(lon2);

		double c = 2.0D * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2.0D), 2.0D)
				+ Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2.0D), 2.0D)));

		return c * 6371004.0D;
	}

	// public static double area() {
	//
	// iArea = iArea + (vecPoly[iCycle].x * vecPoly[(iCycle+1) % iCount].y -
	// vecPoly[(iCycle+1) % iCount].x * vecPoly[iCycle].y);
	// int intAreaCalc(vector<myPoint> &vecPoly)
	// {
	// int iCycle,iCount,iArea;
	// iCycle=0;
	// iArea=0;
	// iCount=vecPoly.size();
	//
	// for(iCycle=0;iCycle<iCount;iCycle++)
	// {
	// iArea=iArea+(vecPoly[iCycle].x*vecPoly[(iCycle+1) %
	// iCount].y-vecPoly[(iCycle+1) % iCount].x*vecPoly[iCycle].y);
	// }
	//
	// return abs(0.5*iArea);
	// }
	// }
}
