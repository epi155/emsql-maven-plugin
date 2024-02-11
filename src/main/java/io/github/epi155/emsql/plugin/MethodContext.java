package io.github.epi155.emsql.plugin;

import io.github.epi155.emsql.plugin.sql.SqlMethod;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;

@Getter
public class MethodContext {
    private final String name;
    private final boolean inputReflect;
    private final boolean inputDelegate;
    private final boolean outoutReflect;
    private final boolean outoutDelegate;
    @Setter @Accessors(fluent = true)
    private Integer oSize;
    @Setter @Accessors(fluent = true)
    private Integer iSize;
    @Setter @Accessors(fluent = true)
    private Integer nSize;

    public MethodContext(SqlMethod sqlMethod) {
        this.name = sqlMethod.getMethodName();
        val perform = sqlMethod.getPerform();
        val input = perform.getInput();
        val outout = perform.getOutput();
        this.inputReflect = input!=null && input.isReflect();
        this.inputDelegate = input!=null && input.isDelegate();
        this.outoutReflect = outout!=null && outout.isReflect();
        this.outoutDelegate = outout!=null && outout.isDelegate();
    }
}
