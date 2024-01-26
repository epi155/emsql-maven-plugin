package io.github.epi155.esql.plugin.sql;

import io.github.epi155.esql.plugin.ClassContext;
import io.github.epi155.esql.plugin.ComAttribute;
import io.github.epi155.esql.plugin.IndentPrintWriter;
import io.github.epi155.esql.plugin.Tools;
import lombok.Getter;
import lombok.Setter;
import org.apache.maven.plugin.MojoExecutionException;

import java.text.DecimalFormat;
import java.text.NumberFormat;

@Setter
public class SqlMethod {
    @Getter
    private String methodName;
    private SqlAction perform;

    private static final NumberFormat NF = new DecimalFormat("0000");
    public void writeQuery(IndentPrintWriter ipw, int km, ClassContext cc) throws MojoExecutionException {
        JdbcStatement jdbc = perform.sql(cc.getFields());
        String sQuery = jdbc.getText();
        String kPrg = NF.format(km);
        ipw.printf("private static final String Q_%s = \"%s\";%n", kPrg, sQuery);
        /*-------------------------------------------------*/
        perform.writeMethod(ipw, methodName, jdbc, kPrg, cc);
        /*-------------------------------------------------*/

        String cName = Tools.capitalize(methodName);
        perform.writeRequest(ipw, cName, cc, jdbc.getIMap().values());
        perform.writeResponse(ipw, cName, cc, jdbc.getOMap().values());

        ComAttribute ia = perform.getInput();
        ComAttribute oa = perform.getOutput();
        if (ia != null && ia.isReflect() || oa != null && oa.isReflect()) {
            cc.add("io.github.epi155.esql.runtime.ESQL");
        }

        jdbc.getIMap().values().forEach(it -> cc.addAll(it.getType().requires()));
        jdbc.getOMap().values().forEach(it -> cc.addAll(it.getType().requires()));
    }
}
