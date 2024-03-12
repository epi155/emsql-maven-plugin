package io.github.epi155.emsql.spring.dpl;

import io.github.epi155.emsql.api.*;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.Tools;
import io.github.epi155.emsql.commons.dql.ApiDocSignature;
import io.github.epi155.emsql.spring.SpringAction;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.epi155.emsql.commons.Contexts.cc;
import static io.github.epi155.emsql.commons.Contexts.mc;

public class SqlCallProcedure extends SpringAction implements ApiDocSignature, CallProcedureModel {
    @Setter
    @Getter
    private InputModel input;
    @Setter @Getter
    private OutFieldsModel output;
    private final DelegateCallSignature delegateSelectSignature;
    public SqlCallProcedure() {
        super();
        this.delegateSelectSignature = new DelegateCallSignature(this);
    }

    private static final String tmpl =
            "^CALL (\\w+)\\((.*)\\)$";
    private static final Pattern regx = Pattern.compile(tmpl, Pattern.CASE_INSENSITIVE);


    @Override
    public JdbcStatement sql(Map<String, SqlDataType> fields) throws InvalidQueryException {
        String nText = Tools.oneLine(getExecSql());
        Matcher m = regx.matcher(nText);
        if (m.find()) {
            Map<String, SqlDataType> inpFields = new HashMap<>();
            Map<String, SqlDataType> outFields = new HashMap<>();
            fields.forEach((k,v) -> {
                if (output!=null && output.getFields().contains(k)) {
                    outFields.put(k, v);
                } else {
                    inpFields.put(k,v);
                }
            });
            return Tools.replacePlaceholder(nText, inpFields, outFields);

        } else {
            throw new InvalidQueryException("Invalid query format: "+ getExecSql());
        }
    }

    public void writeMethod(PrintModel ipw, String name, JdbcStatement jdbc, String kPrg) {
        delegateSelectSignature.signature(ipw, jdbc, name);

        if (mc.oSize() == 0) {
            ipw.putf("void %s(", name);
        } else if (mc.oSize() == 1) {
            // oMap.get(1) may be NULL, the output parameter is NOT the first one
            jdbc.getOMap().forEach((k,v) -> ipw.putf("%s %s(", v.getType().getPrimitive(), name));
        } else {
            ipw.putf("O %s(", name);
        }
        ipw.commaReset();

        declareInput(ipw, jdbc);
        declareOutput(ipw);
        ipw.more();
        cc.add("org.springframework.jdbc.datasource.DataSourceUtils");
        ipw.printf("final Connection c = DataSourceUtils.getConnection(dataSource);%n");
        ipw.printf("try (CallableStatement ps = c.prepareCall(Q_%s)) {%n", kPrg);
        ipw.more();
        setInputAbs(ipw, jdbc);
        registerOutAbs(ipw, jdbc.getOMap());
        setQueryHints(ipw);
        debugAction(ipw, kPrg, jdbc);
        ipw.printf("ps.execute();%n");
        getOutput(ipw, jdbc.getOMap());
        if (mc.oSize()>0)
            ipw.printf("return o;%n");
        ipw.ends();
        ipw.ends();
    }

    public void declareNextClass(
            PrintModel ipw,
            String name,
            String eSqlObject,
            JdbcStatement jdbc,
            int batchSize,
            String kPrg) {
        ipw.printf("public static class %s", name);
        declareGenerics(ipw, name, jdbc.getTKeys());
        ipw.putf(" extends %s", eSqlObject);
        plainGenericsNew(ipw, jdbc);
        ipw.putf("{%n");
        ipw.more();
        ipw.printf("protected %s(CallableStatement ps) {%n", name);
        ipw.more();
        ipw.printf("super(Q_%s, ps, %d);%n", kPrg, batchSize);
        ipw.ends();
    }
}
