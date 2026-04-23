package io.github.epi155.emsql.commons;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static io.github.epi155.emsql.commons.Tools.getterOf;

@Setter
@Getter
@AllArgsConstructor
@ToString
public class SqlParam {
    protected final String name;
    protected final SqlDataType type;

    public void setParameter(PrintModel ipw) {
        String source = String.format("i.%s()", getterOf(this));
        type.psSet(ipw, source);
    }

    public void setParameter(PrintModel ipw, int k) {
        String source = String.format("i.%s()", getterOf(this));
        type.psSet(ipw, source, k);
    }

    public void setValue(PrintModel ipw) {
        type.psSet(ipw, name);
    }

    public void setValue(PrintModel ipw, int k) {
        type.psSet(ipw, name, k);
    }


}
