package io.github.epi155.emsql.pojo.dml;

import io.github.epi155.emsql.api.DeleteModel;
import io.github.epi155.emsql.api.InputModel;
import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import io.github.epi155.emsql.pojo.JdbcStatement;
import io.github.epi155.emsql.pojo.SqlAction;
import lombok.Getter;
import lombok.Setter;
import org.apache.maven.plugin.MojoExecutionException;

import java.util.Map;

public class SqlDelete extends SqlAction implements ApiWriteMethod, ApiDelete, DeleteModel {
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
    public JdbcStatement sql(Map<String, SqlDataType> fields) throws MojoExecutionException {
        return delegateDelete.proceed(fields);
    }
    public void writeMethod(PrintModel ipw, String name, JdbcStatement jdbc, String kPrg) {
        delegateWriteMethod.proceed(ipw, name, jdbc, kPrg);
    }

}
