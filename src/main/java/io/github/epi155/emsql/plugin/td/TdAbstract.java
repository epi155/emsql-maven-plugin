package io.github.epi155.emsql.plugin.td;

import io.github.epi155.emsql.api.CodeFactory;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.nodes.Node;

/**
 * Abstract base class for Template Definition classes to reduce code duplication.
 */
public abstract class TdAbstract<T> extends TypeDescription {
    protected final CodeFactory factory;

    protected TdAbstract(Class<T> modelClass, CodeFactory factory) {
        super(modelClass);
        this.factory = factory;
        substituteAdditionalProperties();
    }
    protected TdAbstract(Class<T> modelClass, String yamlTag, CodeFactory factory) {
        super(modelClass, yamlTag);
        this.factory = factory;
        substituteProperty("exec-sql", String.class, null, "setExecSql");
        substituteAdditionalProperties();
    }

    @Override
    public Object newInstance(Node node) {
        return createModelInstance();
    }

    protected abstract T createModelInstance();

    /**
     * Override to substitute additional properties beyond the standard exec-sql.
     */
    protected void substituteAdditionalProperties() {
        // Default implementation does nothing
    }
}