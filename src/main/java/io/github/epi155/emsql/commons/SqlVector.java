package io.github.epi155.emsql.commons;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import io.github.epi155.emsql.api.SqlVectorType;
import lombok.val;

import java.util.LinkedHashMap;
import java.util.Map;

import static io.github.epi155.emsql.commons.Contexts.cc;
import static io.github.epi155.emsql.commons.Tools.getterOf;

public class SqlVector implements SqlVectorType {

    private final SqlParam[] params;
    private int id;

    public SqlVector(SqlParam... params) {
        this.params = params;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getWrapper() {
        return getPrimitive();
    }

    @Override
    public void psSet(PrintModel ipw, String source) {
        cc.add("java.util.List");
        if (params.length == 1) {
            ipw.printf("for (%s ei: %s) {%n", params[0].getType().getPrimitive(), source);
            ipw.more();
            for (val param : params) {
                param.getType().psSet(ipw, "ei");
            }
            ipw.ends();
        } else {
            ipw.printf("for (L%d ei: %s) {%n", id, source);
            ipw.more();
            for (val param : params) {
                param.getType().psSet(ipw, "ei." + getterOf(param) + "()");
            }
            ipw.ends();
        }
    }

    @Override
    public void psSet(PrintModel ipw, String source, int k) {
        // dead branch - Exception in Tools$1MapStore.push
        throw new IllegalStateException();
    }

    @Override
    public String getGeneric() {
        return "L" + id;
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
    public int columns() {
        return params.length;
    }

    @Override
    public Map<String, SqlDataType> toMap() {
        Map<String, SqlDataType> map = new LinkedHashMap<>();
        for (val param : params) {
            map.putIfAbsent(param.getName(), param.getType());
        }
        return map;
    }
}
