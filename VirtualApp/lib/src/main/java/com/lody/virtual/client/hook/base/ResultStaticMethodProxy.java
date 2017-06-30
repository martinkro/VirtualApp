package com.lody.virtual.client.hook.base;

import java.lang.reflect.Method;
import io.virtualapp.lib.utils.LogHelper;

/**
 * @author Lody
 */

public class ResultStaticMethodProxy extends StaticMethodProxy {

	Object mResult;

	public ResultStaticMethodProxy(String name, Object result) {
		super(name);
		mResult = result;
	}

	public Object getResult() {
		return mResult;
	}

	@Override
	public Object call(Object who, Method method, Object... args) throws Throwable {
		LogHelper.Debug("ResultStaticMethodProxy method name:" + getMethodName());
		return mResult;
	}
}
