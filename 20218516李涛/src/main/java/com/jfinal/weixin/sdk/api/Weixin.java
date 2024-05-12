/**
 * Copyright (c) 2011-2020, James Zhan 詹波 (jfinal@126.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.jfinal.weixin.sdk.api;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Weixin 工具类可以用于任意开发环境，既不限于 web 环境，也不限于 jfinal 环境，
 * 利用 use 方法代替拦截器设定 appid
 * 
 * 使用示例：
 * <pre>
    1：指定 appId 用法：
    ApiResult ret = Weixin.use(appId).call(() -> {
		return MenuApi.getMenu();
	});
	
	使用默认 appId 用法：
	ApiResult ret = Weixin.use().call(() -> {
		return MenuApi.getMenu();
	});
	
 * </pre>
 */
public class Weixin {
	
	protected String appId;
	
	public static Weixin use(String appId) {
		Objects.requireNonNull(appId, "appId can not be null");
		return new Weixin(appId);
	}
	
	public static Weixin use() {
		return new Weixin(null);
	}
	
	Weixin(String appId) {
		this.appId = appId;
	}
	
	public <T> T call(Supplier<T> function) {
		if (appId != null) {
			try {
				ApiConfigKit.setThreadLocalAppId(appId);
				return function.get();
			} finally {
				ApiConfigKit.removeThreadLocalAppId();
			}
		}
		else {
			return function.get();
		}
	}
}



