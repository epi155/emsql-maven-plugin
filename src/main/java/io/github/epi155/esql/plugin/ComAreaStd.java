package io.github.epi155.esql.plugin;

import io.github.epi155.esql.plugin.sql.SqlEnum;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class ComAreaStd implements ComAttribute, ComFields {
    /** DO NOT USE, reduces performance by 14% */ private boolean reflect;
    /** ?? */ private boolean delegate;
    private Map<String, SqlEnum> fields = new LinkedHashMap<>();
}
