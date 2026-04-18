package io.github.epi155.emsql.pojo.dml;

import io.github.epi155.emsql.api.InsertModel;
import io.github.epi155.emsql.api.InvalidQueryException;
import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.dml.ApiInsert;
import io.github.epi155.emsql.commons.dml.ApiWriteMethod;
import io.github.epi155.emsql.commons.dml.DelegateInsert;
import io.github.epi155.emsql.pojo.PojoAction;

import java.util.Map;

public class SqlInsert extends PojoAction implements ApiWriteMethod, ApiInsert, InsertModel {
    private final DelegateWriteMethod delegateWriteMethod;
    private final DelegateInsert delegateInsert;

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
    public void writeMethod(PrintModel ipw, String name, JdbcStatement jdbc, String kPrg) throws InvalidQueryException {
        delegateWriteMethod.proceed(ipw, name, jdbc, kPrg);
    }
}
