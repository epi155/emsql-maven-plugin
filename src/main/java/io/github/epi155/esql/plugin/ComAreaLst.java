package io.github.epi155.esql.plugin;

import io.github.epi155.esql.plugin.sql.SqlEnum;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ComAreaLst implements ComAttribute {
    /** DO NOT USE, reduces performance by 14% */ private boolean reflect;
    private Map<String, SqlEnum> fields = new HashMap<>();
}
