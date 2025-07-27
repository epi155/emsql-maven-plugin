package io.github.epi155.emsql.pojo.dpl;

import io.github.epi155.emsql.api.*;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.dpl.ApiCall;
import io.github.epi155.emsql.commons.dpl.ApiWrite;
import io.github.epi155.emsql.commons.dpl.DelegateCall;
import io.github.epi155.emsql.pojo.PojoAction;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

public class SqlCallProcedure extends PojoAction
        implements ApiWrite, ApiCall, CallProcedureModel {
    private final DelegateCall delegateCall;
    private final DelegateWrite delegatewriteMethod;
    @Setter
    @Getter
    private InputModel input;
    @Setter
    @Getter
    private OutFieldsModel output;
    @Setter
    @Getter
    private InOutFieldsModel inputOutput;

    public SqlCallProcedure() {
        super();
        this.delegatewriteMethod = new DelegateWrite(this);
        this.delegateCall = new DelegateCall(this);
    }

    @Override
    public JdbcStatement sql(Map<String, SqlDataType> fields) throws InvalidQueryException {
        return delegateCall.proceed(fields);
    }

    public void writeMethod(PrintModel ipw, String name, JdbcStatement jdbc, String kPrg) {
        delegatewriteMethod.proceed(ipw, name, jdbc, kPrg);
    }

}
