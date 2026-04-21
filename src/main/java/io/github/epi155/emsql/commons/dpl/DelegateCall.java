package io.github.epi155.emsql.commons.dpl;

import io.github.epi155.emsql.api.InvalidQueryException;
import io.github.epi155.emsql.api.SqlDataType;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.Tools;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DelegateCall {
    private static final String CALL_TEMPLATE =
            "^CALL (\\w+)\\((.*)\\)$";
    private static final Pattern regx = Pattern.compile(CALL_TEMPLATE, Pattern.CASE_INSENSITIVE);
    private final ApiCall api;

    public DelegateCall(ApiCall api) {
        this.api = api;
    }

    public JdbcStatement proceed(Map<String, SqlDataType> fields) throws InvalidQueryException {
        String nText = Tools.oneLine(api.getExecSql());
        Matcher m = regx.matcher(nText);
        if (m.find()) {
            Map<String, SqlDataType> inpFields = new HashMap<>();
            Map<String, SqlDataType> outFields = new HashMap<>();
            Map<String, SqlDataType> ioFields = new HashMap<>();

            fields.forEach((k, v) -> {
                if (api.getInOutFields() != null && api.getInOutFields().contains(k)) {
                    ioFields.put(k, v);
                } else if (api.getOutFields() != null && api.getOutFields().contains(k)) {
                    outFields.put(k, v);
                } else {
                    inpFields.put(k, v);
                }
            });
            return Tools.replacePlaceholder(nText, inpFields, outFields, ioFields);

        } else {
            throw new InvalidQueryException("Invalid query format: " + api.getExecSql());
        }
    }
}
