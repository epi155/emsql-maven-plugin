## 4.4.1. CallProcedure

The CallProcedure template is designed to call a stored procedure.
Since it is not possible to determine from the context whether a parameter is input or output, it is necessary to specify the output parameters, all other parameters will be considered input parameters.
Parameters that are both input parameters and output parameters are not managed.

### Standard use

Template example

~~~yaml
  - methodName: book
    perform: !CallProcedure
      output:
        fields:
          - nmBook
      execSql: |
        call book(:nmBook, :abi, :posi, :circ, :qty) 
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
        int booked = DaoP01.book(c, bookInp);
~~~

[![Up](go-up.png)](ConfigYaml.md) [![Next](go-previous.png)](insertKey.md) [![Next](go-next.png)](inlineProc.md)