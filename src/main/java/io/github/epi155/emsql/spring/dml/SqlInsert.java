package io.github.epi155.emsql.spring.dml;

import io.github.epi155.emsql.api.*;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.dml.ApiInsert;
import io.github.epi155.emsql.commons.dml.ApiWriteMethod;
import io.github.epi155.emsql.commons.dml.DelegateInsert;
import io.github.epi155.emsql.spring.SpringAction;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

public class SqlInsert extends SpringAction implements ApiWriteMethod, ApiInsert, InsertModel {
    private final DelegateWriteMethod delegateWriteMethod;
    private final DelegateInsert delegateInsert;
    @Setter
    @Getter
    private InputModel input;

    public SqlInsert() {
        super();
        this.delegateWriteMethod = new DelegateWriteMethod(this);
        this.delegateInsert = new DelegateInsert(this);
    }

    @Override
    public JdbcStatement sql(Map<String, SqlDataType> fields) throws InvalidQueryException {
        return delegateInsert.proceed(fields, true);
    }

    @Override
    public void writeMethod(PrintModel ipw, String name, JdbcStatement jdbc, String kPrg) {
        delegateWriteMethod.proceed(ipw, name, jdbc, kPrg);
    }
}
