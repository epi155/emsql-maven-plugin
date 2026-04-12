package io.github.epi155.emsql.commons;

import io.github.epi155.emsql.api.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

import static io.github.epi155.emsql.commons.Contexts.REQUEST;
import static io.github.epi155.emsql.commons.Contexts.RESPONSE;
import static io.github.epi155.emsql.commons.SqlAction.docInterfacePS;
import static io.github.epi155.emsql.commons.Tools.capitalize;
import static io.github.epi155.emsql.pojo.PojoAction.throughGetter;

@Slf4j
public abstract class ClassContextImpl implements ClassContext {
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
    private final Map<String, String> dtoMap = new HashMap<>();
    private final Map<String,InterfaceConstruct> iOuMap = new HashMap<>();

    protected ClassContextImpl(PluginContext pc, Map<String, TypeModel> declare) {
        this.pc = pc;
        this.debug = pc.isDebug();
        this.java7 = pc.isJava7();
        this.fields = declare.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> (SqlDataType) e.getValue()));
        importSet.add("java.sql.*");
        if (debug) {
            importSet.add(RUNTIME_TRACE);
        }
    }

    public String supplier() {
        if (java7) {
            importSet.add(RUNTIME_SUPPLIER);
            return "ESupplier";
        } else {
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
            ipw.putf("new ESupplier<SqlArg[]>() {%n");
            ipw.more();
            ipw.printf("@Override%n");
            ipw.printf("public SqlArg[] get() {%n");
        } else {
            ipw.putf("() -> {%n");
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

    public void declareTuner(PrintModel ipw) {
        ipw.commaLn();
        importSet.add("io.github.epi155.emsql.runtime.SqlStmtSetter");
        importSet.add("io.github.epi155.emsql.runtime.SqlStmtSetImpl");
        if (java7) {
            importSet.add(RUNTIME_CONSUMER);
            ipw.printf("        final EConsumer<SqlStmtSetter> u");
        } else {
            importSet.add("java.util.function.Consumer");
            ipw.printf("        final Consumer<SqlStmtSetter> u");
        }
    }

    public void validate(String query, Class<? extends SqlAction> clazz, Map<Integer, SqlParam> parameters) {
        pc.validate(query, clazz, parameters);
    }

    public void put(String key, TypeModel kind) {
        inFields.putIfAbsent(key, kind);
    }

    public void flush(PrintModel ipw) {
        inFields.forEach((name, kind) -> {
            if (kind.columns() > 1) {
                writeInFieldInterface(ipw, capitalize(name), kind.toMap());
            }
        });
    }

    private void writeInFieldInterface(PrintModel ipw, String hiName, Map<String, SqlDataType> map) {
        docInterfacePS(ipw, hiName, map);
        ipw.printf("public interface %s" + REQUEST + " {%n", hiName);
        Map<String, Map<String, SqlDataType>> next = throughGetter(ipw, map);
        next.forEach((n, np) -> writeInFieldInterface(ipw, capitalize(n), np));
        ipw.ends();
    }

    public void incMethods() {
        pc.incMethods();
    }

    /**
     *
     * @param name       method name
     * @param values     output fields
     * @param isReflect     output fields by reflect (no interface)
     * @param isDelegate    output fields delegate (setter reference)
     * @return output interface name
     */
    public String outPrepare(String name, Collection<SqlParam> values, boolean isReflect, boolean isDelegate) {
        if (isReflect) {
            // do nothing
            return null;
        }
        InterfaceConstruct ic = new InterfaceConstruct(values);
        String prefix = isDelegate ? "S::" : "I::";

        String cName = capitalize(name)+RESPONSE;
        String arguments = prefix + ic.signature();

        // 1. does it already exist?
        String ifName = dtoMap.get(arguments);
        if (ifName==null) {
            // it's new
            dtoMap.put(arguments, cName);
            iOuMap.put(cName, ic);
            return cName;
        } else {
            log.info("Interface with the same existing signature, use {} instead of {}\n", ifName, cName);
            return ifName;
        }
    }
    public void writeResponseInterface(PrintModel ipw) throws InvalidQueryException {
        for(Map.Entry<String, String> ea: dtoMap.entrySet()) {
            String args = ea.getKey();
            String iName = ea.getValue();
            InterfaceConstruct param = iOuMap.get(iName);

            boolean isDelegate = args.startsWith("S::");
            if (isDelegate) {
                param.writeDelegate(ipw, iName, java7);
            } else {
                param.writeStandard(ipw, iName);
            }

        }
    }

}
