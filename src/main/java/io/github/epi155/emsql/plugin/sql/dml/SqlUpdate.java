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

public class SqlUpdate extends SqlAction implements ApiWriteMethod, ApiUpdate {
    private final DelegateWriteMethod delegateWriteMethod;
    private final DelegateUpdate delegateUpdate;
    @Setter
    @Getter
    private ComAreaStd input;

    SqlUpdate() {
        super();
        this.delegateWriteMethod = new DelegateWriteMethod(this);
        this.delegateUpdate = new DelegateUpdate(this);
    }

    @Override
    public JdbcStatement sql(Map<String, SqlKind> fields) throws MojoExecutionException {
        return delegateUpdate.proceed(fields);
    }

    @Override
    public void writeMethod(IndentPrintWriter ipw, String name, JdbcStatement jdbc, String kPrg, ClassContext cc) {
        delegateWriteMethod.proceed(ipw, name, jdbc, kPrg, cc);
    }
}
