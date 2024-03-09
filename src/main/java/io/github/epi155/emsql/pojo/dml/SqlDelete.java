package io.github.epi155.emsql.pojo.dml;

import io.github.epi155.emsql.api.*;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.dml.ApiDelete;
import io.github.epi155.emsql.commons.dml.ApiWriteMethod;
import io.github.epi155.emsql.commons.dml.DelegateDelete;
import io.github.epi155.emsql.pojo.PojoAction;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

public class SqlDelete extends PojoAction implements ApiWriteMethod, ApiDelete, DeleteModel {
    private final DelegateWriteMethod delegateWriteMethod;
    private final DelegateDelete delegateDelete;
    @Setter
    @Getter
    private InputModel input;


    public SqlDelete() {
        super();
        this.delegateWriteMethod = new DelegateWriteMethod(this);
        this.delegateDelete = new DelegateDelete(this);
    }
    @Override
    public JdbcStatement sql(Map<String, SqlDataType> fields) throws InvalidQueryException {
        return delegateDelete.proceed(fields, true);
    }
    public void writeMethod(PrintModel ipw, String name, JdbcStatement jdbc, String kPrg) {
        delegateWriteMethod.proceed(ipw, name, jdbc, kPrg);
    }

}
