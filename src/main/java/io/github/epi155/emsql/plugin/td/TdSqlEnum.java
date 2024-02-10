package io.github.epi155.emsql.plugin.td;

import io.github.epi155.emsql.plugin.MapContext;
import io.github.epi155.emsql.plugin.SqlMojo;
import io.github.epi155.emsql.plugin.sql.SqlEnum;
import io.github.epi155.emsql.plugin.sql.SqlKind;
import io.github.epi155.emsql.plugin.sql.SqlParam;
import io.github.epi155.emsql.plugin.xtype.SqlVector;
import lombok.val;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TdSqlEnum extends TypeDescription {
    private static final Map<String,SqlKind> sqlMap = Map.ofEntries(
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
    public TdSqlEnum() {
        super(SqlKind.class);
    }

    @Override
    public Object newInstance(Node node) {
        if (node instanceof ScalarNode) {
            ScalarNode sNode = (ScalarNode) node;
            String value = sNode.getValue();
            SqlKind kind = sqlMap.get(value.toUpperCase());
            if (kind!=null) {
                return kind;
            }
            if (value.startsWith("(") && value.endsWith(")")) {
                value = value.substring(1, value.length()-1);
                val names = value.split(",");
                List<SqlParam> list = new ArrayList<>();
                MapContext mapContext = SqlMojo.mapContext.get();
                for(String name: names) {
                    name = name.trim();
                    kind = mapContext.get(name);
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
        return null;
    }
}
