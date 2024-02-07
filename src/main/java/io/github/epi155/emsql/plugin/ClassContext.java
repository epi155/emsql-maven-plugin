package io.github.epi155.emsql.plugin;

import io.github.epi155.emsql.plugin.sql.JdbcStatement;
import io.github.epi155.emsql.plugin.sql.SqlEnum;
import io.github.epi155.emsql.plugin.sql.SqlParam;
import lombok.Getter;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static io.github.epi155.emsql.plugin.sql.SqlAction.IMAX;

public class ClassContext {
    @Getter
    private final boolean debug;
    private final SortedSet<String> importSet = new TreeSet<>();
    @Getter
    private final Map<String, SqlEnum> fields;
    private final boolean java7;

    public ClassContext(MojoContext cx, Map<String, SqlEnum> declare) {
        this.debug = cx.debug;
        this.java7 = cx.java7;
        this.fields = declare;
        importSet.add("java.sql.*");
        if (debug) {
            importSet.add("io.github.epi155.emsql.runtime.SqlTrace");
        }
    }
    public String supplier() {
        if (java7) {
            importSet.add("io.github.epi155.emsql.runtime.ESupplier");
            return "ESupplier";
        } else{
            importSet.add("java.util.function.Supplier");
            return "Supplier";
        }
    }
    public String consumer() {
        if (java7) {
            importSet.add("io.github.epi155.emsql.runtime.EConsumer");
            return "EConsumer";
        } else {
            importSet.add("java.util.function.Consumer");
            return "Consumer";
        }
    }
    public String optional() {
        if (java7) {
            importSet.add("io.github.epi155.emsql.runtime.EOptional");
            return "EOptional";
        } else {
            importSet.add("java.util.Optional");
            return "Optional";
        }
    }
    public void writeImport(PrintWriter pw) {
        importSet.forEach(it -> pw.printf("import %s;%n", it));
    }

    public void add(String s) {
        importSet.add(s);
    }

    public void addAll(Collection<String> requires) {
        importSet.addAll(requires);
    }

    public void traceParameterEnds(IndentPrintWriter ipw) {
        if (java7) {
            ipw.ends();
        }
        ipw.less();
    }

    public void traceParameterBegin(IndentPrintWriter ipw) {
        if (java7) {
            importSet.add("io.github.epi155.emsql.runtime.ESupplier");
            ipw.putf("new ESupplier<Object[]>() {%n");
            ipw.more();
            ipw.printf("@Override%n");
            ipw.printf("public Object[] get() {%n");
        } else {
            ipw.putf("() -> {%n");
        }
    }

    public void delegateResponseFields(IndentPrintWriter ipw, Collection<SqlParam> sp) {
        if (java7) {
            importSet.add("io.github.epi155.emsql.runtime.EmSQL");
            sp.forEach(p -> ipw.printf("result.%s = %1$s==null ? EmSQL.<%s>getDummyConsumer() : %1$s;%n", p.getName(), p.getType().getWrapper()));
        } else {
            sp.forEach(p -> ipw.printf("result.%s = %1$s==null ? it -> {} : %1$s;%n", p.getName()));
        }
    }

    public void delegateRequestFields(IndentPrintWriter ipw, Map<String, SqlEnum> sp) {
        if (java7) {
            importSet.add("io.github.epi155.emsql.runtime.EmSQL");
            sp.forEach((name, type) -> ipw.printf("result.%s = %1$s==null ? EmSQL.<%s>getDummySupplier() : %1$s;%n", name, type.getWrapper()));
        } else {
            sp.forEach((name, type) -> ipw.printf("result.%s = %1$s==null ? () -> null : %1$s;%n", name));
        }
    }

    public void anonymousGenerics(IndentPrintWriter ipw, JdbcStatement jdbc, ComAttribute input) {
        int nSize = jdbc.getNameSize();
        if (java7) {
            if (nSize == 0) {
                ipw.putf("<Void>");
            } else if (nSize == 1) {
                jdbc.getNMap().forEach((name, type) -> ipw.putf("<%s>", type.getWrapper()));    // once
            } else if (nSize <= IMAX) {
                ipw.putf("%d<%s>", nSize, jdbc.getNMap().values().stream().map(SqlEnum::getWrapper).collect(Collectors.joining(", ")));
            } else {
                if (input != null && input.isDelegate()) {
                    ipw.putf("<DI>");
                } else {
                    ipw.putf("<I>");
                }
            }
        } else {
            if (nSize == 0) {
                ipw.putf("<Void>");
            } else if (nSize == 1) {
                ipw.putf("<>");
            } else if (nSize <= IMAX) {
                ipw.putf("%d<>", nSize);
            } else {
                ipw.putf("<>");
            }
        }
    }
}
