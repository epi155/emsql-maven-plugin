package io.github.epi155.emsql.commons;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;

import static io.github.epi155.emsql.commons.Contexts.RESPONSE;
import static io.github.epi155.emsql.commons.Contexts.cc;
import static io.github.epi155.emsql.commons.Tools.capitalize;

class InterfaceRS implements InterfaceWriter {

    @AllArgsConstructor
    private abstract static class TypeRS {
        @Getter protected final String name;
        protected abstract void writeDocField(PrintModel pw);
        protected abstract void writeFieldSetter(PrintModel pw);
        protected abstract String primitive();
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

    }
    private final List<TypeRS> main = new LinkedList<>();


    InterfaceRS(String name, Collection<SqlOutParam> values) {
        Map<String, Collection<SqlOutParam>> next = new LinkedHashMap<>();
        for(SqlOutParam value: values) {
            String pName = value.getName();
            int kDot = pName.indexOf('.');
            if (kDot < 0) {
                main.add(new BaseType(pName, value.getType()));
            } else {
                String ante = pName.substring(0, kDot);
                String post = pName.substring(kDot + 1);
                Collection<SqlOutParam> flds = next.computeIfAbsent(ante, k -> new ArrayList<>());
                flds.add(new SqlOutParam(post, value.getType()));
            }
        }
        if (!next.isEmpty()) {
            for(Map.Entry<String, Collection<SqlOutParam>> ee: next.entrySet()) {
                String oName = ee.getKey();
                String fullName = name + capitalize(oName);
                InterfaceWriter iw = new InterfaceRS(fullName, ee.getValue());
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
        return "I" + "::" + RESPONSE + "::" + String.join(", ", arguments);
    }

    @Override
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
