package io.github.epi155.emsql.commons;

import io.github.epi155.emsql.api.InvalidQueryException;
import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

import static io.github.epi155.emsql.commons.ClassContextImpl.RUNTIME_EMSQL;
import static io.github.epi155.emsql.commons.Contexts.REQUEST;
import static io.github.epi155.emsql.commons.Contexts.cc;
import static io.github.epi155.emsql.commons.Tools.capitalize;

public class InterfacePS implements InterfaceWriter {
    @Getter
    private final boolean delegate;

    public static void registerTensor(String name, Collection<SqlParam> values) throws InvalidQueryException {
        for(SqlParam value: values) {
            String pName = value.getName();
            SqlDataType pType = value.getType();
            if (!pType.isScalar()) {
                Map<String, SqlDataType> pMap = pType.toMap();
                int cols = pMap.size();
                if (cols > 1) {
                    // TensType
                    /*
                     * bools1: ( bool11, bool12 )
                     * and   (bool11, bool12) in ( :bools1 )
                     * List<L1> getBools1();        // getter
                     * public interface Bools1PS {      !!
                     *    boolean isBool11();           !!
                     *    boolean isBool12();           !!
                     * }                                !!
                     */
                    List<SqlParam> inParms = pMap.entrySet().stream().map(e -> new SqlParam(e.getKey(), e.getValue())).collect(Collectors.toList());
                    InterfaceWriter iw = new InterfacePS(name, inParms, false);
                    cc.deduplicate(capitalize(pName)+REQUEST, iw);
                }
            }
        }
    }

    @AllArgsConstructor
    private abstract static class TypePS {
        @Getter protected final String name;
        protected abstract void writeDocField(PrintModel pw);
        protected abstract void writeFieldGetter(PrintModel pw);
        protected abstract String primitive();
        protected String wrapper() { throw new IllegalStateException(); }

        protected boolean isScalar() { return true; }
        protected int columns() { return 0; }
        protected String generic() { throw new IllegalStateException();  }
    }
    private static class BaseType extends TypePS {
        private final SqlDataType type;

        protected BaseType(String name, SqlDataType type) {
            super(name);
            this.type = type;
        }

        @Override
        protected void writeDocField(PrintModel pw) {
            pw.printf(" *     private %s %s;%n", type.getPrimitive(), name);
        }

        @Override
        protected void writeFieldGetter(PrintModel pw) {
            pw.printf("%s %s%s();%n", type.getPrimitive(), type.getterPrefix(), capitalize(name));
        }

        @Override
        protected String primitive() { return type.getPrimitive(); }

        @Override
        protected String wrapper() { return type.getWrapper(); }
    }
    private static class VectType extends TypePS {
        private final SqlDataType type;

        public VectType(String name, SqlDataType type) {
            super(name);
            this.type = type;
        }

        @Override
        protected void writeDocField(PrintModel pw) {
            pw.printf(" *     private List&lt;%s&gt; %s;%n", type.getWrapper(), name);
        }

        @Override
        protected void writeFieldGetter(PrintModel pw) {
            pw.printf("List<%s> get%s();%n", type.getWrapper(), capitalize(name));
        }

        @Override
        protected String primitive() {
            return "["+type.getPrimitive()+"]";
        }
        protected boolean isScalar() { return false; }
        protected int columns() { return 1; }
    }
    private static class TensType extends TypePS {
        private final String type;
        private final int n;
        private final int cols;

        public TensType(String kName, String pName, int ord, int cols) {
            super(pName);
            this.type = kName;
            this.n = ord;
            this.cols = cols;
        }

        @Override
        protected void writeDocField(PrintModel pw) {
            pw.printf(" *     private List&lt;Dto%1$s&gt; %2$s;\t// {@link %3$s %3$s}%n", capitalize(name), name, type);
        }

        @Override
        protected void writeFieldGetter(PrintModel pw) {
            pw.printf("List<L%d> get%s();%n", n, capitalize(name));
        }

