package net.bytepuppy.redefine.delegate;

import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * 代理模板
 *
 * @author liuh
 * @date 2021/3/25
 */
public class DelegateTemplate {

    private InstMethodAroundInterceptor interceptor;

    public DelegateTemplate(InstMethodAroundInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    /**
     * 拦截增强主方法
     *
     * @param inst
     * @param allArguments
     * @param zuper
     * @param method
     * @return
     */
    public Object interceptor(@This Object inst, @AllArguments Object[] allArguments,
                              @SuperCall Callable<?> zuper, @Origin Method method) {
        ResultWrapper rw = new ResultWrapper();
        if (this.interceptor != null) {
            try {
                // 调用前拦截处理
                this.interceptor.beforeMethod(inst, method,
                        allArguments, method.getParameterTypes(), rw);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        if (!rw.isContinue()) {
            return rw.getResult();
        }

        Object result = null;
        try {
            // 被代理方法调用
            result = zuper.call();

            if (this.interceptor != null) {
                try {
                    // 调用后拦截处理
                    result = this.interceptor.afterMethod(inst, method,
                            allArguments, method.getParameterTypes(), result);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        } catch (Exception e) {
            if (this.interceptor != null) {
                try {
                    // 调用异常拦截处理
                    this.interceptor.handleMethodException(inst, method,
                            allArguments, method.getParameterTypes(), e);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }

        return result;
    }
}
