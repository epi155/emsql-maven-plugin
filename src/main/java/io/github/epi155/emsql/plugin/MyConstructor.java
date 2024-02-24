package io.github.epi155.emsql.plugin;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.MappingNode;

import java.util.Map;

public class MyConstructor extends Constructor {
    private final ThreadLocal<MapContext> mapContext;

    public MyConstructor(ThreadLocal<MapContext> mapContext) {
        super(DaoClassConfig.class, new LoaderOptions());
        this.mapContext = mapContext;
    }

    protected void constructMapping2ndStep(MappingNode node, Map<Object, Object> mapping) {
        mapContext.set(new MapContext(mapping));
        super.constructMapping2ndStep(node, mapping);
    }
}
