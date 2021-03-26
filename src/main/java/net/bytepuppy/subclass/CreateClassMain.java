package net.bytepuppy.subclass;


import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import net.bytepuppy.Runner;


/**
 * @author liuh
 * @date 2021/3/25
 */
public class CreateClassMain {

    static String scanPath = "net.bytepuppy." + "subclass";

    public static void main(String[] args) throws Exception {

        ClassPath classpath = ClassPath.from(CreateClassMain.class.getClassLoader());
        ImmutableSet<ClassPath.ClassInfo> classes = classpath.getTopLevelClassesRecursive(scanPath);
        for (ClassPath.ClassInfo classInfo : classes) {
            Class<?> aClass = classInfo.load();

            if (!aClass.isAnnotationPresent(Runner.class)) {
                continue;
            }
            if (!aClass.getSuperclass().isInstance(Runnable.class)) {
                continue;
            }

            System.out.println(" ## Runner run: " + aClass.getName());
            Runnable run = (Runnable) aClass.newInstance();
            run.run();
        }

    }
}
