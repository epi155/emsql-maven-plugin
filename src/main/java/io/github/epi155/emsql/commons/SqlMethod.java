package io.github.epi155.emsql.commons;

import io.github.epi155.emsql.api.InvalidQueryException;
import io.github.epi155.emsql.api.MethodModel;
import io.github.epi155.emsql.api.PerformModel;
import io.github.epi155.emsql.api.PrintModel;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import static io.github.epi155.emsql.commons.Contexts.mc;

@Setter
@Getter
public class SqlMethod implements MethodModel {
    private static final NumberFormat NF = new DecimalFormat("0000");
    private String methodName;
    private PerformModel perform;

    public void writeCode(PrintModel ipw, int km) throws InvalidQueryException {
        mc = new MethodContextImpl(this);
        String kPrg = NF.format(km);
        perform.writeCode(ipw, kPrg);
    }
}
