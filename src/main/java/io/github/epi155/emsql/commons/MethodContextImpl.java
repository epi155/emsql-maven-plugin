package io.github.epi155.emsql.commons;

import io.github.epi155.emsql.api.ForceAware;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
public class MethodContextImpl implements MethodContext {
    private final String name;
    private final boolean inputReflect;
    private final boolean inputDelegate;
    private final boolean inputForce;
    private boolean outputReflect;
    private boolean outputDelegate;
    @Accessors(fluent = true)
    private Integer oSize;
    @Setter
    @Accessors(fluent = true)
    private Integer iSize;
    @Setter
    @Accessors(fluent = true)
    private Integer nSize;
    private final Map<String, String> inputCache = new HashMap<>();
    private final Map<String, String> outputCache = new HashMap<>();

    public MethodContextImpl(SqlMethod sqlMethod) {
        this.name = sqlMethod.getMethodName();
        val perform = sqlMethod.getPerform();
        val input = perform.getInput();
        val output = perform.getOutput();
        this.inputReflect = input != null && input.isReflect();
        this.inputDelegate = input != null && input.isDelegate();
        this.outputReflect = output != null && output.isReflect();
        this.outputDelegate = output != null && output.isDelegate();
        this.inputForce = perform instanceof ForceAware && ((ForceAware) perform).isForce();
    }

    public MethodContextImpl oSize(Integer size) {
        if (oSize == null && size != null) {
            oSize = size;
            if (size < 2) {
                outputDelegate = false;
                outputReflect = false;
            }
        }
        return this;
    }

    @Override
    public Optional<String> iFind(String name) {
        return Optional.ofNullable(inputCache.get(name));
    }

    @Override
    public void iRegister(String name, String result) {
        inputCache.put(name, result);
    }

    @Override
    public Optional<String> oFind(String name) {
        return Optional.ofNullable(outputCache.get(name));
    }

    @Override
    public void oRegister(String name, String result) {
        outputCache.put(name, result);
    }
}