        @Override
        protected String primitive() {
            return "["+type+"]";
        }
        protected boolean isScalar() { return false; }
        protected int columns() { return cols; }
        protected String generic() { return "L"+n;  }
    }
    private static class NestType extends TypePS {
        private final String type;

        protected NestType(String clazz, String oName) {
            super(oName);
            this.type = clazz;
        }

        @Override
        protected void writeDocField(PrintModel pw) {
            pw.printf(" *     private Dto%1$s %2$s;\t// {@link %3$s %3$s}%n", capitalize(name), name, type);
        }

        @Override
        protected void writeFieldGetter(PrintModel pw) {
            pw.printf("%s get%s();%n", type, capitalize(name));
        }

        @Override
        protected String primitive() { return type; }

        @Override
        protected String wrapper() { return type; }
    }

    private final List<TypePS> main = new LinkedList<>();

    public InterfacePS(String name, Collection<SqlParam> values, boolean isDelegate) throws InvalidQueryException {
        this.delegate = isDelegate;
        Map<String, Collection<SqlParam>> next = new LinkedHashMap<>();
        int kOrd = 0;
        for(SqlParam value: values) {
            String pName = value.getName();
            SqlDataType pType = value.getType();
            if (pType.isScalar()) {
                int kDot = pName.indexOf('.');
                if (kDot < 0) {
                    main.add(new BaseType(pName, value.getType()));
                } else {
                    String ante = pName.substring(0, kDot);
                    String post = pName.substring(kDot + 1);
                    Collection<SqlParam> flds = next.computeIfAbsent(ante, k -> new ArrayList<>());
                    flds.add(new SqlParam(post, value.getType()));
                }
            } else {
                if (isDelegate)
                    throw new InvalidQueryException("Invalid names for delegate fields: " + pName);
                Map<String, SqlDataType> pMap = pType.toMap();
                int cols = pMap.size();
                if (cols == 1) {
                    // VectType
                    /*
                     * bools1: ( bool11 )
                     * and   (bool11) in ( :bools1 )
                     * List<Boolean> getBools1();        // getter
                     */
                    SqlDataType kType = pMap.values().iterator().next();
                    main.add(new VectType(pName, kType));
                } else {
                    // TensType
                    /*
                     * bools1: ( bool11, bool12 )
                     * and   (bool11, bool12) in ( :bools1 )
                     * List<L1> getBools1();        // getter
                     * public interface Bools1PS {      !!
                     *    boolean isBool11();           !!
                     *    boolean isBool12();           !!
                     * }                                !!
                     */
                    List<SqlParam> inParms = pMap.entrySet().stream().map(e -> new SqlParam(e.getKey(), e.getValue())).collect(Collectors.toList());
                    InterfaceWriter iw = new InterfacePS(name, inParms, false);
                    String kName = cc.deduplicate(capitalize(pName)+REQUEST, iw);
                    main.add(new TensType(kName, pName, ++kOrd, cols));
                }
            }
        }
        if (!next.isEmpty()) {
            if (isDelegate)
                throw new InvalidQueryException("Invalid names for delegate fields: " + String.join(",", next.keySet()));
            /*
             * a.foo    -- varchar / String
             * a.bar    -- int / int
             * b.foo    -- varchar / String
             * b.bar    -- int / int
             * interface MethodAPS {
             *   void getFoo(String foo);
             *   void getBar(int bar);
             * }
             * interface MethodPS {
             *   MethodAPS getA();
             *   MethodAPS getB();
             * }
             */
            for (Map.Entry<String, Collection<SqlParam>> ee : next.entrySet()) {
                String oName = ee.getKey();
                String fullName = name + capitalize(oName);
                InterfaceWriter iw = new InterfacePS(fullName, ee.getValue(), false);
                String kName = cc.deduplicate(capitalize(fullName) + REQUEST, iw);
                main.add(new NestType(kName, oName)); // new NestType(MethodARS, a)
            }
        }
    }

