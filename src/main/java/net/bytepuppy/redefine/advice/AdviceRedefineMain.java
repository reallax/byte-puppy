package net.bytepuppy.redefine.advice;


import com.google.common.collect.Lists;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;

import java.util.List;

/**
 * 断点进不去，时间不正确
 *
 * @author liuh
 * @date 2021/3/26
 */
public class AdviceRedefineMain {

    public static void main(String[] args) throws Exception {
        ByteBuddyAgent.install();
        DynamicType.Unloaded dtu = new ByteBuddy()
                .redefine(ComputeService.class)
                .visit(Advice.to(AdviceTemplate.class)
                        .on(ElementMatchers.named("compute")))
                .make();

        Class<?> clazz = dtu.load(ClassLoadingStrategy.BOOTSTRAP_LOADER,
                ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();

        Object service = clazz.newInstance();

        Object result = clazz.getMethod("compute", String.class, List.class)
                .invoke(service, "AdviceDemo", Lists.newArrayList(1, 2, 4));
        System.out.println(result);

//        ((ComputeService) service).compute("AdviceDemo", Lists.newArrayList(1, 2, 4));

    }
}
