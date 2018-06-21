/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.util;

/**
 * 用于提供与字节计算相关的工具方法
 * 
 * @author lei
 */
public class ByteUtils {

	/* CRC-16/IBM： 多项式:X16+X15+X2+1, 生成码为 0x8005. */
	private static final int[] CRC16Table = new int[] { 0x0000, 0x8005, 0x800f, 0x000a, 0x801b, 0x001e, 0x0014, 0x8011,
			0x8033, 0x0036, 0x003c, 0x8039, 0x0028, 0x802d, 0x8027, 0x0022, 0x8063, 0x0066, 0x006c, 0x8069, 0x0078,
			0x807d, 0x8077, 0x0072, 0x0050, 0x8055, 0x805f, 0x005a, 0x804b, 0x004e, 0x0044, 0x8041, 0x80c3, 0x00c6,
			0x00cc, 0x80c9, 0x00d8, 0x80dd, 0x80d7, 0x00d2, 0x00f0, 0x80f5, 0x80ff, 0x00fa, 0x80eb, 0x00ee, 0x00e4,
			0x80e1, 0x00a0, 0x80a5, 0x80af, 0x00aa, 0x80bb, 0x00be, 0x00b4, 0x80b1, 0x8093, 0x0096, 0x009c, 0x8099,
			0x0088, 0x808d, 0x8087, 0x0082, 0x8183, 0x0186, 0x018c, 0x8189, 0x0198, 0x819d, 0x8197, 0x0192, 0x01b0,
			0x81b5, 0x81bf, 0x01ba, 0x81ab, 0x01ae, 0x01a4, 0x81a1, 0x01e0, 0x81e5, 0x81ef, 0x01ea, 0x81fb, 0x01fe,
			0x01f4, 0x81f1, 0x81d3, 0x01d6, 0x01dc, 0x81d9, 0x01c8, 0x81cd, 0x81c7, 0x01c2, 0x0140, 0x8145, 0x814f,
			0x014a, 0x815b, 0x015e, 0x0154, 0x8151, 0x8173, 0x0176, 0x017c, 0x8179, 0x0168, 0x816d, 0x8167, 0x0162,
			0x8123, 0x0126, 0x012c, 0x8129, 0x0138, 0x813d, 0x8137, 0x0132, 0x0110, 0x8115, 0x811f, 0x011a, 0x810b,
			0x010e, 0x0104, 0x8101, 0x8303, 0x0306, 0x030c, 0x8309, 0x0318, 0x831d, 0x8317, 0x0312, 0x0330, 0x8335,
			0x833f, 0x033a, 0x832b, 0x032e, 0x0324, 0x8321, 0x0360, 0x8365, 0x836f, 0x036a, 0x837b, 0x037e, 0x0374,
			0x8371, 0x8353, 0x0356, 0x035c, 0x8359, 0x0348, 0x834d, 0x8347, 0x0342, 0x03c0, 0x83c5, 0x83cf, 0x03ca,
			0x83db, 0x03de, 0x03d4, 0x83d1, 0x83f3, 0x03f6, 0x03fc, 0x83f9, 0x03e8, 0x83ed, 0x83e7, 0x03e2, 0x83a3,
			0x03a6, 0x03ac, 0x83a9, 0x03b8, 0x83bd, 0x83b7, 0x03b2, 0x0390, 0x8395, 0x839f, 0x039a, 0x838b, 0x038e,
			0x0384, 0x8381, 0x0280, 0x8285, 0x828f, 0x028a, 0x829b, 0x029e, 0x0294, 0x8291, 0x82b3, 0x02b6, 0x02bc,
			0x82b9, 0x02a8, 0x82ad, 0x82a7, 0x02a2, 0x82e3, 0x02e6, 0x02ec, 0x82e9, 0x02f8, 0x82fd, 0x82f7, 0x02f2,
			0x02d0, 0x82d5, 0x82df, 0x02da, 0x82cb, 0x02ce, 0x02c4, 0x82c1, 0x8243, 0x0246, 0x024c, 0x8249, 0x0258,
			0x825d, 0x8257, 0x0252, 0x0270, 0x8275, 0x827f, 0x027a, 0x826b, 0x026e, 0x0264, 0x8261, 0x0220, 0x8225,
			0x822f, 0x022a, 0x823b, 0x023e, 0x0234, 0x8231, 0x8213, 0x0216, 0x021c, 0x8219, 0x0208, 0x820d, 0x8207,
			0x0202 };

