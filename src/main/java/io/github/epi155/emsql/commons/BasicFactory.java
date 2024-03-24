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
    private static final Map<String, SqlDataType> sqlMap;
    static {
        Map<String, SqlDataType> map = new HashMap<>();
        map.put("BOOL", SqlEnum.BooleanStd);
        map.put("BOOLEAN", SqlEnum.BooleanStd);
        map.put("BOOL?", SqlEnum.BooleanNil);
        map.put("BOOLEAN?", SqlEnum.BooleanNil);

        map.put("NUMBOOL", SqlEnum.NumBoolStd);
        map.put("NUMBOOL?", SqlEnum.NumBoolNil);

        map.put("BYTE", SqlEnum.ByteStd);
        map.put("BYTE?", SqlEnum.ByteNil);

        map.put("SHORT", SqlEnum.ShortStd);
        map.put("SMALLINT", SqlEnum.ShortStd);
        map.put("SHORT?", SqlEnum.ShortNil);
        map.put("SMALLINT?", SqlEnum.ShortNil);

        map.put("INT", SqlEnum.IntegerStd);
        map.put("INTEGER", SqlEnum.IntegerStd);
        map.put("INT?", SqlEnum.IntegerNil);
        map.put("INTEGER?", SqlEnum.IntegerNil);

        map.put("BIGINT", SqlEnum.LongStd);
        map.put("BIGINTEGER", SqlEnum.LongStd);
        map.put("LONG", SqlEnum.LongStd);
        map.put("BIGSERIAL", SqlEnum.LongStd);
        map.put("BIGINT?", SqlEnum.LongNil);
        map.put("BIGINTEGER?", SqlEnum.LongNil);
        map.put("LONG?", SqlEnum.LongNil);

        map.put("NUMERIC", SqlEnum.NumericStd);
        map.put("NUMBER", SqlEnum.NumericStd);
        map.put("DECIMAL", SqlEnum.NumericStd);
        map.put("NUMERIC?", SqlEnum.NumericNil);
        map.put("NUMBER?", SqlEnum.NumericNil);
        map.put("DECIMAL?", SqlEnum.NumericNil);

        map.put("DOUBLE", SqlEnum.DoubleStd);
        map.put("DOUBLE?", SqlEnum.DoubleNil);

        map.put("FLOAT", SqlEnum.FloatStd);
        map.put("FLOAT?", SqlEnum.FloatNil);

        map.put("VARCHAR", SqlEnum.VarCharStd);
        map.put("VARCHAR?", SqlEnum.VarCharNil);

        map.put("CHAR", SqlEnum.CharStd);
        map.put("CHAR?", SqlEnum.CharNil);

        map.put("DATE", SqlEnum.DateStd);
        map.put("DATE?", SqlEnum.DateNil);

        map.put("TIMESTAMP", SqlEnum.TimestampStd);
        map.put("TIMESTAMP?", SqlEnum.TimestampNil);

        map.put("TIME", SqlEnum.TimeStd);
        map.put("TIME?", SqlEnum.TimeNil);

        map.put("BINARY", SqlEnum.BinaryStd);
        map.put("BINARY?", SqlEnum.BinaryNil);

        map.put("VARBINARY", SqlEnum.VarBinaryStd);
        map.put("VARBINARY?", SqlEnum.VarBinaryNil);

        map.put("LOCALDATE", SqlEnum.LocalDateStd);
        map.put("LOCALDATE?", SqlEnum.LocalDateNil);

        map.put("LOCALDATETIME", SqlEnum.LocalDateTimeStd);
        map.put("LOCALDATETIME?", SqlEnum.LocalDateTimeNil);

        map.put("LOCALTIME", SqlEnum.LocalTimeStd);
        map.put("LOCALTIME?", SqlEnum.LocalTimeNil);

        map.put("NVARCHAR", SqlEnum.NVarCharStd);
        map.put("NVARCHAR?", SqlEnum.NVarCharNil);
        map.put("NCHAR", SqlEnum.NCharStd);
        map.put("NCHAR?", SqlEnum.NCharNil);

        map.put("LONGVARBINARY", SqlEnum.LongVarBinaryStd);
        map.put("LONGVARBINARY?", SqlEnum.LongVarBinaryNil);
        map.put("LONGVARCHAR", SqlEnum.LongVarCharStd);
        map.put("LONGVARCHAR?", SqlEnum.LongVarCharNil);
        map.put("LONGNVARCHAR", SqlEnum.LongNVarCharStd);
        map.put("LONGNVARCHAR?", SqlEnum.LongNVarCharNil);

        map.put("BLOB", SqlEnum.BlobStd);
        map.put("BLOB?", SqlEnum.BlobNil);
        map.put("CLOB", SqlEnum.ClobStd);
        map.put("CLOB?", SqlEnum.ClobNil);
        map.put("NCLOB", SqlEnum.NClobStd);
        map.put("NCLOB?", SqlEnum.NClobNil);

        map.put("LONGVARCHARSTREAM", SqlEnum.LongVarCharStreamStd);
        map.put("LONGVARCHARSTREAM?", SqlEnum.LongVarCharStreamNil);
        map.put("LONGNVARCHARSTREAM", SqlEnum.LongNVarCharStreamStd);
        map.put("LONGNVARCHARSTREAM?", SqlEnum.LongNVarCharStreamNil);
        map.put("LONGVARBINARYSTREAM", SqlEnum.LongVarBinaryStreamStd);
        map.put("LONGVARBINARYSTREAM?", SqlEnum.LongVarBinaryStreamNil);

        map.put("BLOBSTREAM", SqlEnum.BlobStreamStd);
        map.put("BLOBSTREAM?", SqlEnum.BlobStreamNil);
        map.put("CLOBSTREAM", SqlEnum.ClobStreamStd);
        map.put("CLOBSTREAM?", SqlEnum.ClobStreamNil);
        map.put("NCLOBSTREAM", SqlEnum.NClobStreamStd);
        map.put("NCLOBSTREAM?", SqlEnum.NClobStreamNil);

        map.put("REF", SqlEnum.RefStd);
        map.put("REF?", SqlEnum.RefNil);
        map.put("ROWID", SqlEnum.RowIdStd);
        map.put("ROWID?", SqlEnum.RowIdNil);
        map.put("XML", SqlEnum.SQLXMLStd);
        map.put("XML?", SqlEnum.SQLXMLNil);
        map.put("URL", SqlEnum.URLStd);
        map.put("URL?", SqlEnum.URLNil);
        map.put("ARRAY", SqlEnum.ArrayStd);
        map.put("ARRAY?", SqlEnum.ArrayNil);

        //---------------------------------------------------------
//            new AbstractMap.SimpleEntry<>("(CHAR)", new SqlVector(SqlEnum.CharStd))
        sqlMap = Collections.unmodifiableMap(map);
    }

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
    public InOutFieldsModel newInOutFieldsModel() {
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
