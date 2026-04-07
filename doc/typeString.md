## 4.1.1. String Data Types

| Field Type|JDBC Type|java type|
|-----------|---------|---------|
|CHAR               | CHAR        | String |
|CHAR?              | CHAR        | String |
|CHAR(n)            | CHAR        | String |
|CHAR?(n)           | CHAR        | String |
|CLOB               | CLOB        | Clob   |
|CLOB?              | CLOB        | Clob   |
|CLOBSTREAM         | CLOB        | Reader |
|CLOBSTREAM?        | CLOB        | Reader |
|LONGVARCHAR        | LONGVARCHAR | String |
|LONGVARCHAR?       | LONGVARCHAR | String |
|LONGVARCHARSTREAM  | LONGVARCHAR | Reader |
|LONGVARCHARSTREAM? | LONGVARCHAR | Reader |
|VARCHAR            | VARCHAR     | String |
|VARCHAR?           | VARCHAR     | String |

Fields of type `CHAR(n)` and `CHAR?(n)` are handled as `CHAR` and `CHAR?`
if the `autoPad` parameter of the plugin is set to `false`. If the value
of the parameter is `true`, the string values are padded/truncated to the
specified length, `n`, before being passed to the `PreparedStatement`.


[![Up](go-up.png)](ConfigYaml.md) [![Next](go-next.png)](typeNumber.md)