package io.github.epi155.emsql.spring;

import io.github.epi155.emsql.api.PluginContext;
import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.TypeModel;
import io.github.epi155.emsql.commons.ClassContextImpl;

import java.util.Map;

import static io.github.epi155.emsql.commons.Contexts.mc;

public class SpringClassContext extends ClassContextImpl {
    public SpringClassContext(PluginContext pc, Map<String, TypeModel> declare) {
        super(pc, declare);
    }

    @Override
    public void newLine(PrintModel ipw, boolean tune) {
        if (tune || mc.nSize() > 0)
            ipw.putln();
    }
}
