package net.bytepuppy.redefine.advice;

import java.util.List;

/**
 * @author liuh
 * @email hong.liu@dmall.com
 * @date 2021/3/26
 */
public class ComputeService {

    public String compute(String name, List<Integer> values) {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) { }

        return String.format("compute name: %s, compute result: %s",
                name, values.stream().mapToInt(v -> v.intValue()).sum());
    }
}
