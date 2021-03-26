package net.bytepuppy.redefine.delegate;

import java.util.List;

/**
 * 被增强类
 *
 * @author liuh
 * @date 2021/3/25
 */
public class DemoService {

    public String report(String name, int value) {
        return String.format("name: %s, value: %s", name, value);
    }

    public void compute(List<Integer> values) {
        System.out.println("compute result:" + values.stream().mapToInt(v -> v.intValue()).sum());
    }
}
