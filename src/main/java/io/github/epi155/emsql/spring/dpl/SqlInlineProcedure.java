package io.github.epi155.emsql.spring.dpl;

import io.github.epi155.emsql.api.InlineProcedureModel;
import io.github.epi155.emsql.api.InvalidQueryException;
import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.dpl.ApiInline;
import io.github.epi155.emsql.commons.dpl.ApiWrite;
import io.github.epi155.emsql.commons.dpl.DelegateInline;
import io.github.epi155.emsql.spring.SpringAction;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

public class SqlInlineProcedure extends SpringAction
        implements ApiWrite, ApiInline, InlineProcedureModel {
    private final DelegateWrite delegateWrite;
    private final DelegateInline delegateInline;
    @Setter
    @Getter
    private List<String> outFields;

    public SqlInlineProcedure() {
        super();
        this.delegateWrite = new DelegateWrite(this);
        this.delegateInline = new DelegateInline(this);
    }

    @Override
    public JdbcStatement sql(Map<String, SqlDataType> fields) throws InvalidQueryException {
        return delegateInline.proceed(fields);
    }

    public void writeMethod(PrintModel ipw, String name, JdbcStatement jdbc, String kPrg) throws InvalidQueryException {
        delegateWrite.proceed(ipw, name, jdbc, kPrg);
    }
}
