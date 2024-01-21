package io.github.epi155.esql.plugin;

import lombok.Data;

import java.util.Set;

@Data
public class SqlParam {
    private final String name;
    private final SqlEnum type;

    public void setParameter(IndentPrintWriter ipw, int k) {
        String cName = Tools.capitalize(name);
        type.psSet(ipw, k, cName);
    }
    public void pushParameter(IndentPrintWriter ipw, int k) {
        type.psPush(ipw, k, name);
    }
    public void updateParameter(IndentPrintWriter ipw, int k) {
        String cName = Tools.capitalize(name);
        type.rsUpdate(ipw, k, cName);
    }
    public void setValue(IndentPrintWriter ipw, int k) {
        type.setValue(ipw, k, name);
    }

    public void fetchParameter(IndentPrintWriter ipw, int k, Set<String> set) {
        String cName = Tools.capitalize(name);
        type.rsGet(ipw, k, cName, set);
    }
    public void pullParameter(IndentPrintWriter ipw, Integer k, Set<String> set) {
        type.rsPull(ipw, k, name);
    }

    public void fetchValue(IndentPrintWriter ipw, int k, Set<String> set) {
        type.rsGetValue(ipw, k, set);
    }
    public void updateValue(IndentPrintWriter ipw, int k, Set<String> set) {
        type.rsUpdateValue(ipw, k, set);
    }

    public void register(IndentPrintWriter ipw, int k) {
        type.register(ipw, k);
    }

    public void getValue(IndentPrintWriter ipw, int k, Set<String> set) {
        type.psGetValue(ipw, k, set);
    }

    public void getParameter(IndentPrintWriter ipw, int k, Set<String> set) {
        String cName = Tools.capitalize(name);
        type.psGet(ipw, k, cName, set);
    }
}
