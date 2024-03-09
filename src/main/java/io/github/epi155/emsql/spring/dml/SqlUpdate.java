package io.github.epi155.emsql.spring.dml;

import io.github.epi155.emsql.api.*;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.dml.ApiUpdate;
import io.github.epi155.emsql.commons.dml.ApiWriteMethod;
import io.github.epi155.emsql.commons.dml.DelegateUpdate;
import io.github.epi155.emsql.spring.SpringAction;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

public class SqlUpdate extends SpringAction implements ApiWriteMethod, ApiUpdate, UpdateModel {
    private final DelegateWriteMethod delegateWriteMethod;
    private final DelegateUpdate delegateUpdate;
    @Setter
    @Getter
    private InputModel input;

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
    public void writeMethod(PrintModel ipw, String name, JdbcStatement jdbc, String kPrg) {
        delegateWriteMethod.proceed(ipw, name, jdbc, kPrg);
    }
}
