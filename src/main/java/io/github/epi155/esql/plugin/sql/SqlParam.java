package io.github.epi155.esql.plugin.sql;

import io.github.epi155.esql.plugin.ClassContext;
import io.github.epi155.esql.plugin.IndentPrintWriter;
import io.github.epi155.esql.plugin.Tools;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import static io.github.epi155.esql.plugin.Tools.getOf;

@Setter
@Getter
@AllArgsConstructor
public class SqlParam {
    private final String name;
    private final SqlEnum type;

    public void setParameter(IndentPrintWriter ipw, int k) {
        String source = String.format("i.%s%s()", getOf(this), Tools.capitalize(name));
        type.psSet(ipw, k, source);
    }
    public void setDelegateParameter(IndentPrintWriter ipw, int k) {
        String source = String.format("i.%s.get()", name);
        type.psSet(ipw, k, source);
    }
    public void pushParameter(IndentPrintWriter ipw, int k) {
        type.psPush(ipw, k, name);
    }
    public void setValue(IndentPrintWriter ipw, int k) {
        type.setValue(ipw, k, name);
    }

    public void fetchParameter(IndentPrintWriter ipw, int k, ClassContext cc) {
        String target = String.format("o.set%s", Tools.capitalize(name));
        type.rsGet(ipw, k, target, cc);
    }
    public void fetchDelegateParameter(IndentPrintWriter ipw, int k, ClassContext cc) {
        String target = String.format("o.%s.accept", name);
        type.rsGet(ipw, k, target, cc);
    }
    public void pullParameter(IndentPrintWriter ipw, Integer k) {
        type.rsPull(ipw, k, name);
    }

    public void fetchValue(IndentPrintWriter ipw, int k, ClassContext cc) {
        type.rsGetValue(ipw, k, cc);
    }

    public void registerOutParms(IndentPrintWriter ipw, int k) {
        type.registerOut(ipw, k);
    }

    public void getValue(IndentPrintWriter ipw, int k, ClassContext cc) {
        type.psGetValue(ipw, k, cc);
    }

    public void getParameter(IndentPrintWriter ipw, int k, ClassContext cc) {
        String cName = Tools.capitalize(name);
        type.psGet(ipw, k, cName, cc);
    }
}
