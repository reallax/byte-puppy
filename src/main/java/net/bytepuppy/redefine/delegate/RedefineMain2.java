package net.bytepuppy.redefine.delegate;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.pool.TypePool;
import net.bytepuppy.Consts;

/**
 *
 * 错误实例2：
 *  解释见{@link RedefineMain1}
 *
 * reference：https://bytebuddy.net/#/tutorial ## Working with unloaded classes ##
 *
 * @author liuh
 * @date 2021/3/25
 */
public class RedefineMain2 {


    public static void main(String[] args) throws Exception {
        DynamicType.Unloaded unloaded = createWithoutTriggerClassLoad();

        unloaded.saveIn(Consts.newFile(Consts.CLASS_OUTPUT_BASE_DIR));
        Object demoService = unloaded.load(ClassLoader.getSystemClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded().newInstance();
        Object o = demoService.getClass()
                .getMethod("report", String.class, int.class)
                .invoke(demoService, "reallx", 12);
        System.out.println(
                        o.toString());
        System.out.println(demoService.getClass().getDeclaredField("qux"));
    }

    private static DynamicType.Unloaded createWithoutTriggerClassLoad() {
        TypePool typePool = TypePool.Default.ofSystemLoader();
        DynamicType.Unloaded unloaded = new ByteBuddy()
                // try rebase
                .rebase(typePool.describe("net.bytepuppy.redefine.delegate.DemoService").resolve(),
                        ClassFileLocator.ForClassLoader.ofSystemLoader())
                // 如果用ClassLoadingStrategy.Default.WRAPPER，那必须为新类指定一个名字，否则在相同ClassLoader中名字冲突
                // ClassLoadingStrategy.Default.CHILD_FIRST，name定义可以省略
                .name("WhatEver")
                .defineField("qux", String.class)
                .method(ElementMatchers.named("report"))
                .intercept(MethodDelegation.to(new DelegateTemplate(new DemoServiceInterceptor())))
                .make();

        return unloaded;
    }
}
