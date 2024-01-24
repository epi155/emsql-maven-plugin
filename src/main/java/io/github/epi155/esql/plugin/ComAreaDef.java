package io.github.epi155.esql.plugin;

import io.github.epi155.esql.plugin.sql.SqlEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.LinkedHashMap;
import java.util.Map;

@Data @EqualsAndHashCode(callSuper = true)
public class ComAreaDef extends ComAreaStd {
    private Map<String, SqlEnum> fields = new LinkedHashMap<>();
}
