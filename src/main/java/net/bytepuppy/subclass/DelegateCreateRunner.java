package net.bytepuppy.subclass;

import lombok.SneakyThrows;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.*;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytepuppy.Consts;
import net.bytepuppy.Runner;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * @author liuh
 * @email hong.liu@dmall.com
 * @date 2021/3/25
 */
@Runner
public class DelegateCreateRunner implements Runnable {

    @SneakyThrows
    @Override
    public void run() {
        DynamicType.Unloaded<DemoService> unloaded = new ByteBuddy()
                // 自定义类名
                .with(new NamingStrategy.AbstractBase() {
                    @Override
                    protected String name(TypeDescription superClass) {
                        return superClass.getCanonicalName() + "$$Delegate";
                    }
                })
                .subclass(DemoService.class)
                .method(ElementMatchers.named("findTarget"))
                // DemoServiceDelegation实例代理findTarget方法
                // https://bytebuddy.net/#/tutorial ## Delegating a method call ##
                .intercept(MethodDelegation.to(new DemoServiceDelegation()))
                .make();

        // 将字节码保存到指定文件
        unloaded.saveIn(Consts.newFile(Consts.CLASS_OUTPUT_BASE_DIR));
        System.out.println("save class: " + Consts.CLASS_OUTPUT_BASE_DIR + "DemoService$$Delegate");

        // 实例化代理类，并调用findTarget方法
        DemoService demoService = unloaded
                .load(DelegateCreateRunner.class.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded().newInstance();
        System.out.println((demoService).findTarget("xxxxxooooo", 99));

    }

    /**
     * 必须为静态类
     * 否则报错: java.lang.InstantiationException: net.bytepuppy.subclass.DelegateCreateRunner.DemoService$$Delegate
     * 	at java.lang.Class.newInstance(Class.java:427)
     * 	at net.bytepuppy.subclass.DelegateCreateRunner.run(DelegateCreateRunner.java:49)
     *
     * 	可能是内部类无参构造函数的问题：https://github.com/raphw/byte-buddy/issues/361
     */
    public static class DemoService {
        public String findTarget(String name, int idx) {
            return "NOT FOUND: find target in original method! name: " + name + ", idx: " + idx;
        }
    }

    public static class DemoServiceDelegation {

        /**
         * @This: 原DemoService实例本身（被代理实例）
         * @AllArguments: 被代理方法的所有入参。也可以用{@Argument(n)}指定某一个位置的参数
         * @SuperCall:
         * @Origin: 原始方法对象
         *
         * @param obj
         * @param allArguments
         * @param zuper
         * @param method
         * @return
         */
        @SneakyThrows
        public String intercept(@This Object obj,
                                @AllArguments Object[] allArguments,
                                @SuperCall Callable<?> zuper,
                                @Origin Method method) {
            System.out.println(" +++++++++++++ DemoServiceDelegation +++++++++++++++ ");
            System.out.println("@This obj: " + obj.getClass().getName());
            System.out.println("@AllArguments length: " + allArguments.length
                    + ", first args: " + (allArguments.length > 0 ? allArguments[0] : null));
            System.out.println("@Origin method: " + method.getName());
            String result = (String) zuper.call();
            System.out.println("intercept call result:" + result);
            System.out.println(" ------------- DemoServiceDelegation --------------- ");
            return "intercept works! -- " + result;
        }
    }
}
