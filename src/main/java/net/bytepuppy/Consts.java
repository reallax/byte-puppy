package net.bytepuppy;


import java.io.File;
import java.io.IOException;

/**
 * @author liuh
 * @email hong.liu@dmall.com
 * @date 2021/3/25
 */
public class Consts {

    public static final String CLASS_OUTPUT_BASE_DIR = Consts.class.getResource("/")
            .getPath().split("classes")[0] + "generate";

    public static File newFile(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            System.out.println("try create output path: " + path);
            file.getParentFile().mkdirs();
            file.createNewFile();
        }

        return file;
    }
}
