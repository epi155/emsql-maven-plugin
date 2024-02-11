package io.github.epi155.emsql.plugin.sql;

import io.github.epi155.emsql.plugin.Tools;
import lombok.Getter;
import lombok.val;

import java.util.*;
import java.util.stream.Collectors;

import static io.github.epi155.emsql.plugin.Tools.cc;
import static io.github.epi155.emsql.plugin.Tools.mc;
import static io.github.epi155.emsql.plugin.sql.SqlAction.IMAX;

@Getter
public class JdbcStatement implements JdbcMap {
    private final String text;
    private final Map<Integer, SqlParam> iMap;
    private final Map<Integer, SqlParam> oMap;
    private final Map<String, SqlKind> nMap;
    private final List<String> tKeys;

    public JdbcStatement(String text, Map<Integer, SqlParam> iMap, Map<Integer, SqlParam> oMap) {
        this.text = text;
        this.oMap = oMap;
        this.nMap = normalize(iMap);
        this.iMap = iMap;
        this.tKeys = filterNotScalar(nMap);
        mc.oSize(oMap.size());
        mc.iSize(iMap.size());
        mc.nSize(nMap.size());
    }

    private List<String> filterNotScalar(Map<String, SqlKind> nMap) {
        List<String> in = new ArrayList<>();
        int k=0;
        for(val e: nMap.entrySet()) {
            SqlKind kind = e.getValue();
            if (! kind.isScalar() && kind.columns()>1) {
                String name = e.getKey();
                kind.setId(++k);
                in.add(name);
            }
        }
        return in;
    }

    private static Map<String, SqlKind> normalize(Map<Integer, SqlParam> iMap) {
        Map<String, SqlKind> zroMap = new LinkedHashMap<>();
        iMap.forEach((k, p) -> zroMap.putIfAbsent(p.getName(), p.getType()));
        int size = zroMap.size();
        if (size==0 || size>IMAX)
            return zroMap;
        Map<String, SqlKind> oneMap = new LinkedHashMap<>();
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

    public void flush() {
        if (mc.isInputReflect()) return;
        tKeys.forEach(key -> cc.put(key, nMap.get(key)));
    }
}
