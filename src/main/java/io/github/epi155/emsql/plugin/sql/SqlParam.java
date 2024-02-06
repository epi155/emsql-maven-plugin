package io.github.epi155.emsql.plugin.sql;

import io.github.epi155.emsql.plugin.ClassContext;
import io.github.epi155.emsql.plugin.IndentPrintWriter;
import io.github.epi155.emsql.plugin.Tools;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import static io.github.epi155.emsql.plugin.Tools.*;

@Setter
@Getter
@AllArgsConstructor
public class SqlParam {
    private final String name;
    private final SqlEnum type;

    public void setParameter(IndentPrintWriter ipw, int k) {
        String source = String.format("i.%s()", getterOf(this));
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
        String target = String.format("o.%s", Tools.setterOf(name));
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
        type.psGet(ipw, k, setterOf(name), cc);
    }
}
