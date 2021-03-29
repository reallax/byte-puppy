package net.bytepuppy.redefine.advice.simple;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;

/**
 *
 * @author liuh
 * @date 2021/3/26
 */
public class ByteBuddyAdviceMain {

    public static void main(String[] args) throws Exception {
        System.out.println(" ------- before advice ------- ");
        String returnVal = (new Robot()).greetUser("John");
        System.out.println("return value: " + returnVal);

        System.out.println(" -------- after advice -------- ");
        Class<?> type = new ByteBuddy()
                .redefine(Robot.class)
                .visit(Advice.to(MyAdvices.class).on(ElementMatchers.isMethod()))
                .make()
                .load(ClassLoadingStrategy.BOOTSTRAP_LOADER, ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();

        // use java reflection to invoke the method
         returnVal = (String)type.getDeclaredMethod("greetUser", String.class)
                 .invoke(type.getDeclaredConstructor().newInstance(), "John");
        System.out.println("return value: " + returnVal);
    }
}
