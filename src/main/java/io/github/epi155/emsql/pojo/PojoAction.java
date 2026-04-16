package io.github.epi155.emsql.pojo;

import io.github.epi155.emsql.api.InvalidQueryException;
import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.SqlAction;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import static io.github.epi155.emsql.commons.Contexts.cc;
import static io.github.epi155.emsql.commons.Contexts.mc;

public abstract class PojoAction extends SqlAction {
    public void declareNewInstance(@NotNull PrintModel ipw, String eSqlObject, @NotNull JdbcStatement jdbc, String cName) throws InvalidQueryException {
        String iName = cc.inPrepare(cName, jdbc.getIMap().values(), mc);
        String oName = cc.outPrepare(cName, jdbc.getOMap().values(), mc);
        ipw.printf("public static ");
        declareGenerics(ipw, jdbc.getTKeys(), iName, oName);
        ipw.putf("%s", eSqlObject);
        plainGenericsNew(ipw, jdbc);
        ipw.putf(" new%s(%n", cName);
        ipw.printf("        final Connection c)%n", cName);
        ipw.printf("        throws SQLException {%n");
    }

    public void declareNewInstance(@NotNull PrintModel ipw, String cName, String iName) {
        ipw.printf("public static ");
        batchGenerics(ipw, iName);
        ipw.putf("%s", cName);
        genericsNew(ipw);
        ipw.putf(" new%s(%n", cName);
        ipw.printf("        final Connection c)%n", cName);
        ipw.printf("        throws SQLException {%n");
    }

    public void docBegin(@NotNull PrintModel ipw) {
        ipw.printf("/**%n");
        ipw.printf(" * Template %s%n", this.getClass().getSimpleName());
        ipw.printf(" * <pre>%n");
        val lines = getExecSql().split("\n");
        for (val line : lines) {
            ipw.printf(" * %s%n", line);
        }
        ipw.printf(" * </pre>%n");
        ipw.printf(" *%n");
        ipw.printf(" * @param c connection%n");
        if (isTune())
            ipw.printf(" * @param u query hints setting%n");
    }
}
