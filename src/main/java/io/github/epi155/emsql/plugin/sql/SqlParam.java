package io.github.epi155.emsql.plugin.sql;

import io.github.epi155.emsql.plugin.ClassContext;
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

    public void setParameter(IndentPrintWriter ipw, ClassContext cc) {
        String source = String.format("i.%s()", getterOf(this));
        type.psSet(ipw, source, cc);
    }
    public void setDelegateParameter(IndentPrintWriter ipw, ClassContext cc) {
        String source = String.format("i.%s.get()", name);
        type.psSet(ipw, source, cc);
    }
    public void pushParameter(IndentPrintWriter ipw, ClassContext cc) {
        type.psPush(ipw, name, cc);
    }
    public void setValue(IndentPrintWriter ipw, ClassContext cc) {
        type.psSet(ipw, name, cc);
    }

    public void fetchParameter(IndentPrintWriter ipw, int k, ClassContext cc) {
        String target = String.format("o.%s", setterOf(name));
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
        type.csGetValue(ipw, k, cc);
    }

    public void getParameter(IndentPrintWriter ipw, int k, ClassContext cc) {
        type.csGet(ipw, k, setterOf(name), cc);
    }
}
