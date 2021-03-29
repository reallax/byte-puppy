package net.bytepuppy.redefine.advice.simple;

import net.bytebuddy.asm.Advice;

/**
 * @Advice注解参考：
 *  https://blog.csdn.net/wanxiaoderen/article/details/107367250
 *
 * @author liuh
 * @date 2021/3/26
 */
public class MyAdvices {
    @Advice.OnMethodEnter(suppress = Throwable.class)
    static long enter(@Advice.This Object thisObject,
                      @Advice.Origin String origin,
                      @Advice.Origin("#t #m") String detaildOrigin,
                      @Advice.AllArguments Object[] ary,
                      @Advice.FieldValue(value = "name", readOnly = false) String nameField){

        System.out.println("Inside enter method ...");

        if(ary != null) {
            for(int i =0 ; i < ary.length ; i++){
                System.out.println("Argument: " + i + " is " + ary[i]);
            }
        }

        System.out.println("Origin :" + origin);
        System.out.println("Detailed Origin :" + detaildOrigin);

        nameField = "Jack";
        return System.nanoTime();
    }

    @Advice.OnMethodExit(suppress = Throwable.class, onThrowable = Throwable.class)
    static void exit(@Advice.Enter long time){
        System.out.println("Inside exit method ...");
        System.out.println("Method Execution Time: " + (System.nanoTime() - time) + " nano seconds");
    }
}