	/******************************************************
	 * 函数名称:CRC16 输 入:pszBuf 要校验的数据 unLength 校验数据的长 输 出:校验值 功 能:查表法计算CRC16
	 * （美国标准-0x8005）
	 *******************************************************/
	public static short CRC16_TABLE(byte[] aData, int aSize) {
		short nAccum = 0;

		for (int i = 0; i < aSize; i++)
			nAccum = (short) ((nAccum << 8) ^ CRC16Table[((nAccum >>> 8) ^ aData[i]) & 0x00FF]);
		
		return nAccum;
	}

	/**
	 * 计算给定数据的检验和
	 * 
	 * @param data
	 * @return
	 */
	public static int checkSum16(byte[] data) {
		return checkSum16(data, null, 0, data.length);
	}

	/**
	 * 计算给定数据的检验和
	 * 
	 * @param data
	 *            要计算校验和的数据内容
	 * @param key
	 *            密钥
	 * @param fromIdx
	 * @param toIdx
	 * @return
	 */
	public static int checkSum16(byte[] data, byte[] key, int fromIdx, int toIdx) {

		int tSum = 0;

		for (int i = fromIdx, s = data.length; i < s && i < toIdx; i++) {
			tSum += (short) data[i] & 0xff;
		}

		// 密钥
		if (key != null && key.length > 0) {
			for (int i = 0; i < key.length; i++) {
				tSum += (short) key[i] & 0xff;
			}
		}

		return (tSum >> 16) + (tSum & 0xffff);
	}

	/**
	 * 计算给定数据的检验码(异或)
	 * 
	 * @param data
	 * @return
	 */
	public static short checkXOR(final byte[] data) {
		return checkXOR(data, null, 0, data.length);
	}

	/**
	 * 计算给定数据的检验码(异或)
	 * 
	 * @param data
	 *            要计算异或校验码的数据内容
	 * @param key
	 *            密钥
	 * @param fromIdx
	 *            起始索引(含)
	 * @param toIdx
	 *            截止索引(不含)
	 * @return
	 */
	public static short checkXOR(final byte[] data, final byte[] key, int fromIdx, int toIdx) {

		short tVal = 0x0;

		for (int i = fromIdx, s = data.length; i < s && i < toIdx; i++) {
			tVal ^= data[i] & 0xFF;
		}

		// 密钥
		if (key != null && key.length > 0) {
			for (int i = 0; i < key.length; i++) {
				tVal ^= key[i] & 0xFF;
			}
		}

		return tVal;
	}

	/**
	 * 将给定的字节数组转换为16进制字符串形式
	 * 
	 * @return
	 */
	public static String getHexByteString(final short[] dataBytes) {

		StringBuffer strBuf = new StringBuffer();
		if (dataBytes != null && dataBytes.length > 0) {
			for (Short data : dataBytes) {
				strBuf.append(String.format("%02X", data));
			}
		}

		return strBuf.toString();
	}

	/**
	 * 将给定的字节数组转换为16进制字符串形式
	 * 
	 * @return
	 */
	public static String getHexByteString(final byte[] dataBytes) {

		StringBuffer strBuf = new StringBuffer();
		if (dataBytes != null && dataBytes.length > 0) {
			for (byte data : dataBytes) {
				strBuf.append(String.format("%02X", data));
			}
		}

		return strBuf.toString();
	}

	/**
	 * 将给定的整数值转换为字节数组；遵循小端模式
	 * 
	 * @return
	 */
	public static byte[] getBytes(int intVal, byte[] valBytes) {

		for (int i = 0; valBytes != null && i < valBytes.length; i++) {
			valBytes[i] = (byte) ((intVal >>> (8 * i)) & 0xFF);
		}

		return valBytes;
	}

	/**
	 * 将给定的整数值转换为字节数组；遵循大端模式
	 * 
	 * @return
	 */
	public static byte[] getBytes4Bigendian(int intVal, byte[] valBytes) {

		for (int i = 0; valBytes != null && i < valBytes.length; i++) {
			valBytes[valBytes.length - 1 - i] = (byte) ((intVal >>> (8 * i)) & 0xFF);
		}

		return valBytes;
	}

