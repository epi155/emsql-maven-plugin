## 4.4.3. Command

The Command template is designed to ...

### Standard use

Template example

~~~yaml
  - methodName: lock7
    perform: !Command
      timeout: 5          # (seconds) optional, default null (system default)
      execSql: |
        lock table p07 in exclusive mode

~~~

Generated DAO method signature (body omitted):

~~~java
    public static void lock7(
        final Connection c)
        throws SQLException ;
~~~

Example of client code:

~~~java
        DaoP01.lock7(c);
~~~

[![Up](go-up.png)](ConfigYaml.md) [![Next](go-previous.png)](callProc.md) [![Next](go-next.png)](callBatch.md)