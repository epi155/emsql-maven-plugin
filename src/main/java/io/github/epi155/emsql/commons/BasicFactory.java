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
    private static final Map<String, SqlScalarType> sqlMap;

    static {

        sqlMap = Map.<String, SqlScalarType>ofEntries(    //
                Map.entry("BOOL", BooleanStdType.INSTANCE), //
                Map.entry("BOOLEAN", BooleanStdType.INSTANCE),  //
                Map.entry("BOOL?", BooleanNilType.INSTANCE),    //
                Map.entry("BOOLEAN?", BooleanNilType.INSTANCE), //
                Map.entry("NUMBOOL", NumBoolStdType.INSTANCE),  //
                Map.entry("NUMBOOL?", NumBoolNilType.INSTANCE), //
                // numeric types
                Map.entry("BYTE", ByteStdType.INSTANCE),    //
                Map.entry("BYTE?", ByteNilType.INSTANCE),   //
                Map.entry("SHORT", ShortStdType.INSTANCE),  //
                Map.entry("SMALLINT", ShortStdType.INSTANCE),   //
                Map.entry("SHORT?", ShortNilType.INSTANCE), //
                Map.entry("SMALLINT?", ShortNilType.INSTANCE),  //
                Map.entry("INT", IntegerStdType.INSTANCE),  //
                Map.entry("INTEGER", IntegerStdType.INSTANCE),  //
                Map.entry("INT?", IntegerNilType.INSTANCE), //
                Map.entry("INTEGER?", IntegerNilType.INSTANCE), //
                Map.entry("BIGINT", LongStdType.INSTANCE),  //
                Map.entry("BIGINTEGER", LongStdType.INSTANCE),  //
                Map.entry("LONG", LongStdType.INSTANCE),    //
                Map.entry("BIGSERIAL", LongStdType.INSTANCE),   //
                Map.entry("BIGINT?", LongNilType.INSTANCE), //
                Map.entry("BIGINTEGER?", LongNilType.INSTANCE), //
                Map.entry("LONG?", LongNilType.INSTANCE),   //
                Map.entry("NUMBER", NumberStdType.INSTANCE),    //
                Map.entry("DECIMAL", DecimalStdType.INSTANCE),  //
                Map.entry("NUMBER?", NumberNilType.INSTANCE),   //
                Map.entry("DECIMAL?", DecimalNilType.INSTANCE), //
                Map.entry("DOUBLE", DoubleStdType.INSTANCE),    //
                Map.entry("DOUBLE?", DoubleNilType.INSTANCE),   //
                Map.entry("FLOAT", FloatStdType.INSTANCE),  //
                Map.entry("FLOAT?", FloatNilType.INSTANCE), //
                // date/time types
                Map.entry("DATE", DateStdType.INSTANCE),    //
                Map.entry("DATE?", DateNilType.INSTANCE),   //
                Map.entry("TIMESTAMP", TimestampStdType.INSTANCE),  //
                Map.entry("TIMESTAMP?", TimestampNilType.INSTANCE), //
                Map.entry("TIME", TimeStdType.INSTANCE),    //
                Map.entry("TIME?", TimeNilType.INSTANCE),   //
                Map.entry("TIMESTAMPZ", TimestampZStdType.INSTANCE),    //
                Map.entry("TIMESTAMPZ?", TimestampZNilType.INSTANCE),   //
                Map.entry("TIMEZ", TimeZStdType.INSTANCE),  //
                Map.entry("TIMEZ?", TimeZNilType.INSTANCE), //
                Map.entry("LOCALDATE", LocalDateStdType.INSTANCE),  //
                Map.entry("LDATE", LocalDateStdType.INSTANCE),  //
                Map.entry("LOCALDATE?", LocalDateNilType.INSTANCE), //
                Map.entry("LDATE?", LocalDateNilType.INSTANCE), //
                Map.entry("LOCALDATETIME", LocalDateTimeStdType.INSTANCE),  //
                Map.entry("LDATETIME", LocalDateTimeStdType.INSTANCE),  //
                Map.entry("LOCALDATETIME?", LocalDateTimeNilType.INSTANCE), //
                Map.entry("LDATETIME?", LocalDateTimeNilType.INSTANCE), //
                Map.entry("LOCALTIME", LocalTimeStdType.INSTANCE),  //
                Map.entry("LTIME", LocalTimeStdType.INSTANCE),  //
                Map.entry("LOCALTIME?", LocalTimeNilType.INSTANCE), //
                Map.entry("LTIME?", LocalTimeNilType.INSTANCE), //
                // char types
                Map.entry("CHAR", CharStdType.INSTANCE),    //
                Map.entry("CHAR?", CharNilType.INSTANCE),   //
                Map.entry("VARCHAR", VarCharStdType.INSTANCE),  //
                Map.entry("VARCHAR?", VarCharNilType.INSTANCE), //
                Map.entry("LONGVARCHAR", LongVarCharStdType.INSTANCE),  //
                Map.entry("LONGVARCHAR?", LongVarCharNilType.INSTANCE), //
                Map.entry("CLOB", ClobStdType.INSTANCE),    //
                Map.entry("CLOB?", ClobNilType.INSTANCE),   //
                Map.entry("LONGVARCHARSTREAM", LongVarCharStreamStdType.INSTANCE),  //
                Map.entry("LONGVARCHARSTREAM?", LongVarCharStreamNilType.INSTANCE), //
                Map.entry("CLOBSTREAM", ClobStreamStdType.INSTANCE),    //
                Map.entry("CLOBSTREAM?", ClobStreamNilType.INSTANCE),   //
                // binary types
                Map.entry("BINARY", BinaryStdType.INSTANCE),    //
                Map.entry("BINARY?", BinaryNilType.INSTANCE),   //
                Map.entry("VARBINARY", VarBinaryStdType.INSTANCE),  //
                Map.entry("VARBINARY?", VarBinaryNilType.INSTANCE), //
                Map.entry("LONGVARBINARY", LongVarBinaryStdType.INSTANCE),  //
                Map.entry("LONGVARBINARY?", LongVarBinaryNilType.INSTANCE), //
                Map.entry("BLOB", BlobStdType.INSTANCE),    //
                Map.entry("BLOB?", BlobNilType.INSTANCE),   //
                Map.entry("LONGVARBINARYSTREAM", LongVarBinaryStreamStdType.INSTANCE),  //
                Map.entry("LONGVARBINARYSTREAM?", LongVarBinaryStreamNilType.INSTANCE), //
                Map.entry("BLOBSTREAM", BlobStreamStdType.INSTANCE),    //
                Map.entry("BLOBSTREAM?", BlobStreamNilType.INSTANCE),   //
                // national char types
                Map.entry("NCHAR", NCharStdType.INSTANCE),  //
                Map.entry("NCHAR?", NCharNilType.INSTANCE), //
                Map.entry("NVARCHAR", NVarCharStdType.INSTANCE),    //
                Map.entry("NVARCHAR?", NVarCharNilType.INSTANCE),   //
                Map.entry("LONGNVARCHAR", LongNVarCharStdType.INSTANCE),    //
                Map.entry("LONGNVARCHAR?", LongNVarCharNilType.INSTANCE),   //
                Map.entry("NCLOB", NClobStdType.INSTANCE),  //
                Map.entry("NCLOB?", NClobNilType.INSTANCE), //
                Map.entry("LONGNVARCHARSTREAM", LongNVarCharStreamStdType.INSTANCE),    //
                Map.entry("LONGNVARCHARSTREAM?", LongNVarCharStreamNilType.INSTANCE),   //
                Map.entry("NCLOBSTREAM", NClobStreamStdType.INSTANCE),  //
                Map.entry("NCLOBSTREAM?", NClobStreamNilType.INSTANCE), //
                // other types
                Map.entry("REF", RefStdType.INSTANCE),  //
                Map.entry("REF?", RefNilType.INSTANCE), //
                Map.entry("ROWID", RowIdStdType.INSTANCE),  //
                Map.entry("ROWID?", RowIdNilType.INSTANCE), //
                Map.entry("XML", SQLXMLStdType.INSTANCE),   //
                Map.entry("XML?", SQLXMLNilType.INSTANCE),  //
                Map.entry("URL", URLStdType.INSTANCE),  //
                Map.entry("URL?", URLNilType.INSTANCE), //
                Map.entry("ARRAY", ArrayStdType.INSTANCE),  //
                Map.entry("ARRAY?", ArrayNilType.INSTANCE));    //
    }

    private final boolean autoPad;

    protected BasicFactory(boolean autoPad) {
        this.autoPad = autoPad;
    }

    @Override
    public MethodModel newMethodModel() {
        return new SqlMethod();
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
        SqlScalarType kind = sqlMap.get(value.toUpperCase());
        if (kind != null) {
            return kind;
        }
        if (value.startsWith("(") && value.endsWith(")")) {
            value = value.substring(1, value.length() - 1);
            val names = value.split(",");
            List<SqlParam> list = new ArrayList<>();
            for (String name : names) {
                name = name.trim();
                kind = (SqlScalarType) mapContext.get(name);
                if (kind == null)
                    throw new IllegalArgumentException("Undefined field <" + name + ">");
                if (kind instanceof SqlNullType)
                    throw new IllegalArgumentException("Nullable field <" + value + ">");
                if (kind instanceof SqlVectorType)
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
