package io.github.epi155.emsql.pojo;

import io.github.epi155.emsql.api.PluginContext;
import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.TypeModel;
import io.github.epi155.emsql.commons.ClassContextImpl;

import java.util.Map;

public class PojoClassContext extends ClassContextImpl {
    public PojoClassContext(PluginContext pc, Map<String, TypeModel> declare) {
        super(pc, declare);
    }

    @Override
    public void newLine(PrintModel ipw, boolean tune) {
//        ipw.putln();
    }
}
