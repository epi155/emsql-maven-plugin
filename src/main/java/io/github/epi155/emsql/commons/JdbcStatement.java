package io.github.epi155.emsql.commons;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import lombok.Getter;
import lombok.val;
import org.apache.commons.text.StringEscapeUtils;

import java.util.*;
import java.util.stream.Collectors;

import static io.github.epi155.emsql.commons.Contexts.*;

@Getter
public class JdbcStatement implements JdbcMap {
    private final String text;
    private final Map<Integer, SqlParam> iMap;
    private final Map<Integer, SqlParam> oMap;
    private final Map<String, SqlDataType> nMap;
    private final List<String> tKeys;

    public JdbcStatement(String text, Map<Integer, SqlParam> iMap, Map<Integer, SqlParam> oMap) {
        this.text = text;
        this.oMap = oMap;
        this.nMap = normalize(iMap);
        this.iMap = iMap;
        this.tKeys = filterNotScalar(nMap);
        mc.oSize(oMap.size()).iSize(iMap.size()).nSize(nMap.size());
    }

    private List<String> filterNotScalar(Map<String, SqlDataType> nMap) {
        List<String> in = new ArrayList<>();
        int k=0;
        for(val e: nMap.entrySet()) {
            SqlDataType kind = e.getValue();
            if (! kind.isScalar() && kind.columns()>1) {
                String name = e.getKey();
                kind.setId(++k);
                in.add(name);
            }
        }
        return in;
    }

    private static Map<String, SqlDataType> normalize(Map<Integer, SqlParam> iMap) {
        Map<String, SqlDataType> zroMap = new LinkedHashMap<>();
        iMap.forEach((k, p) -> zroMap.putIfAbsent(p.getName(), p.getType()));
        int size = zroMap.size();
        if (size==0 || size>IMAX)
            return zroMap;
        Map<String, SqlDataType> oneMap = new LinkedHashMap<>();
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

    public void writeQuery(String kPrg, PrintModel ipw) {
        ipw.printf("private static final String Q_%s = \"%s\";%n", kPrg, StringEscapeUtils.escapeJava(text));
    }
}
