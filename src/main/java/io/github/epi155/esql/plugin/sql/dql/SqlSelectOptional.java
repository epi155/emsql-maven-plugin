package io.github.epi155.esql.plugin.sql.dql;

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

public class SqlSelectOptional extends SqlAction implements ApiSelect {
    private final DelegateSelect delegateSelect;
    @Getter
    @Setter
    private ComAreaStd input;
    @Getter
    @Setter
    private ComAreaStd output;

    SqlSelectOptional() {
        super();
        this.delegateSelect = new DelegateSelect(this);
    }
    @Override
    public JdbcStatement sql(Map<String, SqlEnum> fields) throws MojoExecutionException {
        return delegateSelect.sql(fields);
    }

    @Override
    public void writeMethod(IndentPrintWriter ipw, String name, JdbcStatement jdbc, String kPrg, ClassContext cc) {
        cc.add("io.github.epi155.esql.runtime.ESqlCode");
        cc.add("java.util.Optional");

        Map<Integer, SqlParam> iMap = jdbc.getIMap();
        Map<Integer, SqlParam> oMap = jdbc.getOMap();
        int iSize = iMap.size();
        int oSize = oMap.size();
        if (oSize < 1) throw new IllegalStateException("Invalid output parameter number");
        String cName = Tools.capitalize(name);
        docBegin(ipw);
        docInput(ipw, iMap);
        docOutput(ipw, oMap);
        docEnd(ipw);

        ipw.printf("public static ");
        declareGenerics(ipw, cName, iSize, oSize);
        if (oSize == 1) {
            String oType = oMap.get(1).getType().getWrapper();
            ipw.putf("Optional<%s> %s(%n", oType, name);
        } else {
            if (output != null && output.isDelegate()) {
                ipw.putf("boolean %s(%n", name);
            } else {
                ipw.putf("Optional<O> %s(%n", name);
            }
        }

        ipw.printf("        Connection c");
        declareInput(ipw, iMap, cName);
        declareOutput(ipw, oSize, cc);
        ipw.more();
        ipw.printf("try (PreparedStatement ps = c.prepareStatement(Q_%s)) {%n", kPrg);
        ipw.more();
        setInput(ipw, iMap);
        ipw.printf("ps.setFetchSize(2);%n");
        if (getTimeout() != null) ipw.printf("ps.setQueryTimeout(%d);%n", getTimeout());
        debugAction(ipw, kPrg, iMap, cc);
        ipw.printf("try (ResultSet rs = ps.executeQuery()) {%n");
        ipw.more();
        ipw.printf("if (rs.next()) {%n");
        ipw.more();
        fetch(ipw, oMap, cc);
        ipw.printf("if (rs.next()) {%n");
        ipw.more();
        ipw.printf("throw ESqlCode.N811.getInstance();%n");
        ipw.orElse();
        if (output != null && output.isDelegate() && oSize > 1) {
            ipw.printf("return true;%n");
        } else {
            ipw.printf("return Optional.of(o);%n");
        }
        ipw.ends();
        ipw.orElse();
        if (output != null && output.isDelegate() && oSize > 1) {
            ipw.printf("return false;%n");
        } else {
            ipw.printf("return Optional.empty();%n");
        }
        ipw.ends();
        ipw.ends();
        ipw.ends();
        ipw.ends();

    }
}
