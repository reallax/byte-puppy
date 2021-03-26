package net.bytepuppy.redefine.delegate;

import lombok.Data;

/**
 * @author liuh
 * @date 2021/3/25
 */
@Data
public class ResultWrapper {
    private boolean isContinue;
    private Object result;
}
