package io.github.epi155.emsql.pojo.dql;

import io.github.epi155.emsql.api.*;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.SqlParam;
import io.github.epi155.emsql.commons.Tools;
import io.github.epi155.emsql.commons.dql.ApiCrsSelect;
import io.github.epi155.emsql.commons.dql.ApiSelectFields;
import io.github.epi155.emsql.commons.dql.DelegateCrsSelect;
import io.github.epi155.emsql.commons.dql.DelegateSelectFields;
import io.github.epi155.emsql.pojo.PojoAction;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

import static io.github.epi155.emsql.commons.Contexts.cc;
import static io.github.epi155.emsql.commons.Contexts.mc;

public class SqlCursorForSelect extends PojoAction
        implements ApiSelectFields, ApiCrsSelect,
        CursorForSelectModel {
    private final DelegateSelectFields delegateSelectFields;
    private final DelegateCrsSelect delegateCrsSelect;
    @Getter
    @Setter
    private InputModel input;
    @Getter
    @Setter
    private OutputModel output;
    @Getter
    @Setter
    private Integer fetchSize;
    @Setter
    private ProgrammingModeEnum mode = ProgrammingModeEnum.Imperative;

    public SqlCursorForSelect() {
        super();
        this.delegateSelectFields = new DelegateSelectFields(this);
        this.delegateCrsSelect = new DelegateCrsSelect(this);
    }

    @Override
    public JdbcStatement sql(Map<String, SqlDataType> fields) throws InvalidQueryException {
        return delegateSelectFields.sql(fields);
    }

    @Override
    public void writeMethod(PrintModel ipw, String name, JdbcStatement jdbc, String kPrg) {

        if (mode == ProgrammingModeEnum.Functional) {
            writeFunctional(ipw, name, jdbc, kPrg);
        } else {
            writeImperative(ipw, name, jdbc, kPrg);
        }
    }

    private void writeImperative(PrintModel ipw, String name, JdbcStatement jdbc, String kPrg) {
        Map<Integer, SqlParam> oMap = jdbc.getOMap();
        int oSize = oMap.size();
        if (oSize < 1) throw new IllegalStateException("Invalid output parameter number");
        docBegin(ipw);
        docInput(ipw, jdbc);
        docOutput(ipw, oMap);
        docEnd(ipw);
        String cName = Tools.capitalize(name);

        ipw.printf("public static ");
        declareGenerics(ipw, cName, jdbc.getTKeys());
        if (oSize == 1) {
            String oType = oMap.get(1).getType().getWrapper();
            cc.add("io.github.epi155.emsql.runtime.SqlCursor");
            ipw.putf("SqlCursor<%s> open%s(%n", oType, cName);
        } else {
            if (mc.isOutputDelegate()) {
                cc.add("io.github.epi155.emsql.runtime.SqlDelegateCursor");
                ipw.putf("SqlDelegateCursor open%s(%n", cName);
            } else {
                cc.add("io.github.epi155.emsql.runtime.SqlCursor");
                ipw.putf("SqlCursor<O> open%s(%n", cName);
            }
        }

        ipw.printf("        final Connection c");
        declareInput(ipw, jdbc);
        declareOutput(ipw);
        ipw.more();

        delegateCrsSelect.writeOpenCode(ipw, jdbc, kPrg);
    }


    private void writeFunctional(PrintModel ipw, String name, JdbcStatement jdbc, String kPrg) {
        Map<Integer, SqlParam> oMap = jdbc.getOMap();
        int oSize = oMap.size();
        if (oSize < 1) throw new IllegalStateException("Invalid output parameter number");
        docBegin(ipw);
        docInput(ipw, jdbc);
        docOutputUse(ipw, oMap);
        docEnd(ipw);
        String cName = Tools.capitalize(name);

        ipw.printf("public static ");
        declareGenerics(ipw, cName, jdbc.getTKeys());

        ipw.putf("void loop%1$s(%n", cName);
        ipw.printf("        final Connection c");
        declareInput(ipw, jdbc);
        declareOutputUse(ipw, oMap.get(1).getType().getWrapper());
        ipw.more();

        delegateCrsSelect.writeForEachCode(ipw, jdbc, kPrg);
    }

}
