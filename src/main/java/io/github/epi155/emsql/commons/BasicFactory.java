package io.github.epi155.emsql.commons;

import io.github.epi155.emsql.api.*;
import io.github.epi155.emsql.types.*;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.PrintWriter;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.epi155.emsql.commons.Contexts.cc;

@Slf4j
public abstract class BasicFactory implements CodeFactory {
    private static final Map<String, SqlDataType> sqlMap;

    static {
        Map<String, SqlDataType> map = new HashMap<>();
        map.put("BOOL", BooleanStdType.INSTANCE);
        map.put("BOOLEAN", BooleanStdType.INSTANCE);
        map.put("BOOL?", BooleanNilType.INSTANCE);
        map.put("BOOLEAN?", BooleanNilType.INSTANCE);

        map.put("NUMBOOL", NumBoolStdType.INSTANCE);
        map.put("NUMBOOL?", NumBoolNilType.INSTANCE);

        map.put("BYTE", ByteStdType.INSTANCE);
        map.put("BYTE?", ByteNilType.INSTANCE);

        map.put("SHORT", ShortStdType.INSTANCE);
        map.put("SMALLINT", ShortStdType.INSTANCE);
        map.put("SHORT?", ShortNilType.INSTANCE);
        map.put("SMALLINT?", ShortNilType.INSTANCE);

        map.put("INT", IntegerStdType.INSTANCE);
        map.put("INTEGER", IntegerStdType.INSTANCE);
        map.put("INT?", IntegerNilType.INSTANCE);
        map.put("INTEGER?", IntegerNilType.INSTANCE);

        map.put("BIGINT", LongStdType.INSTANCE);
        map.put("BIGINTEGER", LongStdType.INSTANCE);
        map.put("LONG", LongStdType.INSTANCE);
        map.put("BIGSERIAL", LongStdType.INSTANCE);
        map.put("BIGINT?", LongNilType.INSTANCE);
        map.put("BIGINTEGER?", LongNilType.INSTANCE);
        map.put("LONG?", LongNilType.INSTANCE);

        map.put("NUMBER", NumberStdType.INSTANCE);
        map.put("DECIMAL", DecimalStdType.INSTANCE);
        map.put("NUMBER?", NumberNilType.INSTANCE);
        map.put("DECIMAL?", DecimalNilType.INSTANCE);

        map.put("DOUBLE", DoubleStdType.INSTANCE);
        map.put("DOUBLE?", DoubleNilType.INSTANCE);

        map.put("FLOAT", FloatStdType.INSTANCE);
        map.put("FLOAT?", FloatNilType.INSTANCE);

        map.put("VARCHAR", VarCharStdType.INSTANCE);
        map.put("VARCHAR?", VarCharNilType.INSTANCE);

        map.put("CHAR", CharStdType.INSTANCE);
        map.put("CHAR?", CharNilType.INSTANCE);

        map.put("DATE", DateStdType.INSTANCE);
        map.put("DATE?", DateNilType.INSTANCE);

        map.put("TIMESTAMP", TimestampStdType.INSTANCE);
        map.put("TIMESTAMP?", TimestampNilType.INSTANCE);

        map.put("TIME", TimeStdType.INSTANCE);
        map.put("TIME?", TimeNilType.INSTANCE);

        map.put("TIMESTAMPZ", TimestampZStdType.INSTANCE);
        map.put("TIMESTAMPZ?", TimestampZNilType.INSTANCE);

        map.put("TIMEZ", TimeZStdType.INSTANCE);
        map.put("TIMEZ?", TimeZNilType.INSTANCE);

        map.put("BINARY", BinaryStdType.INSTANCE);
        map.put("BINARY?", BinaryNilType.INSTANCE);

        map.put("VARBINARY", VarBinaryStdType.INSTANCE);
        map.put("VARBINARY?", VarBinaryNilType.INSTANCE);

        map.put("LOCALDATE", LocalDateStdType.INSTANCE);
        map.put("LDATE", LocalDateStdType.INSTANCE);
        map.put("LOCALDATE?", LocalDateNilType.INSTANCE);
        map.put("LDATE?", LocalDateNilType.INSTANCE);

        map.put("LOCALDATETIME", LocalDateTimeStdType.INSTANCE);
        map.put("LDATETIME", LocalDateTimeStdType.INSTANCE);
        map.put("LOCALDATETIME?", LocalDateTimeNilType.INSTANCE);
        map.put("LDATETIME?", LocalDateTimeNilType.INSTANCE);

        map.put("LOCALTIME", LocalTimeStdType.INSTANCE);
        map.put("LTIME", LocalTimeStdType.INSTANCE);
        map.put("LOCALTIME?", LocalTimeNilType.INSTANCE);
        map.put("LTIME?", LocalTimeNilType.INSTANCE);

        map.put("NVARCHAR", NVarCharStdType.INSTANCE);
        map.put("NVARCHAR?", NVarCharNilType.INSTANCE);
        map.put("NCHAR", NCharStdType.INSTANCE);
        map.put("NCHAR?", NCharNilType.INSTANCE);

        map.put("LONGVARBINARY", LongVarBinaryStdType.INSTANCE);
        map.put("LONGVARBINARY?", LongVarBinaryNilType.INSTANCE);
        map.put("LONGVARCHAR", LongVarCharStdType.INSTANCE);
        map.put("LONGVARCHAR?", LongVarCharNilType.INSTANCE);
        map.put("LONGNVARCHAR", LongNVarCharStdType.INSTANCE);
        map.put("LONGNVARCHAR?", LongNVarCharNilType.INSTANCE);

        map.put("BLOB", BlobStdType.INSTANCE);
        map.put("BLOB?", BlobNilType.INSTANCE);
        map.put("CLOB", ClobStdType.INSTANCE);
        map.put("CLOB?", ClobNilType.INSTANCE);
        map.put("NCLOB", NClobStdType.INSTANCE);
        map.put("NCLOB?", NClobNilType.INSTANCE);

        map.put("LONGVARCHARSTREAM", LongVarCharStreamStdType.INSTANCE);
        map.put("LONGVARCHARSTREAM?", LongVarCharStreamNilType.INSTANCE);
        map.put("LONGNVARCHARSTREAM", LongNVarCharStreamStdType.INSTANCE);
        map.put("LONGNVARCHARSTREAM?", LongNVarCharStreamNilType.INSTANCE);
        map.put("LONGVARBINARYSTREAM", LongVarBinaryStreamStdType.INSTANCE);
        map.put("LONGVARBINARYSTREAM?", LongVarBinaryStreamNilType.INSTANCE);

        map.put("BLOBSTREAM", BlobStreamStdType.INSTANCE);
        map.put("BLOBSTREAM?", BlobStreamNilType.INSTANCE);
        map.put("CLOBSTREAM", ClobStreamStdType.INSTANCE);
        map.put("CLOBSTREAM?", ClobStreamNilType.INSTANCE);
        map.put("NCLOBSTREAM", NClobStreamStdType.INSTANCE);
        map.put("NCLOBSTREAM?", NClobStreamNilType.INSTANCE);

        map.put("REF", RefStdType.INSTANCE);
        map.put("REF?", RefNilType.INSTANCE);
        map.put("ROWID", RowIdStdType.INSTANCE);
        map.put("ROWID?", RowIdNilType.INSTANCE);
        map.put("XML", SQLXMLStdType.INSTANCE);
        map.put("XML?", SQLXMLNilType.INSTANCE);
        map.put("URL", URLStdType.INSTANCE);
        map.put("URL?", URLNilType.INSTANCE);
        map.put("ARRAY", ArrayStdType.INSTANCE);
        map.put("ARRAY?", ArrayNilType.INSTANCE);

        sqlMap = Collections.unmodifiableMap(map);
    }

