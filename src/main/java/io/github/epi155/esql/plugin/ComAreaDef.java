package io.github.epi155.esql.plugin;

import io.github.epi155.esql.plugin.sql.SqlEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

@Setter
@Getter
public class ComAreaDef extends ComAreaStd {
    private Map<String, SqlEnum> fields = new LinkedHashMap<>();
}
