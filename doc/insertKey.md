## 4.2.7. InsertReturnKeys

When the primary key column of a table is defined as IDENTITY, how is it possible to recover the value generated in the insert together with the insert command?

When a PreparedStatement is created it is possible to indicate the RETURN_GENERATED_KEYS flag, with this option it is possible, after executing the insert, to recover the value of the generated key.

[![Up](go-up.png)](ConfigYaml.md) [![Next](go-previous.png)](deleteBatch.md) [![Next](go-next.png)](callProc.md)