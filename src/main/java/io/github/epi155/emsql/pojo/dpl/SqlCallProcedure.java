package io.github.epi155.emsql.pojo.dpl;

import io.github.epi155.emsql.api.*;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.Tools;
import io.github.epi155.emsql.commons.dql.ApiDocSignature;
import io.github.epi155.emsql.pojo.PojoAction;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.epi155.emsql.commons.Contexts.mc;

public class SqlCallProcedure extends PojoAction implements ApiDocSignature, CallProcedureModel {
    @Setter
    @Getter
    private InputModel input;
    @Setter @Getter
    private OutFieldsModel output;
    @Setter @Getter
    private InOutFieldsModel inputOutput;
    private final DelegateCallSignature delegateCallSignature;
    public SqlCallProcedure() {
        super();
        this.delegateCallSignature = new DelegateCallSignature(this);
    }

    private static final String CALL_TEMPLATE =
            "^CALL (\\w+)\\((.*)\\)$";
    private static final Pattern regx = Pattern.compile(CALL_TEMPLATE, Pattern.CASE_INSENSITIVE);


    @Override
    public JdbcStatement sql(Map<String, SqlDataType> fields) throws InvalidQueryException {
        String nText = Tools.oneLine(getExecSql());
        Matcher m = regx.matcher(nText);
        if (m.find()) {
            Map<String, SqlDataType> inpFields = new HashMap<>();
            Map<String, SqlDataType> outFields = new HashMap<>();
            Map<String, SqlDataType> ioFields = new HashMap<>();
            fields.forEach((k,v) -> {
                if (inputOutput!=null && inputOutput.getFields().contains(k)) {
                    ioFields.put(k,v);
                } else if (output!=null && output.getFields().contains(k)) {
                    outFields.put(k,v);
                } else {
                    inpFields.put(k,v);
                }
            });
            return Tools.replacePlaceholder(nText, inpFields, outFields, ioFields);

        } else {
            throw new InvalidQueryException("Invalid query format: "+ getExecSql());
        }
    }

    public void writeMethod(PrintModel ipw, String name, JdbcStatement jdbc, String kPrg) {
        delegateCallSignature.signature(ipw, jdbc, name);

        if (mc.oSize() == 0) {
            ipw.putf("void %s(%n", name);
        } else if (mc.oSize() == 1) {
            // oMap.get(1) may be NULL, the output parameter is NOT the first one
            jdbc.getOMap().forEach((k,v) -> ipw.putf("%s %s(%n", v.getType().getPrimitive(), name));
        } else {
            ipw.putf("O %s(%n", name);
        }

        ipw.printf("        final Connection c");
        declareInput(ipw, jdbc);
        declareOutput(ipw);
        ipw.more();
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

    @Override
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
