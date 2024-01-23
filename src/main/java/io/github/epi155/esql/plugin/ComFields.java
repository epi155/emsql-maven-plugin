package io.github.epi155.esql.plugin;

import io.github.epi155.esql.plugin.sql.SqlEnum;

import java.util.Map;

public interface ComFields {
    Map<String, SqlEnum> getFields();
}
