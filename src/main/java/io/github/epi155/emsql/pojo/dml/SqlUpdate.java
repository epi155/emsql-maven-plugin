package io.github.epi155.emsql.pojo.dml;

import io.github.epi155.emsql.api.InputModel;
import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import io.github.epi155.emsql.api.UpdateModel;
import io.github.epi155.emsql.pojo.JdbcStatement;
import io.github.epi155.emsql.pojo.SqlAction;
import lombok.Getter;
import lombok.Setter;
import org.apache.maven.plugin.MojoExecutionException;

import java.util.Map;

public class SqlUpdate extends SqlAction implements ApiWriteMethod, ApiUpdate, UpdateModel {
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
    public JdbcStatement sql(Map<String, SqlDataType> fields) throws MojoExecutionException {
        return delegateUpdate.proceed(fields);
    }

    @Override
    public void writeMethod(PrintModel ipw, String name, JdbcStatement jdbc, String kPrg) {
        delegateWriteMethod.proceed(ipw, name, jdbc, kPrg);
    }
}
