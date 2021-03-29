package net.bytepuppy.redefine.advice;

import net.bytebuddy.asm.Advice;

/**
 * @author liuh
 * @email hong.liu@dmall.com
 * @date 2021/3/26
 */
public class AdviceTemplate {

    private static LogInterceptor logInterceptor;
    static {
        logInterceptor = new LogInterceptor();
    }

    /**
     * @Advice.OnMethodEnter 必须是静态方法
     *
     * @param thisObject
     * @param origin
     * @param detaildOrigin
     * @param args
     * @return
     */
    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static long beforeMethod(@Advice.This Object thisObject,
                                    @Advice.Origin String origin,
                                    @Advice.Origin("#t #m") String detaildOrigin,
                                    @Advice.AllArguments Object[] args) {

        StringBuilder logBuilder = new StringBuilder();

        if(args != null) {
            for(int i =0 ; i < args.length ; i++) {
                logBuilder.append("Argument- " + i + " is: " + args[i] + ", ");
            }
            logBuilder.delete(logBuilder.length() - 2, logBuilder.length());
        }

        logInterceptor.log(logBuilder.toString());
        return System.currentTimeMillis();
    }

    /**
     * @Advice.OnMethodExit 必须是静态方法
     *
     * @param time
     * @param ret
     */
    @Advice.OnMethodExit(suppress = Throwable.class, onThrowable = Throwable.class)
    public static void afterMethod(@Advice.Enter long time, @Advice.Return Object ret) {
        System.out.println("Method Execution Cost Time: " + (System.currentTimeMillis() - time) + " mills");
    }
}
