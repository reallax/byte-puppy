package net.bytepuppy.redefine.advice.simple;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.InvocationTargetException;
import java.util.Timer;
import java.util.TimerTask;

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
        Object enhancer = type.getDeclaredConstructor().newInstance();
        returnVal = (String) type.getDeclaredMethod("greetUser", String.class)
                 .invoke(enhancer, "John");
        System.out.println("return value: " + returnVal);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                scheduleForArthas(type, enhancer);
                scheduleBeforeAdvice(new Robot());
            }
        }, 1000, 1000);
    }

    /**
     * trace net.bytepuppy.redefine.advice.simple.ByteBuddyAdviceMain scheduleForArthas
     *
     * byte buddy advice enhance class invoke stack still invisible for arthas enhance trace
     * byte buddy advice enhance class invoke can not be debug either.
     *      see <a>https://github.com/raphw/byte-buddy/issues/739</a>
     *
     * @param type
     * @param enhancer
     */
    private static void scheduleForArthas(Class<?> type, Object enhancer) {
        String val = null;
        try {
            val = (String)type.getDeclaredMethod("greetUser", String.class)
                    .invoke(enhancer, "John");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        System.out.println("return value: " + val);
    }

    /**
     * trace net.bytepuppy.redefine.advice.simple.ByteBuddyAdviceMain scheduleBeforeAdvice
     *
     * @param robot
     */
    private static void scheduleBeforeAdvice(Robot robot) {
        System.out.println("invoke before advice" + robot.greetUser("reallx"));
    }
}
