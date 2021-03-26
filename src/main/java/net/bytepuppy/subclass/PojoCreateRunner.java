package net.bytepuppy.subclass;

import lombok.SneakyThrows;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.ToStringMethod;
import net.bytepuppy.Consts;
import net.bytepuppy.Runner;

import java.lang.reflect.Modifier;


/**
 * 创建一个POJO类
 *
 * 调用此POJO类的toString方法
 *
 * @author liuh
 * @date 2021/3/25
 */
@Runner
public class PojoCreateRunner implements Runnable {

    static String newClassName = "net.bytepuppy.subclass.ReallxVo";
    static String field1 = "name";
    static String field2 = "age";
    static String field3 = "male";

    @SneakyThrows
    @Override
    public void run() {
        // DynamicType.Unloaded，顾名思义，创建了字节码，但未加载到虚拟机
        DynamicType.Unloaded<?> dynamicType = new ByteBuddy()
                // 继承Object.class
                .subclass(Object.class)
                // 指定固定的名字
                .name(newClassName)

                // 定义字段：name
                .defineField(field1, String.class, Modifier.PRIVATE)
                // 定义setter
                .defineMethod("setName", Void.TYPE, Modifier.PUBLIC)
                // 定义setter入参
                .withParameters(String.class)
                // 定义setter实现，用FieldAccessor
                .intercept(FieldAccessor.ofField(field1))
                // 定义getter
                .defineMethod("getName", String.class, Modifier.PUBLIC)
                .intercept(FieldAccessor.ofField(field1))

                // 定义字段：age
                .defineField(field2, int.class, Modifier.PRIVATE)
                .defineMethod("setAge", Void.TYPE, Modifier.PUBLIC)
                .withParameters(int.class)
                .intercept(FieldAccessor.ofField(field2))
                .defineMethod("getAge", int.class, Modifier.PUBLIC)
                .intercept(FieldAccessor.ofField(field2))

                // 定义字段：male
                .defineField(field3, boolean.class, Modifier.PRIVATE)
                .defineMethod("setMale", Void.TYPE, Modifier.PUBLIC)
                .withParameters(boolean.class)
                .intercept(FieldAccessor.ofField(field3))
                .defineMethod("getMale", boolean.class, Modifier.PUBLIC)
                .intercept(FieldAccessor.ofField(field3))

                // 定义toString方法
                .defineMethod("toString", String.class, Modifier.PUBLIC)
                .intercept(ToStringMethod.prefixedBySimpleClassName())
                // 创建字节码
                .make();


        // 将字节码保存到指定文件
        dynamicType.saveIn(Consts.newFile(Consts.CLASS_OUTPUT_BASE_DIR));
        System.out.println("save class: " + Consts.CLASS_OUTPUT_BASE_DIR + newClassName);

        // 实例化POJO类，并答应toString方法结果
        Class clazz = dynamicType.load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
        Object reallxVo = clazz.newInstance();
        clazz.getMethod("setName", String.class).invoke(reallxVo, "reallx");
        clazz.getMethod("setAge", int.class).invoke(reallxVo, 18);
        clazz.getMethod("setMale", boolean.class).invoke(reallxVo, true);

        Object res = clazz.getMethod("toString").invoke(reallxVo);
        System.out.println("toString result: " + res);
    }
}
