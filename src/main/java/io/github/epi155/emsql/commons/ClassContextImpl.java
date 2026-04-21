package io.github.epi155.emsql.commons;

import io.github.epi155.emsql.api.PluginContext;
import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import io.github.epi155.emsql.api.TypeModel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

import static io.github.epi155.emsql.commons.Contexts.*;
import static io.github.epi155.emsql.commons.Tools.capitalize;

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
    private final PluginContext pc;
    private final Map<String, String> dtoMap = new HashMap<>();
    private final Map<String, InterfaceWriter> ifMap = new HashMap<>();

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

    public void incMethods() {
        pc.incMethods();
    }

    /**
     *
     * @param name       method name
     * @param values     output fields
     * @param mask       output fields mask (reflect/delegate)
     * @return output interface name
     */
    public String outPrepare(String name, Collection<SqlParam> values, OutputMask mask) {
        Optional<String> oResult = mc.oFind(name);
        if (oResult.isPresent())
            return oResult.get();
        if (values.size() <= 1) {
            return null;
        }
        InterfaceWriter ic = new InterfaceRS(name, values);
        String result = deduplicate(capitalize(name)+RESPONSE, ic);
        mc.oRegister(name, result);
        return result;
    }

    public String deduplicate(String name, InterfaceWriter ic) {
        String arguments = ic.signature();
        String ifName = dtoMap.get(arguments);
        if (ifName==null) {
            // it's new
            dtoMap.put(arguments, name);
            ifMap.put(name, ic);
            return name;
        } else {
            if (ifName.equals(name)) {
                log.info("{} interface already registered", ifName);
            } else {
                log.info("Interface with the same existing signature, use {} instead of {}", ifName, name);
            }
            return ifName;
        }
    }

    public String inPrepare(String name, Collection<SqlParam> values, InputMask mask) {
        Optional<String> oResult = mc.iFind(name);
        if (oResult.isPresent())
            return oResult.get();
        if ((values.size() <= IMAX && !mask.isInputForce()) || values.size() <= 1  ) {
            InterfacePS.registerTensor(name, values);
            return null;
        }
        InterfaceWriter ic = new InterfacePS(name, values);
        String result = deduplicate(capitalize(name)+REQUEST, ic);
        mc.iRegister(name, result);
        return result;
    }

    public void writeInterfaces(PrintModel ipw) {
        for(Map.Entry<String, InterfaceWriter> np: ifMap.entrySet()) {
            String iName = np.getKey();
            InterfaceWriter param = np.getValue();

            param.writeStandard(ipw, iName);
            pc.incInterfaces();
        }
    }

}
