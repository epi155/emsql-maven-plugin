package io.github.epi155.esql.plugin.sql;

import io.github.epi155.esql.plugin.JdbcMap;
import io.github.epi155.esql.plugin.SqlParam;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class JdbcStatement implements JdbcMap {
    private final String text;
    private final Map<Integer, SqlParam> iMap;
    private final Map<Integer, SqlParam> oMap;
}
