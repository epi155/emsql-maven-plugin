package io.github.epi155.emsql.commons;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static io.github.epi155.emsql.commons.Contexts.cc;
import static io.github.epi155.emsql.commons.Tools.getterOf;
import static io.github.epi155.emsql.commons.Tools.setterOf;

@Setter
@Getter
@AllArgsConstructor
@ToString
public class SqlParam {
    private final String name;
    private final SqlDataType type;

    public void setParameter(PrintModel ipw) {
        String source = String.format("i.%s()", getterOf(this));
        type.psSet(ipw, source);
    }
    public void setParameter(PrintModel ipw, int k) {
        String source = String.format("i.%s()", getterOf(this));
        type.psSet(ipw, source, k);
    }
    public void setDelegateParameter(PrintModel ipw) {
        String source = String.format("i.%s.get()", name);
        type.psSet(ipw, source);
    }
    public void setDelegateParameter(PrintModel ipw, int k) {
        String source = String.format("i.%s.get()", name);
        type.psSet(ipw, source, k);
    }
    public void pushParameter(PrintModel ipw) {
        type.psPush(ipw, name);
    }
    public void pushParameter(PrintModel ipw, int k) {
        type.psPush(ipw, name, k);
    }
    public void setValue(PrintModel ipw) {
        type.psSet(ipw, name);
    }
    public void setValue(PrintModel ipw, int k) {
        type.psSet(ipw, name, k);
    }

    public void fetchParameter(PrintModel ipw, int k) {
        if (cc.isDebug()) {
            ipw.printf("%s o%d = ", type.getPrimitive(), k);
            type.rsGetValue(ipw, k);
            ipw.putf(";%n");
            ipw.printf("o.%s(o%d);%n", setterOf(name), k);
        } else {
            ipw.printf("o.%s(", setterOf(name));
            type.rsGetValue(ipw, k);
            ipw.putf(");%n");
        }
    }
    public void fetchDelegateParameter(PrintModel ipw, int k) {
        if (cc.isDebug()) {
            ipw.printf("%s o%d = ", type.getPrimitive(), k);
            type.rsGetValue(ipw, k);
            ipw.putf(";%n");
            ipw.printf("o.%s.accept(o%d);%n", name, k);
        } else {
            ipw.printf("o.%s.accept(", name);
            type.rsGetValue(ipw, k);
            ipw.putf(");%n");
        }
    }
    public void pullParameter(PrintModel ipw, Integer k) {
        if (cc.isDebug()) {
            ipw.printf("%s o%d = ", type.getPrimitive(), k);
            type.rsGetValue(ipw, k);
            ipw.putf(";%n");
            ipw.printf("EmSQL.set(o, \"%s\", o%d);%n", name, k);
        } else {
            ipw.printf("EmSQL.set(o, \"%s\", ", name);
            type.rsGetValue(ipw, k);
            ipw.putf(");%n");
        }
    }

    public void fetchValue(PrintModel ipw, int k) {
        ipw.printf("%s o = ", type.getPrimitive());
        type.rsGetValue(ipw, k);
        ipw.putf(";%n");
    }

    public void registerOutParms(PrintModel ipw, int k) {
        type.registerOut(ipw, k);
    }

    public void getValue(PrintModel ipw, int k) {
//        ipw.printf("return ");
        ipw.printf("%s o = ", type.getPrimitive());
        type.csGetValue(ipw, k);
        ipw.putf(";%n");
    }

    public void getParameter(PrintModel ipw, int k) {
        if (cc.isDebug()) {
            ipw.printf("%s o%d = ", type.getPrimitive(), k);
            type.csGetValue(ipw, k);
            ipw.putf(";%n");
            ipw.printf("o.%s(o%d);%n", setterOf(name), k);
        } else {
            ipw.printf("o.%s(", setterOf(name));
            type.csGetValue(ipw, k);
            ipw.putf(");%n");
        }
    }
    public void takeParameter(PrintModel ipw, Integer k) {
        if (cc.isDebug()) {
            ipw.printf("%s o%d = ", type.getPrimitive(), k);
            type.csGetValue(ipw, k);
            ipw.putf(";%n");
            ipw.printf("EmSQL.set(o, \"%s\", o%d);%n", name, k);
        } else {
            ipw.printf("EmSQL.set(o, \"%s\", ", name);
            type.csGetValue(ipw, k);
            ipw.putf(");%n");
        }
    }
    public void getDelegateParameter(PrintModel ipw, int k) {
        if (cc.isDebug()) {
            ipw.printf("%s o%d = ", type.getPrimitive(), k);
            type.csGetValue(ipw, k);
            ipw.putf(";%n");
            ipw.printf("o.%s.accept(o%d);%n", name, k);
        } else {
            ipw.printf("o.%s.accept(", name);
            type.csGetValue(ipw, k);
            ipw.putf(");%n");
        }
    }
}
