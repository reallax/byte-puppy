package net.bytepuppy.redefine.advice;


import com.google.common.collect.Lists;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import java.lang.instrument.ClassFileTransformer;
import java.util.List;

/**
 * 断点进不去，时间不正确
 *
 * @author liuh
 * @date 2021/3/26
 */
public class AdviceRedefineMain2 {

    public static void main(String[] args) throws Exception {
        ByteBuddyAgent.install();

        ClassFileTransformer classFileTransformer = new AgentBuilder.Default()
                .with(AgentBuilder.PoolStrategy.Default.EXTENDED)
                .with(AgentBuilder.InitializationStrategy.NoOp.INSTANCE)
                //.ignore()
                // 设定匹配范围
                .type(ElementMatchers.is(ComputeService.class))
                .transform(new AgentBuilder.Transformer() {

                    @Override
                    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder,
                                                            TypeDescription typeDescription,
                                                            ClassLoader classLoader, JavaModule module) {
                        return builder.visit(Advice.to(AdviceTemplate.class)
                                .on(ElementMatchers.isMethod()));
                    }
                })
                .installOnByteBuddyAgent();


        Class<ComputeService> clazz = (Class<ComputeService>) ClassLoader
                .getSystemClassLoader().loadClass(ComputeService.class.getName());
        Object service = clazz.getDeclaredConstructor().newInstance();

        Object result = clazz.getMethod("compute", String.class, List.class)
                .invoke(service, "AdviceDemo", Lists.newArrayList(1, 2, 4));
        System.out.println(result);

//        ((ComputeService) service).compute("AdviceDemo", Lists.newArrayList(1, 2, 4));

    }
}
