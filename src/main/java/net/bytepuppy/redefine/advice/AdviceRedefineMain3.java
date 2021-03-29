package net.bytepuppy.redefine.advice;

import com.google.common.collect.Lists;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ByteArrayClassLoader;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import java.lang.instrument.ClassFileTransformer;
import java.util.List;

/**
 * @author liuh
 * @email hong.liu@dmall.com
 * @date 2021/3/29
 */
public class AdviceRedefineMain3 {
    public static AdviceRedefineMain3 INSTANCE = new AdviceRedefineMain3();

    ClassLoader classLoader;

    /**
     * 加载未被修改的类
     */
    public void initClassLoader() throws Exception {


        classLoader = new ByteArrayClassLoader.ChildFirst(getClass().getClassLoader(),
                ClassFileLocator.ForClassLoader.readToNames(ComputeService.class),
                ByteArrayClassLoader.PersistenceHandler.MANIFEST);


    }


    /**
     * modify
     */
    public void modifyTarget() throws Exception {
        ByteBuddyAgent.install();
        ClassFileTransformer classFileTransformer = new AgentBuilder.Default()
                .with(AgentBuilder.PoolStrategy.Default.EXTENDED)
                .with(AgentBuilder.InitializationStrategy.NoOp.INSTANCE)
                //.ignore()
                // 设定匹配范围
                .type(ElementMatchers.is(ComputeService.class), ElementMatchers.is(classLoader))
                .transform(new AgentBuilder.Transformer() {
                    @Override
                    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription,
                                                            ClassLoader classLoader, JavaModule module) {
                        // 对任何类都剩下
                        return builder.visit(Advice.to(AdviceTemplate.class)
                                .on(ElementMatchers.not(ElementMatchers.isConstructor()).and(ElementMatchers.any())));
                    }
                })
                .installOnByteBuddyAgent();


    }

    public void print() throws Exception {

        Class<ComputeService> clazz = (Class<ComputeService>) classLoader.loadClass(ComputeService.class.getName());
        Object service = clazz.getDeclaredConstructor().newInstance();
        Object result = clazz.getDeclaredMethod("compute", String.class, List.class).invoke(service, "pototo", Lists.newArrayList(1, 2, 3));
        System.out.println("compute result: " + result);

        ComputeService cs = clazz.getDeclaredConstructor().newInstance();
        cs.compute("direct", Lists.newArrayList(123, 123));

    }

    public static void main(String[] args) throws Exception {
        INSTANCE.initClassLoader();

        INSTANCE.modifyTarget();

        INSTANCE.print();

    }

}