    @Override
    public String signature() {
        List<TypePS> ordMain = new ArrayList<>(main);
        ordMain.sort(Comparator.comparing(TypePS::getName));
        List<String> arguments = new ArrayList<>();
        ordMain.forEach(it-> arguments.add(it.name+": "+it.primitive()));
        return (delegate ? "G" : "I") + "::" + REQUEST + "::" + String.join(", ", arguments);
    }

    @Override
    public void writeDelegate(PrintModel ipw, String iName, boolean java7) {
        ipw.printf("public static class Delegate%s%s {%n", iName, generics(false));
        ipw.more();
        ipw.printf("private Delegate%s" + "() {}%n", iName);
        main.forEach(ps -> {
            String claz = ps.wrapper();
            ipw.printf("protected %s<%s> %s;%n", cc.supplier(), claz, ps.name);
        });
        ipw.printf("public static Builder%s builder() { return new Builder%1$s(); }%n", iName);
        ipw.printf("public static class Builder%s%s {%n", iName, generics(false));
        ipw.more();
        ipw.printf("private Builder%s() {}%n", iName);
        main.forEach(ps -> {
            String claz = ps.wrapper();
            ipw.printf("private %s<%s> %s;%n", cc.supplier(), claz, ps.name);
        });
        ipw.printf("public Delegate%s build() {%n", iName);
        ipw.more();
        ipw.printf("Delegate%s result = new Delegate%1$s();%n", iName);
        delegateRequestFields(ipw, java7);
        ipw.printf("return  result;%n");
        ipw.ends();
        main.forEach(ps -> ipw.printf("public Builder%s %s(%s<%s> %2$s) { this.%2$s = %2$s; return this; }%n",
                iName, ps.name, cc.supplier(), ps.wrapper()));
        ipw.ends(); // Builder
        ipw.ends(); // Delegate
    }

    private String generics(boolean isDoc) {
        List<String> generics = main.stream()
                .filter(it -> !it.isScalar() && it.columns() > 1)
                .map(it -> (isDoc) ?
                        String.format("Dto%s", capitalize(it.name)) :
                        String.format("%s extends %s%s", it.generic(), capitalize(it.name), REQUEST))
                .collect(Collectors.toList());
        if (generics.isEmpty()) return "";
        if (isDoc) {
            return "&lt;" + String.join(", ", generics) + "&gt";
        } else {
            return "<" + String.join(", ", generics) + ">";
        }
    }

    private void delegateRequestFields(PrintModel ipw, boolean java7) {
        if (java7) {
            cc.add(RUNTIME_EMSQL);
            main.forEach(it -> ipw.printf("result.%s = %1$s==null ? EmSQL.<%s>getDummySupplier() : %1$s;%n", it.name, it.wrapper()));
        } else {
            main.forEach(it -> ipw.printf("result.%s = %1$s==null ? () -> null : %1$s;%n", it.name));
        }
    }

    @Override
    public void writeStandard(PrintModel ipw, String methodName) {
        docInterfacePS(ipw, methodName);
        ipw.printf("public interface %s%s {%n" , methodName, generics(false));
        ipw.more();
        main.forEach(ps -> ps.writeFieldGetter(ipw));
        ipw.ends();
    }

    public void docInterfacePS(PrintModel ipw, String ifName) {
        ipw.printf("/**%n");
        ipw.printf(" * Example of DTO class using interface (getter)%n");
        ipw.printf(" *<pre>%n");
        ipw.printf(" *{@literal @}Data%n");
        ipw.printf(" * public class %s implements %s%s {%n", toDto(ifName), ifName, generics(true));
        main.forEach(ps -> ps.writeDocField(ipw));
        ipw.printf(" * }%n");
        ipw.printf(" *</pre>%n");
        ipw.printf(" */%n");
    }
    private String toDto(String iName) {
        if (iName.endsWith(REQUEST)) {
            iName = iName.substring(0, iName.length()-REQUEST.length());
        }
        return "Dto" + iName;
    }
}
