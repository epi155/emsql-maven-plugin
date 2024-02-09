package io.github.epi155.emsql.plugin.sql.dml;

import io.github.epi155.emsql.plugin.ClassContext;
import io.github.epi155.emsql.plugin.ComAreaStd;
import io.github.epi155.emsql.plugin.IndentPrintWriter;
import io.github.epi155.emsql.plugin.sql.JdbcStatement;
import io.github.epi155.emsql.plugin.sql.SqlAction;
import io.github.epi155.emsql.plugin.sql.SqlKind;
import lombok.Getter;
import lombok.Setter;
import org.apache.maven.plugin.MojoExecutionException;

import java.util.Map;

public class SqlInsert extends SqlAction implements ApiWriteMethod, ApiInsert {
    private final DelegateWriteMethod delegateWriteMethod;
    private final DelegateInsert delegateInsert;
    @Setter
    @Getter
    private ComAreaStd input;

    SqlInsert() {
        super();
        this.delegateWriteMethod = new DelegateWriteMethod(this);
        this.delegateInsert = new DelegateInsert(this);
    }

    @Override
    public JdbcStatement sql(Map<String, SqlKind> fields) throws MojoExecutionException {
        return delegateInsert.proceed(fields);
    }

    @Override
    public void writeMethod(IndentPrintWriter ipw, String name, JdbcStatement jdbc, String kPrg, ClassContext cc) {
        delegateWriteMethod.proceed(ipw, name, jdbc, kPrg, cc);
    }
}
