package io.github.epi155.emsql.commons;

import io.github.epi155.emsql.api.*;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.PrintWriter;
import java.util.*;
import java.util.function.Consumer;

import static io.github.epi155.emsql.commons.Contexts.cc;

@Slf4j
public abstract class BasicFactory implements CodeFactory {
    private static final Map<String, SqlDataType> sqlMap = Map.ofEntries(
            new AbstractMap.SimpleEntry<>("BOOL", SqlEnum.BooleanStd),
            new AbstractMap.SimpleEntry<>("BOOLEAN", SqlEnum.BooleanStd),
            new AbstractMap.SimpleEntry<>("BOOL?", SqlEnum.BooleanNil),
            new AbstractMap.SimpleEntry<>("BOOLEAN?", SqlEnum.BooleanNil),

            new AbstractMap.SimpleEntry<>("NUMBOOL", SqlEnum.NumBoolStd),
            new AbstractMap.SimpleEntry<>("NUMBOOL?", SqlEnum.NumBoolNil),

            new AbstractMap.SimpleEntry<>("BYTE", SqlEnum.ByteStd),
            new AbstractMap.SimpleEntry<>("BYTE?", SqlEnum.ByteNil),

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
    public Consumer<PrintWriter> createClass(PrintModel pw, String className, List<MethodModel> methods, Map<String, TypeModel> declare) throws InvalidQueryException {
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

        return (PrintWriter wr) -> {
            cc.writeImport(wr);
            wr.println();
        };

    }

    protected abstract void classBegin(PrintModel pw, String className, boolean debug);
}
