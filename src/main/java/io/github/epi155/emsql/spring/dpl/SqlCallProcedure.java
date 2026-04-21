package io.github.epi155.emsql.spring.dpl;

import io.github.epi155.emsql.api.CallProcedureModel;
import io.github.epi155.emsql.api.InvalidQueryException;
import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.dpl.ApiCall;
import io.github.epi155.emsql.commons.dpl.ApiWrite;
import io.github.epi155.emsql.commons.dpl.DelegateCall;
import io.github.epi155.emsql.spring.SpringAction;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

public class SqlCallProcedure extends SpringAction
        implements ApiWrite, ApiCall, CallProcedureModel {
    private final DelegateWrite delegateWrite;
    private final DelegateCall delegateCall;
    @Setter
    @Getter
    private List<String> outFields;
    @Setter
    @Getter
    private List<String> inOutFields;

    public SqlCallProcedure() {
        super();
        this.delegateWrite = new DelegateWrite(this);
        this.delegateCall = new DelegateCall(this);
    }


    @Override
    public JdbcStatement sql(Map<String, SqlDataType> fields) throws InvalidQueryException {
        return delegateCall.proceed(fields);
    }

    public void writeMethod(PrintModel ipw, String name, JdbcStatement jdbc, String kPrg) throws InvalidQueryException {
        delegateWrite.proceed(ipw, name, jdbc, kPrg);
    }

}
