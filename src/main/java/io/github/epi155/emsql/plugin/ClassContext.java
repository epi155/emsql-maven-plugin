package io.github.epi155.emsql.plugin;

import io.github.epi155.emsql.plugin.sql.SqlEnum;
import lombok.Getter;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

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
            importSet.add("io.github.epi155.emsql.runtime.ESqlTrace");
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
            ipw.putf("new ESupplier<Object[]>() {%n");
            ipw.more();
            ipw.printf("@Override%n");
            ipw.printf("public Object[] get() {%n");
        } else {
            ipw.putf("() -> {%n");
        }
    }
}
