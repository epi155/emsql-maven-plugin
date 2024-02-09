package io.github.epi155.emsql.plugin.xtype;

import io.github.epi155.emsql.plugin.ClassContext;
import io.github.epi155.emsql.plugin.IndentPrintWriter;
import io.github.epi155.emsql.plugin.sql.SqlKind;
import lombok.val;

public class SqlVector implements SqlKind {

    private final SqlKind[] kinds;

    public SqlVector(SqlKind...sqlKinds) {
        this.kinds = sqlKinds;
    }

    @Override
    public void psSet(IndentPrintWriter ipw, String source, ClassContext cc) {
        cc.add("java.util.List");
        ipw.printf("for (String ei: %s) {%n", source);
        ipw.more();
        for(val kind: kinds) {
            kind.psSet(ipw, "ei", cc);
        }
        ipw.ends();
    }

    @Override
    public void psPush(IndentPrintWriter ipw, String name, ClassContext cc) {
        ipw.printf("for (String ei: EmSQL.get(i, \"%s\", List<String>.class)) {%n", name);
        ipw.more();
        for(val kind: kinds) {
            kind.psSet(ipw, "ei", cc);
        }
        ipw.ends();

    }

    @Override
    public String getPrimitive() {
        if (kinds.length == 1) {
            return "List<" + kinds[0].getPrimitive() + ">";
        }
        throw new IllegalStateException();
    }
    @Override
    public boolean isScalar() { return false; }

    @Override
    public int columns() { return kinds.length; }
}