    private final boolean autoPad;

    protected BasicFactory(boolean autoPad) {
        this.autoPad = autoPad;
    }

    @Override
    public MethodModel newMethodModel() {
        return new SqlMethod();
    }

    @Override
    public FieldsModel newOutFieldsModel() {
        return new ComAreaDef();
    }

    @Override
    public FieldsModel newInOutFieldsModel() {
        return new ComAreaDef();
    }

    private static final Pattern FIX_CHAR = Pattern.compile("CHAR\\s*\\(\\s*(\\d+\\s*)\\)([?])?", Pattern.CASE_INSENSITIVE);

    @Override
    public SqlDataType getInstance(String value, MapContext mapContext) {
        Matcher matcher = FIX_CHAR.matcher(value);
        if (matcher.find()) {
            String idNil = matcher.group(2);
            if (autoPad) {
                int len = Integer.parseInt(matcher.group(1));
                return (idNil == null) ? FixedCharStdType.getInstance(len) :  FixedCharNilType.getInstance(len);
            }
            value = (idNil == null) ? "CHAR" : "CHAR?";
        }
        SqlDataType kind = sqlMap.get(value.toUpperCase());
        if (kind != null) {
            return kind;
        }
        if (value.startsWith("(") && value.endsWith(")")) {
            value = value.substring(1, value.length() - 1);
            val names = value.split(",");
            List<SqlParam> list = new ArrayList<>();
            for (String name : names) {
                name = name.trim();
                kind = (SqlDataType) mapContext.get(name);
                if (kind == null)
                    throw new IllegalArgumentException("Undefined field <" + name + ">");
                if (kind.isNullable())
                    throw new IllegalArgumentException("Nullable field <" + value + ">");
                if (!kind.isScalar())
                    throw new IllegalArgumentException("Not scalar field <" + value + ">");
                list.add(new SqlParam(name, kind));
            }
            if (list.isEmpty())
                throw new IllegalArgumentException("Invalid for list fields <" + value + ">");
            SqlParam[] params = list.toArray(new SqlParam[0]);
            return new SqlVector(params);
        }
        throw new IllegalArgumentException("Unknown SQL type <" + value + ">");
    }

    @Override
    public Consumer<PrintWriter> createClass(PrintModel pw, String className, List<MethodModel> methods, Map<String, TypeModel> declare) throws InvalidQueryException {
        Set<String> basket = new HashSet<>();
        preCheck(methods);
        classBegin(pw, className, cc.isDebug());
        int kMethod = 0;
        for (val method : methods) {
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
        cc.writeInterfaces(pw);
        pw.ends(); // close class

        return (PrintWriter wr) -> {
            cc.writeImport(wr);
            wr.println();
        };

    }

    protected abstract void preCheck(List<MethodModel> methods);

    protected abstract void classBegin(PrintModel pw, String className, boolean debug);
}