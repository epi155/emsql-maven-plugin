package io.github.epi155.emsql.commons;

import io.github.epi155.emsql.api.InvalidQueryException;
import io.github.epi155.emsql.api.PrintModel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

import static io.github.epi155.emsql.commons.ClassContextImpl.RUNTIME_EMSQL;
import static io.github.epi155.emsql.commons.Contexts.RESPONSE;
import static io.github.epi155.emsql.commons.Contexts.cc;
import static io.github.epi155.emsql.commons.Tools.capitalize;

class InterfaceConstruct {

    @AllArgsConstructor
    private static class BaseType {
        @Getter private final String name;
        private final String primitive;
        private final String wrapper;
    }
    private final List<BaseType> main = new LinkedList<>();
    private final Map<String, List<BaseType>> next = new LinkedHashMap<>();


    InterfaceConstruct(Collection<SqlParam> values) {
        for(SqlParam value: values) {
            String pName = value.getName();
            int kDot = pName.indexOf('.');
            if (kDot < 0) {
                main.add(new BaseType(pName, value.getType().getPrimitive(), value.getType().getWrapper()));
            } else {
                String ante = pName.substring(0, kDot);
                String post = pName.substring(kDot + 1);
                List<BaseType> flds = next.computeIfAbsent(ante, k -> new ArrayList<>());
                flds.add(new BaseType(post, value.getType().getPrimitive(), value.getType().getWrapper()));
            }
        }
    }
    String signature() {
        SortedMap<String, List<BaseType>> ordNext = new TreeMap<>(next);
        for(Map.Entry<String, List<BaseType>> e: ordNext.entrySet()) {
            List<BaseType> ls = e.getValue();
            ls.sort(Comparator.comparing(BaseType::getName));
            ordNext.put(e.getKey(), ls);
        }
//        for(String s: ordNext.keySet()) {
//            List<BaseType> ls = ordNext.get(s);
//            ls.sort(Comparator.comparing(BaseType::getName));
//            ordNext.put(s, ls);
//        }
        List<BaseType> ordMain = new ArrayList<>(main);
        ordMain.sort(Comparator.comparing(BaseType::getName));
        List<String> arguments = new ArrayList<>();
        ordMain.forEach(it-> arguments.add(it.name+": "+it.primitive));
        ordNext.forEach((key, value) ->
                arguments.add(key + ": [" + value
                        .stream()
                        .map(it -> it.name + ": " + it.primitive)
                        .collect(Collectors.joining()) + "]"));
        return String.join(", ", arguments);
    }

    public void writeDelegate(PrintModel ipw, String iName, boolean java7) throws InvalidQueryException {
        if (!next.isEmpty()) {
            throw new InvalidQueryException("Invalid names for delegate fields: " + String.join(",", next.keySet()));
        }
        ipw.printf("public static class Delegate%s {%n", iName);
        ipw.more();
        main.forEach(p -> {
            String claz = p.wrapper;
            ipw.printf("protected %s<%s> %s;%n", cc.consumer(), claz, p.getName());
        });
        ipw.printf("public static Builder%s builder() { return new Builder%1$s(); }%n", iName);
        ipw.printf("public static class Builder%s {%n", iName);
        ipw.more();
        ipw.printf("private Builder%s() {}%n", iName);
        main.forEach(p -> ipw.printf("private %s<%s> %s;%n", cc.consumer(), p.wrapper, p.name));
        ipw.printf("public Delegate%s build() {%n", iName);
        ipw.more();
        ipw.printf("Delegate%s result = new Delegate%1$s();%n", iName);

        if (java7) {
            cc.add(RUNTIME_EMSQL);
            main.forEach(p -> ipw.printf("result.%s = %1$s==null ? EmSQL.<%s>getDummyConsumer() : %1$s;%n", p.name, p.wrapper));
        } else {
            main.forEach(p -> ipw.printf("result.%s = %1$s==null ? it -> {} : %1$s;%n", p.name));
        }


        ipw.printf("return  result;%n");
        ipw.ends();
        main.forEach(p -> ipw.printf("public Builder%s %s(%s<%s> %2$s) { this.%2$s = %2$s; return this; }%n",
                iName, p.name, cc.consumer(), p.wrapper));
        ipw.ends(); // Builder
        ipw.ends(); // Delegate
    }

    public void writeStandard(PrintModel ipw, String iName) {
        docInterfaceRS(ipw, iName);
        ipw.printf("public interface %s {%n", iName);
        ipw.more();
        main.forEach(p -> {
            String cName = capitalize(p.name);
            ipw.printf("void set%s(%s s);%n", cName, p.primitive);
        });
        next.keySet().forEach(p -> {
            String cName = capitalize(p);
            ipw.printf("%1$s%2$s get%1$s();%n", cName, RESPONSE);
        });
        next.forEach((key, value) -> {
            ipw.println();
            String nxName = capitalize(key);
            docInterfaceRS(ipw, nxName, value);
            ipw.printf("public interface %s%s {%n", nxName, RESPONSE);
            ipw.more();
            value.forEach(p -> {
                String cName = capitalize(p.name);
                ipw.printf("void set%s(%s s);%n", cName, p.primitive);
            });
            ipw.ends();
        });
        ipw.ends();
    }

    private void docInterfaceRS(PrintModel ipw, String iName) {
        ipw.printf("/**%n");
        ipw.printf(" * Example of DTO class using interface (setter)%n");
        ipw.printf(" *<pre>%n");
        ipw.printf(" *{@literal @}Data%n");
        ipw.printf(" * public class DtoCustom implements %s {%n", iName);
        main.forEach(k ->
                ipw.printf(" *     private %s %s;%n", k.primitive, k.name));
        next.keySet().forEach(k -> {
            String cName = capitalize(k);
            String claz = cName + RESPONSE;
            ipw.printf(" *     private Dto%s %s; // {@link #%s}%n", cName, k, claz);
        });
        ipw.printf(" * }%n");
        ipw.printf(" *</pre>%n");
        ipw.printf(" */%n");
    }
    private void docInterfaceRS(PrintModel ipw, String iName, Collection<BaseType> fields) {
        ipw.printf("/**%n");
        ipw.printf(" * Example of DTO class using interface (setter)%n");
        ipw.printf(" *<pre>%n");
        ipw.printf(" *{@literal @}Data%n");
        ipw.printf(" * public class DtoCustom implements %s%s {%n", iName, RESPONSE);
        fields.forEach(k ->
                ipw.printf(" *     private %s %s;%n", k.primitive, k.name));
        ipw.printf(" * }%n");
        ipw.printf(" *</pre>%n");
        ipw.printf(" */%n");
    }

}
