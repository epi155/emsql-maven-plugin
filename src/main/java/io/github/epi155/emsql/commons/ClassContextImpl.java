package io.github.epi155.emsql.commons;

import io.github.epi155.emsql.api.PluginContext;
import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import io.github.epi155.emsql.api.TypeModel;
import lombok.Getter;

import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

import static io.github.epi155.emsql.commons.Contexts.*;
import static io.github.epi155.emsql.commons.Tools.capitalize;
import static io.github.epi155.emsql.pojo.PojoAction.plainGenericsNew;
import static io.github.epi155.emsql.pojo.PojoAction.throughGetter;

public class ClassContextImpl implements ClassContext {
    public static final String RUNTIME_EMSQL = "io.github.epi155.emsql.runtime.EmSQL";
    public static final String RUNTIME_J8TIME = "io.github.epi155.emsql.runtime.J8Time";
    public static final String RUNTIME_TRACE = "io.github.epi155.emsql.runtime.SqlTrace";
    public static final String RUNTIME_SUPPLIER = "io.github.epi155.emsql.runtime.ESupplier";
    public static final String RUNTIME_CONSUMER = "io.github.epi155.emsql.runtime.EConsumer";
    public static final String RUNTIME_OPTIONAL = "io.github.epi155.emsql.runtime.EOptional";
    @Getter
    private final boolean debug;
    private final SortedSet<String> importSet = new TreeSet<>();
    @Getter
    private final Map<String, SqlDataType> fields;
    private final boolean java7;
    private final Map<String, TypeModel> inFields = new LinkedHashMap<>();
    private final PluginContext pc;

    public ClassContextImpl(PluginContext pc, Map<String, TypeModel> declare) {
        this.pc = pc;
        this.debug = pc.isDebug();
        this.java7 = pc.isJava7();
        this.fields = declare.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> (SqlDataType)e.getValue()));
        importSet.add("java.sql.*");
        if (debug) {
            importSet.add(RUNTIME_TRACE);
        }
    }
    public String supplier() {
        if (java7) {
            importSet.add(RUNTIME_SUPPLIER);
            return "ESupplier";
        } else{
            importSet.add("java.util.function.Supplier");
            return "Supplier";
        }
    }
    public String consumer() {
        if (java7) {
            importSet.add(RUNTIME_CONSUMER);
            return "EConsumer";
        } else {
            importSet.add("java.util.function.Consumer");
            return "Consumer";
        }
    }
    public String optional() {
        if (java7) {
            importSet.add(RUNTIME_OPTIONAL);
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

    public void traceParameterEnds(PrintModel ipw) {
        if (java7) {
            ipw.ends();
        }
        ipw.less();
    }

    public void traceParameterBegin(PrintModel ipw) {
        if (java7) {
            importSet.add(RUNTIME_SUPPLIER);
            ipw.putf("new ESupplier<Object[]>() {%n");
            ipw.more();
            ipw.printf("@Override%n");
            ipw.printf("public Object[] get() {%n");
        } else {
            ipw.putf("() -> {%n");
        }
    }

    public void delegateResponseFields(PrintModel ipw, Collection<SqlParam> sp) {
        if (java7) {
            importSet.add(RUNTIME_EMSQL);
            sp.forEach(p -> ipw.printf("result.%s = %1$s==null ? EmSQL.<%s>getDummyConsumer() : %1$s;%n", p.getName(), p.getType().getWrapper()));
        } else {
            sp.forEach(p -> ipw.printf("result.%s = %1$s==null ? it -> {} : %1$s;%n", p.getName()));
        }
    }

    public void delegateRequestFields(PrintModel ipw, Map<String, SqlDataType> sp) {
        if (java7) {
            importSet.add(RUNTIME_EMSQL);
            sp.forEach((name, type) -> ipw.printf("result.%s = %1$s==null ? EmSQL.<%s>getDummySupplier() : %1$s;%n", name, type.getWrapper()));
        } else {
            sp.forEach((name, type) -> ipw.printf("result.%s = %1$s==null ? () -> null : %1$s;%n", name));
        }
    }

    public void anonymousGenerics(PrintModel ipw, JdbcStatement jdbc) {
        if (java7) {
            plainGenericsNew(ipw, jdbc);
        } else {
            int nSize = mc.nSize();
            if (nSize == 0) {
                ipw.putf("<Void>"); // why not "<>" ?
            } else if (nSize == 1) {
                ipw.putf("<>");
            } else if (nSize <= IMAX) {
                ipw.putf("%d<>", nSize);
            } else {
                ipw.putf("<>");
            }
        }
    }

    public void declareTuner(PrintModel ipw) {
        ipw.commaLn();
        importSet.add("io.github.epi155.emsql.runtime.SqlStmtSetter");
        importSet.add("io.github.epi155.emsql.runtime.SqlStmtSetImpl");
        if (java7) {
            importSet.add("io.github.epi155.emsql.runtime.EConsumer");
            ipw.printf("        final EConsumer<SqlStmtSetter> u");
        } else {
            importSet.add("java.util.function.Consumer");
            ipw.printf("        final Consumer<SqlStmtSetter> u");
        }

    }

    public void put(String key, TypeModel kind) {
        inFields.putIfAbsent(key, kind);
    }

    public void flush(PrintModel ipw) {
        inFields.forEach((name, kind) -> {
            if (kind.columns()>1) {
                writeInFieldInterface(ipw, capitalize(name), kind.toMap());
            }
        });
    }
    private void writeInFieldInterface(PrintModel ipw, String hiName, Map<String, SqlDataType> map) {
        ipw.printf("public interface %s"+REQUEST+" {%n", hiName);
        Map<String, Map<String, SqlDataType>> next = throughGetter(ipw, map);
        next.forEach((n,np) -> writeInFieldInterface(ipw, capitalize(n), np));
        ipw.ends();
    }

    public void incMethods() {
        pc.incMethods();
    }
}
