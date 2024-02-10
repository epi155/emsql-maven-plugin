package io.github.epi155.emsql.plugin.xtype;

import io.github.epi155.emsql.plugin.ClassContext;
import io.github.epi155.emsql.plugin.IndentPrintWriter;
import io.github.epi155.emsql.plugin.sql.SqlKind;
import io.github.epi155.emsql.plugin.sql.SqlParam;
import lombok.val;

import java.util.LinkedHashMap;
import java.util.Map;

import static io.github.epi155.emsql.plugin.Tools.capitalize;
import static io.github.epi155.emsql.plugin.Tools.getterOf;
import static io.github.epi155.emsql.plugin.sql.SqlAction.REQUEST;

public class SqlVector implements SqlKind {

    private final SqlParam[] params;
    private String name;
    private int id;

    public SqlVector(SqlParam...params) {
        this.params = params;
    }

    @Override
    public void setName(String name, int id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public void psSet(IndentPrintWriter ipw, String source, ClassContext cc) {
        cc.add("java.util.List");
        if (params.length==1) {
            ipw.printf("for (%s ei: %s) {%n", params[0].getType().getPrimitive(), source);
            ipw.more();
            for(val param: params) {
                param.getType().psSet(ipw, "ei", cc);
            }
            ipw.ends();
        } else {
            ipw.printf("for (%s ei: %s) {%n", self(), source);
            ipw.more();
            for(val param: params) {
                param.getType().psSet(ipw, "ei."+getterOf(param)+"()", cc);
            }
            ipw.ends();
        }
    }

    @Override
    public void psPush(IndentPrintWriter ipw, String name, ClassContext cc) {
        if (params.length==1) {
            ipw.printf("for (%s ei: (List<%s>) EmSQL.get(i, \"%s\", List.class)) {%n",
                    params[0].getType().getPrimitive(), params[0].getType().getWrapper(), name);
        } else {
            throw new IllegalStateException();
        }
        ipw.more();
        for(val param: params) {
            param.getType().psSet(ipw, "ei", cc);
        }
        ipw.ends();

    }
    private String self() {
        return capitalize(name)+REQUEST;
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
            return "List<" + self() + ">";
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
    public Map<String, SqlKind> toMap() {
        Map<String, SqlKind> map = new LinkedHashMap<>();
        for (val param: params) {
            map.putIfAbsent(param.getName(), param.getType());
        }
        return map;
    }
}
