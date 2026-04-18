package io.github.epi155.emsql.spring.dml;

import io.github.epi155.emsql.api.DeleteModel;
import io.github.epi155.emsql.api.InvalidQueryException;
import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.dml.ApiDelete;
import io.github.epi155.emsql.commons.dml.ApiWriteMethod;
import io.github.epi155.emsql.commons.dml.DelegateDelete;
import io.github.epi155.emsql.spring.SpringAction;

import java.util.Map;

public class SqlDelete extends SpringAction implements ApiWriteMethod, ApiDelete, DeleteModel {
    private final DelegateWriteMethod delegateWriteMethod;
    private final DelegateDelete delegateDelete;


    public SqlDelete() {
        super();
        this.delegateWriteMethod = new DelegateWriteMethod(this);
        this.delegateDelete = new DelegateDelete(this);
    }

    @Override
    public JdbcStatement sql(Map<String, SqlDataType> fields) throws InvalidQueryException {
        return delegateDelete.proceed(fields, true);
    }

    public void writeMethod(PrintModel ipw, String name, JdbcStatement jdbc, String kPrg) throws InvalidQueryException {
        delegateWriteMethod.proceed(ipw, name, jdbc, kPrg);
    }

}
