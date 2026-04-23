package io.github.epi155.emsql.commons;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlScalarType;

import static io.github.epi155.emsql.commons.Contexts.cc;
import static io.github.epi155.emsql.commons.Tools.setterOf;

public class SqlOutParam extends SqlParam {
    public SqlOutParam(String name, SqlScalarType type) {
        super(name, type);
    }
    public SqlScalarType getType() {
        return (SqlScalarType) type;
    }
    public void fetchParameter(PrintModel ipw, int k) {
        if (cc.isDebug()) {
            ipw.printf("%s o%d = ", type.getPrimitive(), k);
            getType().rsGetValue(ipw, k);
            ipw.putf(";%n");
            ipw.printf("o.%s(o%d);%n", setterOf(name), k);
        } else {
            ipw.printf("o.%s(", setterOf(name));
            getType().rsGetValue(ipw, k);
            ipw.putf(");%n");
        }
    }
    public void fetchValue(PrintModel ipw, int k) {
        ipw.printf("%s o = ", type.getPrimitive());
        getType().rsGetValue(ipw, k);
        ipw.putf(";%n");
    }

    public void registerOutParms(PrintModel ipw, int k) {
        getType().registerOut(ipw, k);
    }

    public void getValue(PrintModel ipw, int k) {
        ipw.printf("%s o = ", type.getPrimitive());
        getType().csGetValue(ipw, k);
        ipw.putf(";%n");
    }

    public void getParameter(PrintModel ipw, int k) {
        if (cc.isDebug()) {
            ipw.printf("%s o%d = ", type.getPrimitive(), k);
            getType().csGetValue(ipw, k);
            ipw.putf(";%n");
            ipw.printf("o.%s(o%d);%n", setterOf(name), k);
        } else {
            ipw.printf("o.%s(", setterOf(name));
            getType().csGetValue(ipw, k);
            ipw.putf(");%n");
        }
    }
}
