package io.github.epi155.emsql.pojo;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static io.github.epi155.emsql.pojo.Tools.getterOf;
import static io.github.epi155.emsql.pojo.Tools.setterOf;

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
    public void setDelegateParameter(PrintModel ipw) {
        String source = String.format("i.%s.get()", name);
        type.psSet(ipw, source);
    }
    public void pushParameter(PrintModel ipw) {
        type.psPush(ipw, name);
    }
    public void setValue(PrintModel ipw) {
        type.psSet(ipw, name);
    }

    public void fetchParameter(PrintModel ipw, int k) {
        String target = String.format("o.%s", setterOf(name));
        type.rsGet(ipw, k, target);
    }
    public void fetchDelegateParameter(PrintModel ipw, int k) {
        String target = String.format("o.%s.accept", name);
        type.rsGet(ipw, k, target);
    }
    public void pullParameter(PrintModel ipw, Integer k) {
        type.rsPull(ipw, k, name);
    }

    public void fetchValue(PrintModel ipw, int k) {
        type.rsGetValue(ipw, k);
    }

    public void registerOutParms(PrintModel ipw, int k) {
        type.registerOut(ipw, k);
    }

    public void getValue(PrintModel ipw, int k) {
        type.csGetValue(ipw, k);
    }

    public void getParameter(PrintModel ipw, int k) {
        type.csGet(ipw, k, setterOf(name));
    }
}
