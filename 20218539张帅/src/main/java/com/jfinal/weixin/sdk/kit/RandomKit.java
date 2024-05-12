package com.jfinal.weixin.sdk.kit;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机工具，用于生成 nonce_str 等随机字符串
 */
public class RandomKit {
	
	private static final char[] CHAR_ARRAY = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
	
	/**
	 * 生成指定长度的随机字符串
	 */
	public static String gen(int len) {
		StringBuilder salt = new StringBuilder(len);
		for (int i=0; i<len; i++) {
			salt.append(CHAR_ARRAY[ThreadLocalRandom.current().nextInt(CHAR_ARRAY.length)]);
		}
		return salt.toString();
	}
	
	/**
	 * 微信支付建议生成长度为 16 的字符串作为 nonce_str 值
	 */
	public static String genNonceStr() {
		return gen(16);
	}
}

