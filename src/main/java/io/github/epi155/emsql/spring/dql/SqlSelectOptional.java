package io.github.epi155.emsql.spring.dql;

import io.github.epi155.emsql.api.InvalidQueryException;
import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SelectOptionalModel;
import io.github.epi155.emsql.commons.JdbcStatement;

import static io.github.epi155.emsql.commons.Contexts.cc;
import static io.github.epi155.emsql.commons.Contexts.mc;


public class SqlSelectOptional extends SqlSelectSingle implements SelectOptionalModel {

    @Override
    public void writeMethod(PrintModel ipw, String name, JdbcStatement jdbc, String kPrg) throws InvalidQueryException {
        delegateSelectSignature.signature(ipw, jdbc, name);

        if (mc.oSize() == 1) {
            jdbc.getOMap().forEach((k, v) -> ipw.putf("%s<%s> %s(", cc.optional(), v.getType().getWrapper(), name));
        } else {
            if (mc.isOutputDelegate()) {
                ipw.putf("boolean %s(", name);
            } else {
                ipw.putf("%s<O> %s(", cc.optional(), name);
            }
        }

        delegateSelectSimple.fetch(ipw, jdbc, kPrg);
        ipw.orElse();
        if (mc.isOutputDelegate()) {
            ipw.printf("return true;%n");
        } else {
            ipw.printf("return %s.of(o);%n", cc.optional());
        }
        ipw.ends();
        ipw.orElse();
        ipw.printf("log.debug(\"*** NoResult ***\");%n");
        if (mc.isOutputDelegate()) {
            ipw.printf("return false;%n");
        } else {
            ipw.printf("return %s.empty();%n", cc.optional());
        }
        ipw.ends();
        ipw.ends();
        dumpAction(ipw, kPrg, jdbc);
        ipw.ends(); // end method

    }
}
