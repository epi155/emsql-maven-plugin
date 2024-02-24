package io.github.epi155.emsql.pojo;

import io.github.epi155.emsql.api.*;
import io.github.epi155.emsql.plugin.MapContext;
import io.github.epi155.emsql.pojo.dml.*;
import io.github.epi155.emsql.pojo.dpl.SqlCallProcedure;
import io.github.epi155.emsql.pojo.dpl.SqlInlineProcedure;
import io.github.epi155.emsql.pojo.dql.SqlCursorForSelect;
import io.github.epi155.emsql.pojo.dql.SqlSelectList;
import io.github.epi155.emsql.pojo.dql.SqlSelectOptional;
import io.github.epi155.emsql.pojo.dql.SqlSelectSingle;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.maven.plugin.MojoExecutionException;

import java.util.*;

import static io.github.epi155.emsql.pojo.Tools.cc;

@Slf4j
public class PojoFactory implements CodeFactory {
    private static final Map<String,SqlDataType> sqlMap = Map.ofEntries(
            new AbstractMap.SimpleEntry<>("BOOL", SqlEnum.BooleanStd),
            new AbstractMap.SimpleEntry<>("BOOLEAN", SqlEnum.BooleanStd),
            new AbstractMap.SimpleEntry<>("BOOL?", SqlEnum.BooleanNil),
            new AbstractMap.SimpleEntry<>("BOOLEAN?", SqlEnum.BooleanNil),

            new AbstractMap.SimpleEntry<>("SHORT", SqlEnum.ShortStd),
            new AbstractMap.SimpleEntry<>("SMALLINT", SqlEnum.ShortStd),
            new AbstractMap.SimpleEntry<>("SHORT?", SqlEnum.ShortNil),
            new AbstractMap.SimpleEntry<>("SMALLINT?", SqlEnum.ShortNil),

            new AbstractMap.SimpleEntry<>("INT", SqlEnum.IntegerStd),
            new AbstractMap.SimpleEntry<>("INTEGER", SqlEnum.IntegerStd),
            new AbstractMap.SimpleEntry<>("INT?", SqlEnum.IntegerNil),
            new AbstractMap.SimpleEntry<>("INTEGER?", SqlEnum.IntegerNil),

            new AbstractMap.SimpleEntry<>("BIGINT", SqlEnum.LongStd),
            new AbstractMap.SimpleEntry<>("BIGINTEGER", SqlEnum.LongStd),
            new AbstractMap.SimpleEntry<>("LONG", SqlEnum.LongStd),
            new AbstractMap.SimpleEntry<>("BIGSERIAL", SqlEnum.LongStd),
            new AbstractMap.SimpleEntry<>("BIGINT?", SqlEnum.LongNil),
            new AbstractMap.SimpleEntry<>("BIGINTEGER?", SqlEnum.LongNil),
            new AbstractMap.SimpleEntry<>("LONG?", SqlEnum.LongNil),
            new AbstractMap.SimpleEntry<>("BIGSERIAL?", SqlEnum.LongNil),

            new AbstractMap.SimpleEntry<>("NUMERIC", SqlEnum.NumericStd),
            new AbstractMap.SimpleEntry<>("NUMBER", SqlEnum.NumericStd),
            new AbstractMap.SimpleEntry<>("DECIMAL", SqlEnum.NumericStd),
            new AbstractMap.SimpleEntry<>("NUMERIC?", SqlEnum.NumericNil),
            new AbstractMap.SimpleEntry<>("NUMBER?", SqlEnum.NumericNil),
            new AbstractMap.SimpleEntry<>("DECIMAL?", SqlEnum.NumericNil),

            new AbstractMap.SimpleEntry<>("DOUBLE", SqlEnum.DoubleStd),
            new AbstractMap.SimpleEntry<>("DOUBLE?", SqlEnum.DoubleNil),

            new AbstractMap.SimpleEntry<>("FLOAT", SqlEnum.FloatStd),
            new AbstractMap.SimpleEntry<>("FLOAT?", SqlEnum.FloatNil),

            new AbstractMap.SimpleEntry<>("VARCHAR", SqlEnum.VarCharStd),
            new AbstractMap.SimpleEntry<>("VARCHAR?", SqlEnum.VarCharNil),

            new AbstractMap.SimpleEntry<>("CHAR", SqlEnum.CharStd),
            new AbstractMap.SimpleEntry<>("CHAR?", SqlEnum.CharNil),

            new AbstractMap.SimpleEntry<>("DATE", SqlEnum.DateStd),
            new AbstractMap.SimpleEntry<>("DATE?", SqlEnum.DateNil),

            new AbstractMap.SimpleEntry<>("TIMESTAMP", SqlEnum.TimestampStd),
            new AbstractMap.SimpleEntry<>("TIMESTAMP?", SqlEnum.TimestampNil),

            new AbstractMap.SimpleEntry<>("TIME", SqlEnum.TimeStd),
            new AbstractMap.SimpleEntry<>("TIME?", SqlEnum.TimeNil),

            new AbstractMap.SimpleEntry<>("LOCALDATE", SqlEnum.LocalDateStd),
            new AbstractMap.SimpleEntry<>("LOCALDATE?", SqlEnum.LocalDateNil),

            new AbstractMap.SimpleEntry<>("LOCALDATETIME", SqlEnum.LocalDateTimeStd),
            new AbstractMap.SimpleEntry<>("LOCALDATETIME?", SqlEnum.LocalDateTimeNil),

            new AbstractMap.SimpleEntry<>("LOCALTIME", SqlEnum.LocalTimeStd),
            new AbstractMap.SimpleEntry<>("LOCALTIME?", SqlEnum.LocalTimeNil)

            //---------------------------------------------------------
//            new AbstractMap.SimpleEntry<>("(CHAR)", new SqlVector(SqlEnum.CharStd))

    );

