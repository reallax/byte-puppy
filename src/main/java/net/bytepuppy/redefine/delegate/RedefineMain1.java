package net.bytepuppy.redefine.delegate;

import com.google.common.collect.Lists;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytepuppy.Consts;

/**
 * 错误示例1：
 * redefine和rebase都是对已有类进行修改。
 * 在正常JVM启动（这里是main函数）中，DemoService已被加载
 * 被redefine的DemoService，不能再被加载，会报错
 *
 * redefine：重新定义一个类，被增强的方法、属性，会丢失原方法、属性
 * rebase：与redefine相似，但被增强的方法、属性不会丢失，而是会已拷贝 + 重命名的方式被保留
 *
 * 另外：JVM热加载时，禁止修改已有类的schema（方法、属性，但可以修改逻辑片段）
 * 因此：
 *  redefine：MethodDelegate无效。因为redefine会丢失原方法，@SuperCall调用父类方法找不着了。参考{@link RedefineMain2}
 *  rebase：MethodDelegate无效。因为rebase拷贝、重命名原有方法，会新增方法，破坏了热加载规则，代理失效。参考{@link RedefineMain2}
 *  # reference: https://github.com/raphw/byte-buddy/issues/104
 *
 *
 *
 * @author liuh
 * @date 2021/3/25
 */
public class RedefineMain1 {


    public static void main(String[] args) throws Exception {
        DynamicType.Unloaded<DemoService> unloaded = errorCreateDemo();

        unloaded.saveIn(Consts.newFile(Consts.CLASS_OUTPUT_BASE_DIR));
        // 试着将 ClassLoadingStrategy.Default.WRAPPER 改成ClassLoadingStrategy.Default.CHILD_FIRST
        // 报错：java.lang.ClassCastException: net.bytepuppy.redefine.delegate.DemoService cannot be cast to net.bytepuppy.redefine.delegate.DemoService
        // WRAPPER是创建给定ClassLoader的子类，所有类相互可见；CHILD_FIRST刚好相反，子ClassLoad加载的类，对BootstrapClassLoader、AppClassLoader加载的类不可见
        DemoService demoService = unloaded.load(ClassLoader.getSystemClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded().newInstance();
        System.out.println(demoService.report("reallx", 12));
        demoService.compute(Lists.newArrayList(1, 2, 3, 4, 5));
    }

    /**
     * DemoService.class语句，触发DemoService被JVM加载，不能使用redefine命令
     *
     * 报错：
     *  java.lang.IllegalStateException: Class already loaded: class net.bytepuppy.redefine.delegate.DemoService
     * @return
     */
    private static DynamicType.Unloaded<DemoService> errorCreateDemo() {
        DynamicType.Unloaded<DemoService> unloaded = new ByteBuddy()
                // DemoService.class语句，触发DemoService被JVM加载，不能使用redefine命令。此语句触发异常。
                .redefine(DemoService.class)
                .method(ElementMatchers.named("report"))
                .intercept(MethodDelegation.to(new DelegateTemplate(new DemoServiceInterceptor())))
                .make();

        return unloaded;
    }

}
