package io.github.epi155.esql.plugin.sql.dml;

import io.github.epi155.esql.plugin.ClassContext;
import io.github.epi155.esql.plugin.ComAreaStd;
import io.github.epi155.esql.plugin.IndentPrintWriter;
import io.github.epi155.esql.plugin.Tools;
import io.github.epi155.esql.plugin.sql.JdbcStatement;
import io.github.epi155.esql.plugin.sql.SqlAction;
import io.github.epi155.esql.plugin.sql.SqlEnum;
import io.github.epi155.esql.plugin.sql.SqlParam;
import lombok.Getter;
import lombok.Setter;
import org.apache.maven.plugin.MojoExecutionException;

import java.util.Map;

public class SqlInsertBatch extends SqlAction implements ApiInsert {
    private final DelegateInsert delegateInsert;
    @Setter
    @Getter
    private ComAreaStd input;
    @Setter
    private int batchSize = 1024;

    SqlInsertBatch() {
        super();
        this.delegateInsert = new DelegateInsert(this);
    }
    @Override
    public JdbcStatement sql(Map<String, SqlEnum> fields) throws MojoExecutionException {
        return delegateInsert.proceed(fields);
    }
    @Override
    public void writeMethod(IndentPrintWriter ipw, String name, JdbcStatement jdbc, String kPrg, ClassContext cc) {
        Map<Integer, SqlParam> iMap = jdbc.getIMap();
        int iSize = iMap.size();
        if (1<iSize && iSize<=IMAX) {
            cc.add("io.github.epi155.esql.runtime.ESqlInsertBatch"+iSize);
        } else {
            cc.add("io.github.epi155.esql.runtime.ESqlInsertBatch");
        }
        String cName = Tools.capitalize(name);
        docBegin(ipw);
        docInput(ipw, iMap);
        docEnd(ipw);
        declareNewInstance(ipw, "ESqlInsertBatch", iMap, cName);
        ipw.more();
        ipw.printf("PreparedStatement ps = c.prepareStatement(Q_%s);%n", kPrg);
        if (getTimeout() != null) ipw.printf("ps.setQueryTimeout(%d);%n", getTimeout());
        declareReturnNew(ipw, "ESqlInsertBatch", iMap, batchSize);
        ipw.more();
        ipw.printf("@Override%n");
        ipw.printf("public void lazyInsert(%n");
        declareInputBatch(ipw, iMap);
        ipw.closeParenthesisLn();
        ipw.printf("        throws SQLException {%n");
        ipw.more();
        setInput(ipw, iMap);
        ipw.printf("addBatch();%n");
        ipw.ends();
        ipw.less();
        ipw.printf("};%n");
        ipw.ends();
    }
}