    @Override
    public MethodModel newMethodModel() {
        return new SqlMethod();
    }

    @Override
    public InputModel newInputModel() {
        return new ComAreaStd();
    }

    @Override
    public OutputModel newOutputModel() {
        return new ComAreaStd();
    }

    @Override
    public OutFieldsModel newOutFieldsModel() {
        return new ComAreaDef();
    }

    @Override
    public SelectSingleModel newSelectSingleModel() {
        return new SqlSelectSingle();
    }

    @Override
    public SelectOptionalModel newSelectOptionalModel() {
        return new SqlSelectOptional();
    }

    @Override
    public SelectListModel newSelectListModel() {
        return new SqlSelectList();
    }

    @Override
    public CursorForSelectModel newCursorForSelectModel() {
        return new SqlCursorForSelect();
    }

    @Override
    public DeleteModel newDeleteModel() {
        return new SqlDelete();
    }

    @Override
    public InsertModel newInsertModel() {
        return new SqlInsert();
    }

    @Override
    public UpdateModel newUpdateModel() {
        return new SqlUpdate();
    }

    @Override
    public DeleteBatchModel newDeleteBatchModel() {
        return new SqlDeleteBatch();
    }

    @Override
    public InsertBatchModel newInsertBatchModel() {
        return new SqlInsertBatch();
    }

    @Override
    public UpdateBatchModel newUpdateBatchModel() {
        return new SqlUpdateBatch();
    }

    @Override
    public InsertReturnGeneratedKeysModel newInsertReturnGeneratedKeysModel() {
        return new SqlInsertReturnGeneratedKeys();
    }

    @Override
    public CallProcedureModel newCallProcedureModel() {
        return new SqlCallProcedure();
    }

    @Override
    public InlineProcedureModel newInlineProcedureModel() {
        return new SqlInlineProcedure();
    }

    @Override
    public void createClass(PrintModel pw, String className, List<MethodModel> methods, Map<String, TypeModel> declare) throws MojoExecutionException {
        Set<String> basket = new HashSet<>();
        classBegin(pw, className, cc.isDebug());
        int kMethod = 0;
        for(val method: methods) {
            String methodName = method.getMethodName();
            log.info("- method {} ...", methodName);
            if (basket.contains(methodName)) {
                log.warn("Duplicate method name {}, skipped", methodName);
            } else {
                pw.println();
                /*------------------------------------*/
                method.writeCode(pw, ++kMethod);
                /*------------------------------------*/
                basket.add(methodName);
                cc.incMethods();
            }
        }
        cc.flush(pw);
        pw.ends(); // close class

        cc.writeImport(pw);
        pw.println();

    }

    @Override
    public SqlDataType getInstance(String value, MapContext mapContext) {
        SqlDataType kind = sqlMap.get(value.toUpperCase());
        if (kind!=null) {
            return kind;
        }
        if (value.startsWith("(") && value.endsWith(")")) {
            value = value.substring(1, value.length()-1);
            val names = value.split(",");
            List<SqlParam> list = new ArrayList<>();
            for(String name: names) {
                name = name.trim();
                kind = (SqlDataType) mapContext.get(name);
                if (kind == null)
                    throw new IllegalArgumentException("Undefined field <"+name+">");
                if (kind.isNullable())
                    throw new IllegalArgumentException("Nullable field <"+value+">");
                if (!kind.isScalar())
                    throw new IllegalArgumentException("Not scalar field <"+value+">");
                list.add(new SqlParam(name, kind));
            }
            if (list.isEmpty())
                throw new IllegalArgumentException("Invalid for list fields <"+value+">");
            SqlParam[] params = list.toArray(new SqlParam[0]);
            return new SqlVector(params);
        }
        throw new IllegalArgumentException("Unknown SQL type <"+value+">");
    }

    @Override
    public void classContext(PluginContext pc, Map<String, TypeModel> declare) {
        cc = new ClassContext(pc, declare);
    }

    private void classBegin(PrintModel pw, String className, boolean isDebug) {
        pw.printf("public class %s {%n", className);
        pw.more();
        if (isDebug) {
            pw.printf("private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(%s.class);%n", className);
        }
        pw.printf("private %s() {}%n", className);
    }
}
