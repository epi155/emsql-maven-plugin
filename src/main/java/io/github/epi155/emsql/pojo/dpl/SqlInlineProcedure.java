package io.github.epi155.emsql.pojo.dpl;

import io.github.epi155.emsql.api.*;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.dpl.ApiInline;
import io.github.epi155.emsql.commons.dpl.ApiWrite;
import io.github.epi155.emsql.commons.dpl.DelegateInline;
import io.github.epi155.emsql.pojo.PojoAction;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

public class SqlInlineProcedure extends PojoAction
        implements ApiWrite, ApiInline, InlineProcedureModel {
    private final DelegateWrite delegateWrite;
    private final DelegateInline delegateInline;
    @Setter
    @Getter
    private InputModel input;
    @Setter
    @Getter
    private OutFieldsModel output;
    @Setter
    @Getter
    private InOutFieldsModel inputOutput;

    public SqlInlineProcedure() {
        super();
        this.delegateWrite = new DelegateWrite(this);
        this.delegateInline = new DelegateInline(this);
    }

    @Override
    public JdbcStatement sql(Map<String, SqlDataType> fields) throws InvalidQueryException {
        return delegateInline.proceed(fields);
    }

    public void writeMethod(PrintModel ipw, String name, JdbcStatement jdbc, String kPrg) {
        delegateWrite.proceed(ipw, name, jdbc, kPrg);
    }
}
