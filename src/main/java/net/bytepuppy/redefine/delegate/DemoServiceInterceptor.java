package net.bytepuppy.redefine.delegate;

import java.lang.reflect.Method;

/**
 * DemoService增强切面
 *
 * @author liuh
 * @date 2021/3/25
 */
public class DemoServiceInterceptor implements InstMethodAroundInterceptor {
    @Override
    public void beforeMethod(Object inst, Method interceptPoint, Object[] allArguments,
                             Class<?>[] argumentsTypes, ResultWrapper result) {
        System.out.println("DemoService Interceptor in ...");
    }

    @Override
    public Object afterMethod(Object inst, Method interceptPoint, Object[] allArguments,
                              Class<?>[] argumentsTypes, Object ret) {
        System.out.println("DemoService Interceptor out ...");
        return ret;
    }

    @Override
    public void handleMethodException(Object inst, Method method, Object[] allArguments,
                                      Class<?>[] argumentsTypes, Throwable t) {
        System.out.println("DemoService Interceptor error handle ...");
    }
}