	/**
	 * 从给定的数组缓存中获取整数值；遵循小端模式
	 * 
	 * @param data
	 * @param fromIdx
	 * @param toIdx
	 * @return
	 */
	public static int getInt(byte[] data) {

		if (data == null) {
			throw new IllegalArgumentException("arguments is invalid");
		}

		return getInt(data, 0, data.length);
	}

	/**
	 * 从给定的数组缓存中获取整数值；遵循小端模式
	 * 
	 * @param data
	 * @param fromIdx
	 * @param toIdx
	 * @return
	 */
	public static int getInt(byte[] data, int fromIdx, int toIdx) {

		if (data == null || fromIdx > toIdx || toIdx > data.length) {
			throw new IllegalArgumentException("arguments is invalid");
		}
		if (toIdx - fromIdx > 4) {
			throw new IllegalArgumentException("arguments is invalid");
		}

		int intVal = 0x0;

		// 判断是否是负数
		boolean isNegative = ((data[toIdx - 1] & 0x80) == 0x80);

		// 负数采用补码算法
		if (isNegative) {

			for (int i = fromIdx; i < toIdx && i < fromIdx + 4; i++) {
				intVal ^= (~data[i] & 0xFF) << (8 * (i - fromIdx));
			}
			intVal = (intVal + 1) * -1;
		}

		// 正数直接返回原码
		else {

			for (int i = fromIdx; i < toIdx && i < fromIdx + 4; i++) {
				intVal ^= (data[i] & 0xFF) << (8 * (i - fromIdx));
			}
		}

		return intVal;
	}

	/**
	 * 从给定的数组缓存中获取整数值；遵循大端模式
	 * 
	 * @param data
	 * @return
	 */
	public static int getInt4Bigendian(byte[] data) {

		if (data == null) {
			throw new IllegalArgumentException("arguments is invalid");
		}

		return getInt4Bigendian(data, 0, data.length);
	}

	/**
	 * 从给定的数组缓存中获取整数值；遵循大端模式
	 * 
	 * @param data
	 * @param fromIdx
	 * @param toIdx
	 * @return
	 */
	public static int getInt4Bigendian(byte[] data, int fromIdx, int toIdx) {

		if (data == null || fromIdx > toIdx || toIdx > data.length) {
			throw new IllegalArgumentException("arguments is invalid");
		}
		if (toIdx - fromIdx > 4) {
			throw new IllegalArgumentException("arguments is invalid");
		}

		int intVal = 0x0;

		// 判断是否是负数
		boolean isNegative = ((data[fromIdx] & 0x80) == 0x80);

		// 负数采用补码算法
		if (isNegative) {

			for (int i = fromIdx; i < toIdx && i < fromIdx + 4; i++) {
				intVal ^= (~data[i] & 0xFF) << (8 * (toIdx - 1 - i));
			}
			intVal = (intVal + 1) * -1;
		}

		// 正数直接返回原码
		else {

			for (int i = fromIdx; i < toIdx && i < fromIdx + 4; i++) {
				intVal ^= (data[i] & 0xFF) << (8 * (toIdx - 1 - i));
			}
		}

		return intVal;
	}

	/**
	 * 从给定的数组缓存中获取整数值；遵循小端模式
	 * 
	 * @param data
	 * @param fromIdx
	 * @param toIdx
	 * @return
	 */
	public static long getUnsignedInt(byte[] data) {

		if (data == null) {
			throw new IllegalArgumentException("arguments is invalid");
		}

		return getUnsignedInt(data, 0, data.length);
	}

	/**
	 * 从给定的数组缓存中获取整数值；遵循小端模式
	 * 
	 * @param data
	 * @param fromIdx
	 * @param toIdx
	 * @return
	 */
	public static long getUnsignedInt(byte[] data, int fromIdx, int toIdx) {

		if (data == null || fromIdx > toIdx || toIdx > data.length) {
			throw new IllegalArgumentException("arguments is invalid");
		}
		if (toIdx - fromIdx > 4) {
			throw new IllegalArgumentException("arguments is invalid");
		}

		int intVal = 0x0;
		for (int i = fromIdx; i < toIdx && i < fromIdx + 4; i++) {
			intVal ^= (data[i] & 0xFF) << (8 * (i - fromIdx));
		}

		return intVal & 0xFFFFFFFFL;
	}

