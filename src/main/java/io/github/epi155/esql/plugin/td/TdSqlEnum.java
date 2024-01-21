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
                case "VARCHAR":
                    return SqlEnum.VarCharStd;
                case "VARCHAR?":
                    return SqlEnum.VarCharNil;
                case "DATE":
                    return SqlEnum.DateStd;
                case "DATE?":
                    return SqlEnum.DateNil;
                case "TIMESTAMP":
                    return SqlEnum.TimestampStd;
                case "TIMESTAMP?":
                    return SqlEnum.TimestampNil;
                case "BIGINT":
                case "BIGINTEGER":
                case "BIGSERIAL":
                    return SqlEnum.LongStd;
                case "BIGINT?":
                case "BIGINTEGER?":
                    return SqlEnum.LongNil;
                case "NUMERIC":
                    return SqlEnum.NumericStd;
                case "NUMERIC?":
                    return SqlEnum.NumericNil;
                case "LOCALDATE":
                    return SqlEnum.LocalDateStd;
                case "LOCALDATE?":
                    return SqlEnum.LocalDateNil;
                case "LOCALDATETIME":
                    return SqlEnum.LocalDateTimeStd;
                case "LOCALDATETIME?":
                    return SqlEnum.LocalDateTimeNil;
                default:
                    return null;
            }
        }
        return null;
    }
}
