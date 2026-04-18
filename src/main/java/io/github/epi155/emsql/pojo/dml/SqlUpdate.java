package io.github.epi155.emsql.pojo.dml;

import io.github.epi155.emsql.api.InvalidQueryException;
import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import io.github.epi155.emsql.api.UpdateModel;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.dml.ApiUpdate;
import io.github.epi155.emsql.commons.dml.ApiWriteMethod;
import io.github.epi155.emsql.commons.dml.DelegateUpdate;
import io.github.epi155.emsql.pojo.PojoAction;

import java.util.Map;

public class SqlUpdate extends PojoAction implements ApiWriteMethod, ApiUpdate, UpdateModel {
    private final DelegateWriteMethod delegateWriteMethod;
    private final DelegateUpdate delegateUpdate;

    public SqlUpdate() {
        super();
        this.delegateWriteMethod = new DelegateWriteMethod(this);
        this.delegateUpdate = new DelegateUpdate(this);
    }

    @Override
    public JdbcStatement sql(Map<String, SqlDataType> fields) throws InvalidQueryException {
        return delegateUpdate.proceed(fields, true);
    }

    @Override
    public void writeMethod(PrintModel ipw, String name, JdbcStatement jdbc, String kPrg) throws InvalidQueryException {
        delegateWriteMethod.proceed(ipw, name, jdbc, kPrg);
    }
}
