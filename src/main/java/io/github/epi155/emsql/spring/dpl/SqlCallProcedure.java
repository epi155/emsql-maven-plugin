package io.github.epi155.emsql.spring.dpl;

import io.github.epi155.emsql.api.*;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.dpl.ApiCall;
import io.github.epi155.emsql.commons.dpl.ApiWrite;
import io.github.epi155.emsql.commons.dpl.DelegateCall;
import io.github.epi155.emsql.spring.SpringAction;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

public class SqlCallProcedure extends SpringAction
        implements ApiWrite, ApiCall, CallProcedureModel {
    private final DelegateWrite delegateWrite;
    private final DelegateCall delegateCall;
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
        this.delegateWrite = new DelegateWrite(this);
        this.delegateCall = new DelegateCall(this);
    }


    @Override
    public JdbcStatement sql(Map<String, SqlDataType> fields) throws InvalidQueryException {
        return delegateCall.proceed(fields);
    }

    public void writeMethod(PrintModel ipw, String name, JdbcStatement jdbc, String kPrg) {
        delegateWrite.proceed(ipw, name, jdbc, kPrg);
    }

}
