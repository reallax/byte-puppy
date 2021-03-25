package net.bytepuppy.subclass;

import lombok.SneakyThrows;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytepuppy.Consts;
import net.bytepuppy.Runner;

/**
 * 创建一个空类
 *
 * @author liuh
 * @date 2021/3/25
 */
@Runner
public class SimpleCreateRunner implements Runnable {

    static String newClassName = "net.bytepuppy.subclass.HelloWorld";
    @SneakyThrows
    @Override
    public void run() {
        // DynamicType.Unloaded，顾名思义，创建了字节码，但未加载到虚拟机
        DynamicType.Unloaded<?> dynamicType = new ByteBuddy()
                // 继承Object.class
                .subclass(Object.class)
                // 指定固定的名字
                .name(newClassName)
                // 创建字节码
                .make();

        // 将字节码保存到指定文件
        dynamicType.saveIn(Consts.newFile(Consts.CLASS_OUTPUT_BASE_DIR));

        System.out.println("save class: " + Consts.CLASS_OUTPUT_BASE_DIR + newClassName);
    }
}
