package io.github.epi155.emsql.pojo.dql;

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
            jdbc.getOMap().forEach((k, v) -> ipw.putf("%s<%s> %s(%n", cc.optional(), v.getType().getWrapper(), name));
        } else {
            ipw.putf("%s<O> %s(%n", cc.optional(), name);
        }

        delegateSelectSimple.fetch(ipw, jdbc, kPrg);
        ipw.orElse();
        ipw.printf("return %s.of(o);%n", cc.optional());
        ipw.ends();
        ipw.orElse();
        ipw.printf("log.debug(\"*** NoResult ***\");%n");
        ipw.printf("return %s.empty();%n", cc.optional());
        ipw.ends();
        ipw.ends();
        dumpAction(ipw, kPrg, jdbc);
        ipw.ends(); // end method

    }
}
