package io.github.epi155.emsql.plugin.sql.dml;

import io.github.epi155.emsql.plugin.ComAreaStd;
import io.github.epi155.emsql.plugin.IndentPrintWriter;
import io.github.epi155.emsql.plugin.sql.JdbcStatement;
import io.github.epi155.emsql.plugin.sql.SqlAction;
import io.github.epi155.emsql.plugin.sql.SqlKind;
import lombok.Getter;
import lombok.Setter;
import org.apache.maven.plugin.MojoExecutionException;

import java.util.Map;

public class SqlDelete extends SqlAction implements ApiWriteMethod, ApiDelete {
    private final DelegateWriteMethod delegateWriteMethod;
    private final DelegateDelete delegateDelete;
    @Setter
    @Getter
    private ComAreaStd input;


    SqlDelete() {
        super();
        this.delegateWriteMethod = new DelegateWriteMethod(this);
        this.delegateDelete = new DelegateDelete(this);
    }
    @Override
    public JdbcStatement sql(Map<String, SqlKind> fields) throws MojoExecutionException {
        return delegateDelete.proceed(fields);
    }
    public void writeMethod(IndentPrintWriter ipw, String name, JdbcStatement jdbc, String kPrg) {
        delegateWriteMethod.proceed(ipw, name, jdbc, kPrg);
    }

}
