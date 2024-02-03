package io.github.epi155.emsql.plugin.sql;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
public class JdbcStatement implements JdbcMap {
    private final String text;
    private final Map<Integer, SqlParam> iMap;
    private final Map<Integer, SqlParam> oMap;

    public int getOutSize() {
        return oMap.size();
    }

    public int getInpSize() {
        return iMap.size();
    }
}