	/**
	 * 从给定的数组缓存中获取整数值；遵循大端模式
	 * 
	 * @param data
	 * @return
	 */
	public static long getUnsignedInt4Bigendian(byte[] data) {

		if (data == null) {
			throw new IllegalArgumentException("arguments is invalid");
		}

		return getUnsignedInt4Bigendian(data, 0, data.length);
	}

	/**
	 * 从给定的数组缓存中获取整数值；遵循大端模式
	 * 
	 * @param data
	 * @param fromIdx
	 * @param toIdx
	 * @return
	 */
	public static long getUnsignedInt4Bigendian(byte[] data, int fromIdx, int toIdx) {

		if (data == null || fromIdx > toIdx || toIdx > data.length) {
			throw new IllegalArgumentException("arguments is invalid");
		}
		if (toIdx - fromIdx > 4) {
			throw new IllegalArgumentException("arguments is invalid");
		}

		int intVal = 0x0;
		for (int i = fromIdx; i < toIdx && i < fromIdx + 4; i++) {
			intVal ^= (data[i] & 0xFF) << (8 * (toIdx - 1 - i));
		}

		return intVal & 0xFFFFFFFFL;
	}

	/**
	 * 从给定的数组缓存中获取整数值；遵循小端模式
	 * 
	 * @param data
	 * @param fromIdx
	 * @param toIdx
	 * @return
	 */
	public static short getShort(byte[] data) {

		if (data == null) {
			throw new IllegalArgumentException("arguments is invalid");
		}

		return getShort(data, 0, data.length);
	}

	/**
	 * 从给定的数组缓存中获取整数值；遵循小端模式
	 * 
	 * @param data
	 * @param fromIdx
	 * @param toIdx
	 * @return
	 */
	public static short getShort(byte[] data, int fromIdx, int toIdx) {

		if (data == null || fromIdx > toIdx || toIdx > data.length) {
			throw new IllegalArgumentException("arguments is invalid");
		}
		if (toIdx - fromIdx > 2) {
			throw new IllegalArgumentException("arguments is invalid");
		}

		short intVal = 0x0;

		// 判断是否是负数
		boolean isNegative = ((data[toIdx - 1] & 0x80) == 0x80);

		// 负数采用补码算法
		if (isNegative) {

			for (int i = fromIdx; i < toIdx && i < fromIdx + 2; i++) {
				intVal ^= (~data[i] & 0xFF) << (8 * (i - fromIdx));
			}
			intVal = (short) ((intVal + 1) * -1);
		}

		// 正数直接返回原码
		else {

			for (int i = fromIdx; i < toIdx && i < fromIdx + 2; i++) {
				intVal ^= (data[i] & 0xFF) << (8 * (i - fromIdx));
			}
		}

		return intVal;
	}

	/**
	 * 从给定的数组缓存中获取整数值；遵循大端模式
	 * 
	 * @param data
	 * @return
	 */
	public static short getShort4Bigendian(byte[] data) {

		if (data == null) {
			throw new IllegalArgumentException("arguments is invalid");
		}

		return getShort4Bigendian(data, 0, data.length);
	}

	/**
	 * 从给定的数组缓存中获取整数值；遵循大端模式
	 * 
	 * @param data
	 * @param fromIdx
	 * @param toIdx
	 * @return
	 */
	public static short getShort4Bigendian(byte[] data, int fromIdx, int toIdx) {

		if (data == null || fromIdx > toIdx || toIdx > data.length) {
			throw new IllegalArgumentException("arguments is invalid");
		}
		if (toIdx - fromIdx > 2) {
			throw new IllegalArgumentException("arguments is invalid");
		}

		short intVal = 0x0;

		// 判断是否是负数
		boolean isNegative = ((data[fromIdx] & 0x80) == 0x80);

		// 负数采用补码算法
		if (isNegative) {

			for (int i = fromIdx; i < toIdx && i < fromIdx + 2; i++) {
				intVal ^= (~data[i] & 0xFF) << (8 * (toIdx - 1 - i));
			}
			intVal = (short) ((intVal + 1) * -1);
		}

		// 正数直接返回原码
		else {

			for (int i = fromIdx; i < toIdx && i < fromIdx + 2; i++) {
				intVal ^= (data[i] & 0xFF) << (8 * (toIdx - 1 - i));
			}
		}

		return intVal;
	}

