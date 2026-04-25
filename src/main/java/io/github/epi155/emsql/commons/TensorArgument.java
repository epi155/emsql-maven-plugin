package io.github.epi155.emsql.commons;

import java.util.ArrayList;
import java.util.List;

public class TensorArgument {
    private final List<String> staticArguments;
    private final List<String> dynamicArguments;

    public TensorArgument(List<String> staticArguments, List<String> dynamicArguments) {
        this.staticArguments = staticArguments;
        this.dynamicArguments = dynamicArguments;
    }

    public boolean haveStatic() {
        return !staticArguments.isEmpty();
    }

    public List<String> getAll() {
        ArrayList<String> r = new ArrayList<>(staticArguments);
        r.addAll(dynamicArguments);
        return r;
    }

    public boolean isPresent() {
        return !staticArguments.isEmpty() || !dynamicArguments.isEmpty();
    }

    public int staticSize() {
        return staticArguments.size();
    }
}
