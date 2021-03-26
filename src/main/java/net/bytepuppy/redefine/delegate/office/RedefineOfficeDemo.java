package net.bytepuppy.redefine.delegate.office;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;

/**
 * 官方的redefine示例：
 *  可以把一个类重定义到另一个类，但是不修改类的属性、方法。
 *  增减方法、属性报错，参考{@link net.bytepuppy.redefine.delegate.RedefineMain3}。
 *
 * @author liuh
 * @date 2021/3/26
 */
public class RedefineOfficeDemo {

    public static void main(String[] args) {
        ByteBuddyAgent.install();
        // redefine Bar.class to Foo.class
        // all Foo instance is Bar implementation
        new ByteBuddy()
                .redefine(Bar.class)
                .name(Foo.class.getName())
                .make()
                .load(Foo.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());

        Foo foo = new Foo();
        System.out.println(foo.m());
    }
}
