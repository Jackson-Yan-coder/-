package com.jfinal.wxaapp.api;

import java.util.Objects;
import java.util.function.Supplier;
import com.jfinal.wxaapp.WxaConfigKit;

/**
 * Wxa 工具类可以用于任意开发环境，既不限于 web 环境，也不限于 jfinal 环境，
 * 利用 use 方法代替拦截器设定 appid
 * 
 * 使用示例：
 * <pre>
    1：指定 appId 用法：
    ApiResult ret = Wxa.use(appId).call(() -> {
		return WxaUserApi.getSessionKey(code);
	});
	
	使用默认 appId 用法：
	ApiResult ret = Wxa.use().call(() -> {
		return WxaUserApi.getSessionKey(code);
	});
	
 * </pre>
 * 
 * @author 山东小木
 * 
 */
public class Wxa {
	
	protected String appId;
	
	public static Wxa use(String appId) {
		Objects.requireNonNull(appId, "appId can not be null");
		return new Wxa(appId);
	}
	
	public static Wxa use() {
		return new Wxa(null);
	}
	
	Wxa(String appId) {
		this.appId = appId;
	}
	
	public <T> T call(Supplier<T> function) {
		if (appId != null) {
			try {
				WxaConfigKit.setThreadLocalAppId(appId);
				return function.get();
			} finally {
				WxaConfigKit.removeThreadLocalAppId();
			}
		}
		else {
			return function.get();
		}
	}
}



