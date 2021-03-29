package net.bytepuppy.redefine.delegate.office;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

/**
 *
 * 有示例显示下面的代码也能工作，实验发现：
 *  将Bar、Foo的方法改成静态方法就可以生效，非静态方法则报错
 *
 * IllegalArgumentException: None of [] allows for delegation from public
 *              java.lang.String net.bytepuppy.redefine.delegate.office.Bar.m()
 *
 * @author liuh
 * @date 2021/3/26
 */
public class RedefineVariantDemo {

    public static void main(String[] args) throws Exception {
        ByteBuddyAgent.install();
        // redefine Bar.class to Foo.class
        // all Foo instance is Bar implementation
        Bar bar = new ByteBuddy()
                .redefine(Bar.class)
                .method(ElementMatchers.named("m"))
                .intercept(MethodDelegation.to(Foo.class))
                .make()
                .load(Foo.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent())
                .getLoaded()
                .newInstance();

        System.out.println(bar.m());
    }
}
