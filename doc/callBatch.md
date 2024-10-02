## 4.4.4. CallBatch

The CallBatch template is designed to queue call of new items and send them to the database in bulk when the queue reaches the preset threshold.

### Standard use

Template example

~~~yaml
  - methodName: book
    perform: !CallBatch
      input:              # optional
        reflect: false    # optional, default false
        delegate: false   # optional, default false
      timeout: 5          # (seconds) optional, default null (system default)
      batchSize: 1024     # optional, default 1024, threshold for execute
      execSql: |
        call book(:abi, :posi, :circ, :qty)
~~~

Generated DAO method signature (body omitted):

~~~java
    public static <I extends BookPS> Book<I> newBook(
            final Connection c)
            throws SQLException ;
~~~

Example of client code:

~~~java
        try (DaoU01.Book<XBook> bookQueue = DaoU01.newBook(c)) {
            while(...) {
                XBook book = ...
                bookeQueue.lazyCall(book);     // execute call on threshold
            }
        }   // call on close
~~~

it is possible to force the execution of the calls before reaching the threshold using

~~~java
                bookQueue.flush();     // force execute call
~~~

it is possible to define a trigger method that is called after sending call requests to the database with the result returned by the `.executeBatch()` function

~~~java
            bookQueue.setTrigger(this::count);  // check executeBatch() result
~~~

For linked queue see note on [insert](insertBatch.md#cascade).

[![Up](go-up.png)](ConfigYaml.md) [![Next](go-previous.png)](inlineProc.md) [![Next](go-next.png)](inlineBatch.md)
