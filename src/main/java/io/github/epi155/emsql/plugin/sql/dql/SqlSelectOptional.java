package io.github.epi155.emsql.plugin.sql.dql;

import io.github.epi155.emsql.plugin.IndentPrintWriter;
import io.github.epi155.emsql.plugin.sql.JdbcStatement;

import static io.github.epi155.emsql.plugin.Tools.cc;
import static io.github.epi155.emsql.plugin.Tools.mc;

public class SqlSelectOptional extends SqlSelectSingle {

    @Override
    public void writeMethod(IndentPrintWriter ipw, String name, JdbcStatement jdbc, String kPrg) {
        cc.add("io.github.epi155.emsql.runtime.SqlCode");
        delegateSelectSignature.signature(ipw, jdbc, name);

        if (mc.oSize() == 1) {
            jdbc.getOMap().forEach((k,v) -> ipw.putf("%s<%s> %s(%n", cc.optional(), v.getType().getWrapper(), name));
        } else {
            if (output != null && output.isDelegate()) {
                ipw.putf("boolean %s(%n", name);
            } else {
                ipw.putf("%s<O> %s(%n", cc.optional(), name);
            }
        }

        delegateSelectSimple.fetch(ipw, jdbc, kPrg);
        ipw.orElse();
        if (mc.isOutoutDelegate() && mc.oSize() > 1) {
            ipw.printf("return true;%n");
        } else {
            ipw.printf("return %s.of(o);%n", cc.optional());
        }
        ipw.ends();
        ipw.orElse();
        if (mc.isOutoutDelegate() && mc.oSize() > 1) {
            ipw.printf("return false;%n");
        } else {
            ipw.printf("return %s.empty();%n", cc.optional());
        }
        ipw.ends();
        ipw.ends();
        ipw.ends();
        ipw.ends();

    }
}
