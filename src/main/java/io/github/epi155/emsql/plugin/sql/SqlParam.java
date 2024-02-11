package io.github.epi155.emsql.plugin.sql;

import io.github.epi155.emsql.plugin.IndentPrintWriter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static io.github.epi155.emsql.plugin.Tools.getterOf;
import static io.github.epi155.emsql.plugin.Tools.setterOf;

@Setter
@Getter
@AllArgsConstructor
@ToString
public class SqlParam {
    private final String name;
    private final SqlKind type;

    public void setParameter(IndentPrintWriter ipw) {
        String source = String.format("i.%s()", getterOf(this));
        type.psSet(ipw, source);
    }
    public void setDelegateParameter(IndentPrintWriter ipw) {
        String source = String.format("i.%s.get()", name);
        type.psSet(ipw, source);
    }
    public void pushParameter(IndentPrintWriter ipw) {
        type.psPush(ipw, name);
    }
    public void setValue(IndentPrintWriter ipw) {
        type.psSet(ipw, name);
    }

    public void fetchParameter(IndentPrintWriter ipw, int k) {
        String target = String.format("o.%s", setterOf(name));
        type.rsGet(ipw, k, target);
    }
    public void fetchDelegateParameter(IndentPrintWriter ipw, int k) {
        String target = String.format("o.%s.accept", name);
        type.rsGet(ipw, k, target);
    }
    public void pullParameter(IndentPrintWriter ipw, Integer k) {
        type.rsPull(ipw, k, name);
    }

    public void fetchValue(IndentPrintWriter ipw, int k) {
        type.rsGetValue(ipw, k);
    }

    public void registerOutParms(IndentPrintWriter ipw, int k) {
        type.registerOut(ipw, k);
    }

    public void getValue(IndentPrintWriter ipw, int k) {
        type.csGetValue(ipw, k);
    }

    public void getParameter(IndentPrintWriter ipw, int k) {
        type.csGet(ipw, k, setterOf(name));
    }
}
