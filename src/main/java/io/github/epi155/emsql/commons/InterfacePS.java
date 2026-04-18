package io.github.epi155.emsql.commons;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

import static io.github.epi155.emsql.commons.Contexts.REQUEST;
import static io.github.epi155.emsql.commons.Contexts.cc;
import static io.github.epi155.emsql.commons.Tools.capitalize;

public class InterfacePS implements InterfaceWriter {

    public static void registerTensor(String name, Collection<SqlParam> values) {
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
                    InterfaceWriter iw = new InterfacePS(name, inParms);
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

        @Override
        protected boolean isScalar() { return false; }

        @Override
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

        @Override
        protected boolean isScalar() { return false; }

        @Override
        protected int columns() { return cols; }

        @Override
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

    }

    private final List<TypePS> main = new LinkedList<>();

    public InterfacePS(String name, Collection<SqlParam> values) {
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
                Map<String, SqlDataType> pMap = pType.toMap();
                int cols = pMap.size();
                if (cols == 1) {
                    SqlDataType kType = pMap.values().iterator().next();
                    main.add(new VectType(pName, kType));
                } else {
                    List<SqlParam> inParms = pMap.entrySet().stream().map(e -> new SqlParam(e.getKey(), e.getValue())).collect(Collectors.toList());
                    InterfaceWriter iw = new InterfacePS(name, inParms);
                    String kName = cc.deduplicate(capitalize(pName)+REQUEST, iw);
                    main.add(new TensType(kName, pName, ++kOrd, cols));
                }
            }
        }
        if (!next.isEmpty()) {
            for (Map.Entry<String, Collection<SqlParam>> ee : next.entrySet()) {
                String oName = ee.getKey();
                String fullName = name + capitalize(oName);
                InterfaceWriter iw = new InterfacePS(fullName, ee.getValue());
                String kName = cc.deduplicate(capitalize(fullName) + REQUEST, iw);
                main.add(new NestType(kName, oName));
            }
        }
    }

    public String signature() {
        List<TypePS> ordMain = new ArrayList<>(main);
        ordMain.sort(Comparator.comparing(TypePS::getName));
        List<String> arguments = new ArrayList<>();
        ordMain.forEach(it-> arguments.add(it.name+": "+it.primitive()));
        return "I" + "::" + REQUEST + "::" + String.join(", ", arguments);
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
