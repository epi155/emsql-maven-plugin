## 4.4.1. CallProcedure

The CallProcedure template is designed to call a stored procedure.
Since it is not possible to determine from the context whether a parameter is input or output, it is necessary to specify the output parameters, all other parameters will be considered input parameters.
Parameters that are both input parameters and output parameters are not managed.

### Standard use

Template example

~~~yaml
  - methodName: book
    perform: !CallProcedure
      input:              # optional
        reflect: false    # optional, default false
        delegate: false   # optional, default false
      output:             # optional
        reflect: false    # optional, default false
        delegate: false   # optional, default false
        fields:           # output fields
          - ...           # field name
      input-output:       # optional
        reflect: false    # optional, default false
        delegate: false   # optional, default false
        fields:           # input-output fields
          - qty           # field name
      timeout: 5          # (seconds) optional, default null (system default)
      execSql: |
        call book(:abi, :posi, :circ, :aff, :cpp, :qty)
~~~

Generated DAO method signature (body omitted):

~~~java
    public static <I extends BookPS> int book(
        final Connection c,
        final I i)
        throws SQLException ;
~~~

Example of client code:

~~~java
        int qty = DaoP01.book(c, bookInp);
~~~

[![Up](go-up.png)](ConfigYaml.md) [![Next](go-previous.png)](insertKey.md) [![Next](go-next.png)](inlineProc.md)