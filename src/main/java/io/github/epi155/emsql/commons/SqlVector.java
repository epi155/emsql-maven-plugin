package io.github.epi155.emsql.commons;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import lombok.val;

import java.util.LinkedHashMap;
import java.util.Map;

import static io.github.epi155.emsql.commons.Contexts.*;
import static io.github.epi155.emsql.commons.Tools.getterOf;

public class SqlVector implements SqlDataType {

    private final SqlParam[] params;
    private int id;

    public SqlVector(SqlParam...params) {
        this.params = params;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public void psSet(PrintModel ipw, String source) {
        cc.add("java.util.List");
        if (params.length==1) {
            ipw.printf("for (%s ei: %s) {%n", params[0].getType().getPrimitive(), source);
            ipw.more();
            for(val param: params) {
                param.getType().psSet(ipw, "ei");
            }
            ipw.ends();
        } else {
            ipw.printf("for (L%d ei: %s) {%n", id, source);
            ipw.more();
            for(val param: params) {
                param.getType().psSet(ipw, "ei."+getterOf(param)+"()");
            }
            ipw.ends();
        }
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name) {
        if (params.length==1) {
            ipw.printf("for (%1$s e%3$s: (List<%2$s>) EmSQL.get(%3$s, \"%4$s\", List.class)) {%n",
                    params[0].getType().getPrimitive(), params[0].getType().getWrapper(), orig, name);
            ipw.more();
            for(val param: params) {
                param.getType().psSet(ipw, "e"+orig);
            }
            ipw.ends();
        } else {
            if (mc.nSize() > IMAX) {
                ipw.printf("for (L%d e%s: (List<L%1$d>) EmSQL.get(%2$s, \"%3$s\", List.class)) {%n", id, orig, name);
            } else {
                ipw.printf("for (L%d e%s: %s) {%n", id, orig, name);
            }
            ipw.more();
            for(val param: params) {
                param.getType().xPsPush(ipw, "e"+orig, param.getName());
            }
            ipw.ends();
        }

    }

    @Override
    public String getGeneric() {
        return "L"+id;
    }
    @Override
    public String getPrimitive() {
        if (params.length == 1) {
            return "List<" + params[0].getType().getWrapper() + ">";
        } else {
            return "List<L" + id + ">";
        }
    }
    @Override
    public String getContainer() {
        return "List";
    }
    @Override
    public boolean isScalar() { return false; }

    @Override
    public int columns() { return params.length; }
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, SqlDataType> toMap() {
        Map<String, SqlDataType> map = new LinkedHashMap<>();
        for (val param: params) {
            map.putIfAbsent(param.getName(), param.getType());
        }
        return map;
    }
}
