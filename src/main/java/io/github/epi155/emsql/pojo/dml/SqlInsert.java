package io.github.epi155.emsql.pojo.dml;

import io.github.epi155.emsql.api.InputModel;
import io.github.epi155.emsql.api.InsertModel;
import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import io.github.epi155.emsql.pojo.JdbcStatement;
import io.github.epi155.emsql.pojo.SqlAction;
import lombok.Getter;
import lombok.Setter;
import org.apache.maven.plugin.MojoExecutionException;

import java.util.Map;

public class SqlInsert extends SqlAction implements ApiWriteMethod, ApiInsert, InsertModel {
    private final DelegateWriteMethod delegateWriteMethod;
    private final DelegateInsert delegateInsert;
    @Setter
    @Getter
    private InputModel input;

    public SqlInsert() {
        super();
        this.delegateWriteMethod = new DelegateWriteMethod(this);
        this.delegateInsert = new DelegateInsert(this);
    }

    @Override
    public JdbcStatement sql(Map<String, SqlDataType> fields) throws MojoExecutionException {
        return delegateInsert.proceed(fields);
    }

    @Override
    public void writeMethod(PrintModel ipw, String name, JdbcStatement jdbc, String kPrg) {
        delegateWriteMethod.proceed(ipw, name, jdbc, kPrg);
    }
}
