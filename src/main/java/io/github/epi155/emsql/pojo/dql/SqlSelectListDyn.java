package io.github.epi155.emsql.pojo.dql;

import io.github.epi155.emsql.api.InvalidQueryException;
import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SelectListDynModel;
import io.github.epi155.emsql.api.SqlDataType;
import io.github.epi155.emsql.commons.*;
import io.github.epi155.emsql.commons.dql.*;
import io.github.epi155.emsql.pojo.PojoAction;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

import static io.github.epi155.emsql.commons.Contexts.cc;
import static io.github.epi155.emsql.commons.Contexts.mc;

public class SqlSelectListDyn extends PojoAction
        implements ApiSelectFields, ApiDocSignature, ApiSelectDyn,
        SelectListDynModel {
    private final DelegateSelectDyn delegateSelectDyn;
    private final DelegateSelectDynFields delegateSelectFields;
//    private final DelegateSelectSignature delegateSelectSignature;
    @Getter
    private final Map<String, Map<Integer, SqlParam>> andParms = new LinkedHashMap<>();
    @Setter
    @Getter
    private Map<String, String> optionalAnd = new LinkedHashMap<>();
    @Getter
    @Setter
    private Integer fetchSize;

    public SqlSelectListDyn() {
        super();
        this.delegateSelectFields = new DelegateSelectDynFields(this);
//        this.delegateSelectSignature = new DelegateSelectSignature(this);
        this.delegateSelectDyn = new DelegateSelectDyn(this);
    }

    @Override
    public JdbcStatement sql(Map<String, SqlDataType> fields) throws InvalidQueryException {
        return delegateSelectFields.sql(fields);
    }

    @Override
    protected void customWrite(String kPrg, @NotNull PrintModel ipw) {
        delegateSelectDyn.customWrite(ipw, kPrg);
    }

    @Override
    public void writeMethod(PrintModel ipw, String name, JdbcStatement jdbc, String kPrg) throws InvalidQueryException {
        cc.add("java.util.List");
        cc.add("java.util.ArrayList");
        defineBuilder(ipw, jdbc, name, kPrg);

        TensorArgument tKeys = delegateSelectDyn.packGenerics(name, andParms.values(), jdbc.getTKeys());
        signature(ipw, jdbc, name, tKeys);
        String cName = Tools.capitalize(name);
        ipw.putf("%sBuilder", cName);
        useGenerics(ipw, tKeys);
        ipw.putf(" %s(%n", name);

        ipw.printf("        final Connection c");
        declareInput(ipw, jdbc);
        declareOutput(ipw);
        ipw.more();
        returnBuilder(ipw, jdbc, cName);

        ipw.ends();
    }

    private void signature(PrintModel ipw, JdbcStatement jdbc, String name, TensorArgument tKeys) {
        if (mc.oSize() < 1) throw new IllegalStateException("Invalid output parameter number");
        docBegin(ipw);
        docInput(ipw, jdbc);
        docOutput(ipw, jdbc.getOMap());
        docEnd(ipw);

        String iName = cc.inPrepare(name, jdbc.getIMap().values(), mc);
        String oName = cc.outPrepare(name, jdbc.getOMap().values(), mc);
        ipw.printf("public static ");
        declareGenerics(ipw, tKeys, iName, oName);
    }

    private void returnBuilder(PrintModel ipw, JdbcStatement jdbc, String cName) {
        ipw.printf("return new %sBuilder<>(c", cName);
        pushInput(ipw, jdbc, 1);
        if (mc.oSize() >= 2) {
            ipw.commaLn();
            ipw.printf("so");
        }
        ipw.putf(");%n");
    }

    @Override
    public void docEnd(@NotNull PrintModel ipw) {
        delegateSelectDyn.docEnd(ipw);
    }

    public void defineBuilder(PrintModel ipw, JdbcStatement jdbc, String name, String kPrg) {
        if (mc.oSize() < 1) throw new IllegalStateException("Invalid output parameter number");
        String cName = Tools.capitalize(name);

        String iName = cc.inPrepare(name, jdbc.getIMap().values(), mc);
        String oName = cc.outPrepare(name, jdbc.getOMap().values(), mc);
        // class definition
        TensorArgument tKeys = delegateSelectDyn.packGenerics(name, andParms.values(), jdbc.getTKeys());
        ipw.printf("public static class %sBuilder", cName);
        declareGenerics(ipw, tKeys, iName, oName);

        ipw.putf(" {%n");
        ipw.more();
        ipw.printf("private final Connection c;%n");
        delegateSelectDyn.defineInput(ipw, jdbc);
        delegateSelectDyn.defineOutput(ipw);
        delegateSelectDyn.defineOptional(ipw);
        ipw.println();

        // ctor definition
        ipw.printf("public %sBuilder(%n", cName);
        ipw.printf("        final Connection c");
        declareInput(ipw, jdbc);
        declareOutputPlain(ipw);
        ipw.more();
        ipw.printf("this.c = c;%n");
        delegateSelectDyn.assignInput(ipw, jdbc);
        delegateSelectDyn.assignOutput(ipw);
        ipw.ends();
        delegateSelectDyn.defineMethodArgBuilder(ipw, kPrg, cName, () -> useGenerics(ipw, tKeys));
        defineMethodList(ipw, jdbc, name, kPrg);
        ipw.ends();
    }

    private void defineMethodList(@NotNull PrintModel ipw, @NotNull JdbcStatement jdbc, String name, String kPrg) {
        DocUtils.docResultList(ipw, name);
        ipw.printf("public List<");
        if (mc.oSize() == 1) {
            jdbc.getOMap().forEach((k, v) -> ipw.putf("%s", v.getType().getWrapper()));
        } else {
            ipw.putf("O");
        }
        ipw.putf("> list() throws SQLException {%n");
        ipw.more();

        delegateSelectDyn.writeResultListCode(ipw, jdbc, kPrg);
    }

    @Override
    public void declareInput(PrintModel ipw, @NotNull JdbcStatement jdbc) {
        delegateSelectDyn.declareInput(ipw, jdbc);
    }

    @Override
    public void declareOutput(PrintModel ipw) {
        delegateSelectDyn.declareOutput(ipw);
    }
}
