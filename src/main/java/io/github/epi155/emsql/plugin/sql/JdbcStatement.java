package io.github.epi155.emsql.plugin.sql;

import io.github.epi155.emsql.plugin.Tools;
import lombok.Getter;
import lombok.val;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static io.github.epi155.emsql.plugin.sql.SqlAction.IMAX;

@Getter
public class JdbcStatement implements JdbcMap {
    private final String text;
    private final Map<Integer, SqlParam> iMap;
    private final Map<Integer, SqlParam> oMap;
    private final Map<String, SqlEnum> nMap;

    public JdbcStatement(String text, Map<Integer, SqlParam> iMap, Map<Integer, SqlParam> oMap) {
        this.text = text;
        this.oMap = oMap;
        this.nMap = normalize(iMap);
//        this.nMap = normalize(iMap.values().stream().collect(Collectors.toMap(SqlParam::getName, SqlParam::getType, (a, b) -> a)));
        this.iMap = iMap;
    }
    public int getOutSize() {
        return oMap.size();
    }

    public int getInpSize() {
        return iMap.size();
    }
    public int getNameSize() {
        return nMap.size();
    }

    private static Map<String, SqlEnum> normalize(Map<Integer, SqlParam> iMap) {
        Map<String, SqlEnum> zroMap = new LinkedHashMap<>();
        iMap.forEach((k, p) -> zroMap.putIfAbsent(p.getName(), p.getType()));
        int size = zroMap.size();
        if (size==0 || size>IMAX)
            return zroMap;
        Map<String, SqlEnum> oneMap = new LinkedHashMap<>();
        zroMap.forEach((name,type) -> {
            String nName = Tools.normalizeName(name);
            if (! nName.equals(name)) {
                String bName = nName;
                Set<String> names = iMap.values().stream().map(SqlParam::getName).collect(Collectors.toSet());
                for (int k=2; names.contains(nName); k++) {
                    nName = String.format("%s%d", bName, k);
                }
                for(val ee: iMap.entrySet()) {
                    if (ee.getValue().getName().equals(name)) {
                        iMap.replace(ee.getKey(), new SqlParam(nName, ee.getValue().getType()));
                    }
                }
            }
            oneMap.put(nName, type);
        });
        if (oneMap.size() == size)
            return oneMap;
        throw new IllegalArgumentException("Input argument collision: "+String.join(",", zroMap.keySet()));
    }
}
