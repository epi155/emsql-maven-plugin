package io.github.epi155.emsql.commons;

import io.github.epi155.emsql.api.InvalidQueryException;
import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;

import static io.github.epi155.emsql.commons.ClassContextImpl.RUNTIME_EMSQL;
import static io.github.epi155.emsql.commons.Contexts.RESPONSE;
import static io.github.epi155.emsql.commons.Contexts.cc;
import static io.github.epi155.emsql.commons.Tools.capitalize;

class InterfaceRS implements InterfaceWriter {

    @Getter
    private final boolean delegate;

    @AllArgsConstructor
    private abstract static class TypeRS {
        @Getter protected final String name;
        protected abstract void writeDocField(PrintModel pw);
        protected abstract void writeFieldSetter(PrintModel pw);
        protected abstract String primitive();
        protected abstract String wrapper();
    }
    private static class BaseType extends TypeRS{
        private final SqlDataType type;
        protected BaseType(String name, SqlDataType type) {
            super(name);
            this.type = type;
        }

        protected void writeDocField(PrintModel pw) {
            pw.printf(" *     private %s %s;%n", type.getPrimitive(), name);
        }
        protected void writeFieldSetter(PrintModel pw) {
            pw.printf("void set%s(%s s);%n", capitalize(name), type.getPrimitive());
        }

        @Override
        protected String primitive() { return type.getPrimitive(); }

        @Override
        protected String wrapper() { return type.getWrapper(); }
    }
    private static class NestType extends TypeRS {
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
        protected void writeFieldSetter(PrintModel pw) {
            pw.printf("%s get%s();%n", type, capitalize(name));
        }

        @Override
        protected String primitive() { return type; }

        @Override
        protected String wrapper() { return type; }
    }
    private final List<TypeRS> main = new LinkedList<>();


    InterfaceRS(String name, Collection<SqlParam> values, boolean isDelegate) throws InvalidQueryException {
        this.delegate = isDelegate;
        Map<String, Collection<SqlParam>> next = new LinkedHashMap<>();
        for(SqlParam value: values) {
            String pName = value.getName();
            int kDot = pName.indexOf('.');
            if (kDot < 0) {
                main.add(new BaseType(pName, value.getType()));
            } else {
                String ante = pName.substring(0, kDot);
                String post = pName.substring(kDot + 1);
                Collection<SqlParam> flds = next.computeIfAbsent(ante, k -> new ArrayList<>());
                flds.add(new SqlParam(post, value.getType()));
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
             * interface MethodARS {
             *   void setFoo(String foo);
             *   void setBar(int bar);
             * }
             * interface MethodRS {
             *   MethodARS getA();
             *   MethodARS getB();
             * }
             */
            for(Map.Entry<String, Collection<SqlParam>> ee: next.entrySet()) {
                String oName = ee.getKey();
                String fullName = name + capitalize(oName);
                InterfaceWriter iw = new InterfaceRS(fullName, ee.getValue(), false);
                String kName = cc.deduplicate(capitalize(fullName)+RESPONSE, iw);
                main.add(new NestType(kName, oName)); // new NestType(MethodARS, a)
            }
        }
    }
    public String signature() {
        List<TypeRS> ordMain = new ArrayList<>(main);
        ordMain.sort(Comparator.comparing(TypeRS::getName));
        List<String> arguments = new ArrayList<>();
        ordMain.forEach(it-> arguments.add(it.name+": "+it.primitive()));
        return (delegate ? "S" : "I") + "::" + RESPONSE + "::" + String.join(", ", arguments);
    }

    public void writeDelegate(PrintModel ipw, String iName, boolean java7) {
        ipw.printf("public static class Delegate%s {%n", iName);
        ipw.more();
        main.forEach(p -> {
            String claz = p.wrapper();
            ipw.printf("protected %s<%s> %s;%n", cc.consumer(), claz, p.getName());
        });
        ipw.printf("public static Builder%s builder() { return new Builder%1$s(); }%n", iName);
        ipw.printf("public static class Builder%s {%n", iName);
        ipw.more();
        ipw.printf("private Builder%s() {}%n", iName);
        main.forEach(p -> ipw.printf("private %s<%s> %s;%n", cc.consumer(), p.wrapper(), p.name));
        ipw.printf("public Delegate%s build() {%n", iName);
        ipw.more();
        ipw.printf("Delegate%s result = new Delegate%1$s();%n", iName);

        if (java7) {
            cc.add(RUNTIME_EMSQL);
            main.forEach(p -> ipw.printf("result.%s = %1$s==null ? EmSQL.<%s>getDummyConsumer() : %1$s;%n", p.name, p.wrapper()));
        } else {
            main.forEach(p -> ipw.printf("result.%s = %1$s==null ? it -> {} : %1$s;%n", p.name));
        }


        ipw.printf("return  result;%n");
        ipw.ends();
        main.forEach(p -> ipw.printf("public Builder%s %s(%s<%s> %2$s) { this.%2$s = %2$s; return this; }%n",
                iName, p.name, cc.consumer(), p.wrapper()));
        ipw.ends(); // Builder
        ipw.ends(); // Delegate
    }

    public void writeStandard(PrintModel ipw, String iName) {
        docInterfaceRS(ipw, iName);
        ipw.printf("public interface %s {%n", iName);
        ipw.more();
        main.forEach(p -> p.writeFieldSetter(ipw));
        ipw.ends();
    }

    private void docInterfaceRS(PrintModel ipw, String ifName) {
        ipw.printf("/**%n");
        ipw.printf(" * Example of DTO class using interface (setter)%n");
        ipw.printf(" *<pre>%n");
        ipw.printf(" *{@literal @}Data%n");
        ipw.printf(" * public class %s implements %s {%n", toDto(ifName), ifName);
        main.forEach(k -> k.writeDocField(ipw));
        ipw.printf(" * }%n");
        ipw.printf(" *</pre>%n");
        ipw.printf(" */%n");
    }
    private String toDto(String iName) {
        if (iName.endsWith(RESPONSE)) {
            iName = iName.substring(0, iName.length()-RESPONSE.length());
        }
        return "Dto" + iName;
    }

}