	/**
	 * 从给定的数组缓存中获取整数值；遵循小端模式
	 * 
	 * @param data
	 * @param fromIdx
	 * @param toIdx
	 * @return
	 */
	public static int getUnsignedShort(byte[] data) {

		if (data == null) {
			throw new IllegalArgumentException("arguments is invalid");
		}

		return getUnsignedShort(data, 0, data.length);
	}

	/**
	 * 从给定的数组缓存中获取整数值；遵循小端模式
	 * 
	 * @param data
	 * @param fromIdx
	 * @param toIdx
	 * @return
	 */
	public static int getUnsignedShort(byte[] data, int fromIdx, int toIdx) {

		if (data == null || fromIdx > toIdx || toIdx > data.length) {
			throw new IllegalArgumentException("arguments is invalid");
		}
		if (toIdx - fromIdx > 2) {
			throw new IllegalArgumentException("arguments is invalid");
		}

		short intVal = 0x0;
		for (int i = fromIdx; i < toIdx && i < fromIdx + 4; i++) {
			intVal ^= (data[i] & 0xFF) << (8 * (i - fromIdx));
		}

		return intVal & 0xFFFF;
	}

	/**
	 * 从给定的数组缓存中获取整数值；遵循大端模式
	 * 
	 * @param data
	 * 
	 * @return
	 */
	public static int getUnsignedShort4Bigendian(byte[] data) {

		if (data == null) {
			throw new IllegalArgumentException("arguments is invalid");
		}

		return getUnsignedShort4Bigendian(data, 0, data.length);
	}

	/**
	 * 从给定的数组缓存中获取整数值；遵循大端模式
	 * 
	 * @param data
	 * @param fromIdx
	 * @param toIdx
	 * @return
	 */
	public static int getUnsignedShort4Bigendian(byte[] data, int fromIdx, int toIdx) {

		if (data == null || fromIdx > toIdx || toIdx > data.length) {
			throw new IllegalArgumentException("arguments is invalid");
		}
		if (toIdx - fromIdx > 2) {
			throw new IllegalArgumentException("arguments is invalid");
		}

		short intVal = 0x0;
		for (int i = fromIdx; i < toIdx && i < fromIdx + 4; i++) {
			intVal ^= (data[i] & 0xFF) << (8 * (toIdx - 1 - i));
		}

		return intVal & 0xFFFF;
	}

	/**
	 * 从给定的数组缓存中获取片断；
	 * 
	 * @param data
	 * @param fromIdx
	 * @param toIdx
	 * 
	 * @return
	 */
	public static byte[] getRange(byte[] data, int fromIdx, int toIdx) {

		if (data == null || fromIdx > toIdx || toIdx > data.length) {
			throw new IllegalArgumentException("arguments is invalid");
		}

		byte[] range = new byte[toIdx - fromIdx];

		System.arraycopy(data, fromIdx, range, 0, toIdx - fromIdx);

		return range;
	}

	/**
	 * 使用给定的源数据来顺序填充目标数组
	 * 
	 * @param dest
	 * @param srcArys
	 * @return
	 */
	public static byte[] fill(byte[] dest, byte[]... srcArys) {
		return fill(dest, 0, srcArys);
	}

	/**
	 * 使用给定的源数据来顺序填充目标数组
	 * 
	 * @param dest
	 * @param offsetIdx
	 * @param srcArys
	 * @return
	 */
	public static byte[] fill(byte[] dest, int offsetIdx, byte[]... srcArys) {

		if (dest != null && srcArys != null && srcArys.length > 0) {

			for (int i = 0, copiedDataLength = 0; i < srcArys.length; i++) {

				if (srcArys[i] != null) {
					System.arraycopy(srcArys[i], 0, dest, offsetIdx + copiedDataLength, srcArys[i].length);
					copiedDataLength += srcArys[i].length;
				}
			}
		}

		return dest;
	}
	
	/**
	 * int 转换成byte数组
	 * 
	 * @param i
	 *            所要转换的int 由高位到低位
	 * @return byte数组
	 */
	public static byte[] shortToByte(short number) {
		byte[] b = new byte[2];
		b[0] = (byte) (number >>> 8 & 0x00FF);
		b[1] = (byte) (number >>> 0 & 0x00FF);
		return b;
	}
}
