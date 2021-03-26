package net.bytepuppy.redefine.delegate;

import com.google.common.collect.Lists;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.pool.TypePool;
import net.bytepuppy.Consts;

/**
 * 错误示例3：
 *  MethodDelegate会删除原方法，构建新方法，破坏了热加载类的Schema
 *
 * UnsupportedOperationException: class redefinition failed: attempted to change the schema (add/remove fields)
 *
 * @author liuh
 * @date 2021/3/25
 */
public class RedefineMain3 {


    public static void main(String[] args) throws Exception {
        DynamicType.Unloaded<DemoService> unloaded = createWithClassReloading();

        unloaded.saveIn(Consts.newFile(Consts.CLASS_OUTPUT_BASE_DIR));
        // attention: ClassReloadingStrategy.fromInstalledAgent()
        Class clazz = unloaded
                .load(ClassLoader.getSystemClassLoader(), ClassReloadingStrategy.fromInstalledAgent())
                .getLoaded();
        DemoService demoService = (DemoService) clazz.newInstance();
        System.out.println(demoService.report("reallx", 12));
        demoService.compute(Lists.newArrayList(1, 2, 3, 4, 5));
    }

    private static DynamicType.Unloaded<DemoService> createWithClassReloading() {
        // java agent wrapper, trigger class instrumentation reloading
        // why we need java agent install, reference here: https://bytebuddy.net/#/tutorial ## Reloading a class ##
        // 安装Byte Buddy的Agent，除了通过-javaagent静态安装，还可以：
        ByteBuddyAgent.install();

        DynamicType.Unloaded unloaded = new ByteBuddy()
                .redefine(DemoService.class)
                // 如果用ClassLoadingStrategy.Default.WRAPPER，那必须为新类指定一个名字，否则在相同ClassLoader中名字冲突
                // ClassLoadingStrategy.Default.CHILD_FIRST，name定义可以省略
                .name(DemoService.class.getName())
                .method(ElementMatchers.named("report"))
                .intercept(MethodDelegation.to(new DelegateTemplate(new DemoServiceInterceptor())))
                .make();

        return unloaded;
    }
}
