package io.github.epi155.esql.plugin.sql.dql;

import io.github.epi155.esql.plugin.*;
import io.github.epi155.esql.plugin.sql.JdbcStatement;
import io.github.epi155.esql.plugin.sql.SqlAction;
import io.github.epi155.esql.plugin.sql.SqlEnum;
import io.github.epi155.esql.plugin.sql.SqlParam;
import lombok.Getter;
import lombok.Setter;
import org.apache.maven.plugin.MojoExecutionException;

import java.util.Map;

public class SqlSelectList extends SqlAction implements ApiSelect {
    private final DelegateSelect delegateSelect;
    @Getter
    @Setter
    private ComAreaStd input;
    @Getter
    @Setter
    private ComAreaLst output;
    @Setter
    private Integer fetchSize;

    SqlSelectList() {
        super();
        this.delegateSelect = new DelegateSelect(this);
    }

    @Override
    public JdbcStatement sql(Map<String, SqlEnum> fields) throws MojoExecutionException {
        return delegateSelect.sql(fields);
    }

    @Override
    public void writeMethod(IndentPrintWriter ipw, String name, JdbcStatement jdbc, String kPrg, ClassContext cc) {
        cc.add("java.util.List");
        cc.add("java.util.ArrayList");

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
            ipw.putf("List<%s> %s(%n", oType, name);
        } else {
            ipw.putf("List<O> %s(%n", name);
        }

        ipw.printf("        Connection c");
        declareInput(ipw, iMap, cName);
        declareOutput(ipw, oSize, cc);
        ipw.more();
        ipw.printf("try (PreparedStatement ps = c.prepareStatement(Q_%s)) {%n", kPrg);
        ipw.more();
        setInput(ipw, iMap);
        if (fetchSize != null) ipw.printf("ps.setFetchSize(%d);%n", fetchSize);
        if (getTimeout() != null) ipw.printf("ps.setQueryTimeout(%d);%n", getTimeout());
        debugAction(ipw, kPrg, iMap, cc);
        ipw.printf("try (ResultSet rs = ps.executeQuery()) {%n");
        ipw.more();
        if (oSize == 1) {
            ipw.printf("List<%s> list = new ArrayList<>();%n", oMap.get(1).getType().getWrapper());
        } else {
            ipw.printf("List<O> list = new ArrayList<>();%n");
        }
        ipw.printf("while (rs.next()) {%n");
        ipw.more();
        fetch(ipw, oMap, cc);
        ipw.printf("list.add(o);%n");
        ipw.ends();
        ipw.printf("return list;%n");
        ipw.ends();
        ipw.ends();
        ipw.ends();
    }
}
