package io.github.epi155.emsql.spring.dpl;

import io.github.epi155.emsql.api.InlineProcedureModel;
import io.github.epi155.emsql.api.InvalidQueryException;
import io.github.epi155.emsql.api.SqlDataType;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.Tools;

import java.util.HashMap;
import java.util.Map;

public class SqlInlineProcedure extends SqlCallProcedure implements InlineProcedureModel {

    @Override
    public JdbcStatement sql(Map<String, SqlDataType> fields) throws InvalidQueryException {
        String nText = getExecSql();
        String uText = nText.toUpperCase();
        int k0 = uText.indexOf("DECLARE");
        int k1 = uText.indexOf("BEGIN");
        int k2 = uText.indexOf("END");
        if (!(k1>=0 && k2>k1 && (k0<0 || k0<k1))) {
            throw new InvalidQueryException("Invalid query format: "+ getExecSql());
        }
        Map<String, SqlDataType> inpFields = new HashMap<>();
        Map<String, SqlDataType> outFields = new HashMap<>();
        fields.forEach((k,v) -> {
            if (getOutput()!=null && getOutput().getFields().contains(k)) {
                outFields.put(k, v);
            } else {
                inpFields.put(k,v);
            }
        });
        return Tools.replacePlaceholder(nText, inpFields, outFields);
    }
}
