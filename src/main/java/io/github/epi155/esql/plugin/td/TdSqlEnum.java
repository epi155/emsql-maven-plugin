package io.github.epi155.esql.plugin.td;

import io.github.epi155.esql.plugin.sql.SqlEnum;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class TdSqlEnum extends TypeDescription {
    public TdSqlEnum() {
        super(SqlEnum.class);
    }

    public Object newInstance(Node node) {
        if (node instanceof ScalarNode) {
            ScalarNode sNode = (ScalarNode) node;
            String value = sNode.getValue().toUpperCase();
            switch (value) {
                case "BOOL":
                case "BOOLEAN":
                    return SqlEnum.BooleanStd;
                case "BOOL?":
                case "BOOLEAN?":
                    return SqlEnum.BooleanNil;
                case "INT":
                case "INTEGER":
                    return SqlEnum.IntegerStd;
                case "INT?":
                case "INTEGER?":
                    return SqlEnum.IntegerNil;
                case "SHORT":
                case "SMALLINT":
                    return SqlEnum.ShortStd;
                case "SHORT?":
                case "SMALLINT?":
                    return SqlEnum.ShortNil;
                case "BIGINT":
                case "BIGINTEGER":
                case "LONG":
                case "BIGSERIAL":
                    return SqlEnum.LongStd;
                case "BIGINT?":
                case "BIGINTEGER?":
                case "LONG?":
                    return SqlEnum.LongNil;
                case "DOUBLE":
                    return SqlEnum.DoubleStd;
                case "DOUBLE?":
                    return SqlEnum.DoubleNil;
                case "FLOAT":
                    return SqlEnum.FloatStd;
                case "FLOAT?":
                    return SqlEnum.FloatNil;

                case "NUMERIC":
                case "NUMBER":
                    return SqlEnum.NumericStd;
                case "NUMERIC?":
                case "NUMBER?":
                    return SqlEnum.NumericNil;
                case "DECIMAL":
                    return SqlEnum.NumericStd;
                case "DECIMAL?":
                    return SqlEnum.NumericNil;

                case "VARCHAR":
                    return SqlEnum.VarCharStd;
                case "VARCHAR?":
                    return SqlEnum.VarCharNil;
                case "CHAR":
                    return SqlEnum.CharStd;
                case "CHAR?":
                    return SqlEnum.CharNil;

                case "DATE":
                    return SqlEnum.DateStd;
                case "DATE?":
                    return SqlEnum.DateNil;
                case "TIMESTAMP":
                    return SqlEnum.TimestampStd;
                case "TIMESTAMP?":
                    return SqlEnum.TimestampNil;
                case "TIME":
                    return SqlEnum.TimeStd;
                case "TIME?":
                    return SqlEnum.TimeNil;

                case "LOCALDATE":
                    return SqlEnum.LocalDateStd;
                case "LOCALDATE?":
                    return SqlEnum.LocalDateNil;
                case "LOCALDATETIME":
                    return SqlEnum.LocalDateTimeStd;
                case "LOCALDATETIME?":
                    return SqlEnum.LocalDateTimeNil;
                case "LOCALTIME":
                    return SqlEnum.LocalTimeStd;
                case "LOCALTIME?":
                    return SqlEnum.LocalTimeNil;
                default:
                    return null;
            }
        }
        return null;
    }
}
