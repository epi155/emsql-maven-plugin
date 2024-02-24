package io.github.epi155.emsql.commons;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;

@Getter
public class MethodContextImpl implements MethodContext {
    private final String name;
    private final boolean inputReflect;
    private final boolean inputDelegate;
    private boolean outputReflect;
    private boolean outputDelegate;
    @Accessors(fluent = true)
    private Integer oSize;
    @Setter @Accessors(fluent = true)
    private Integer iSize;
    @Setter @Accessors(fluent = true)
    private Integer nSize;

    public MethodContextImpl(SqlMethod sqlMethod) {
        this.name = sqlMethod.getMethodName();
        val perform = sqlMethod.getPerform();
        val input = perform.getInput();
        val outout = perform.getOutput();
        this.inputReflect = input!=null && input.isReflect();
        this.inputDelegate = input!=null && input.isDelegate();
        this.outputReflect = outout!=null && outout.isReflect();
        this.outputDelegate = outout!=null && outout.isDelegate();
    }

    public MethodContextImpl oSize(Integer size) {
        if (oSize == null && size != null) {
            oSize = size;
            if (size<2) {
                outputDelegate = false;
                outputReflect = false;
            }
        }
        return this;
    }
}
