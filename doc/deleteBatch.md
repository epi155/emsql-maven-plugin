## 4.2.6. DeleteBatch

The DeleteBatch template is designed to queue deletions of items from the table and send them in bulk to the database when the queue reaches the preset threshold.

### Standard use

Template example

~~~yaml
  - methodName: deleteUsers
    perform: !DeleteBatch
      input:              # optional
        reflect: false    # optional, default false
        delegate: false   # optional, default false
      timeout: 5          # (seconds) optional, default null (system default)
      batchSize: 1024     # optional, default 1024, threshold for execute
      execSql: |
        delete from u01_user
        where SURNAME     = :surname
          and GIVEN_NAME  = :givenName
          and BIRTH_DATE  = :birthDate
          and BIRTH_PLACE = :birthPlace
          and BIRTH_STATE = :birthState
~~~

Generated DAO method signature (body omitted):

~~~java
    public static <I extends DeleteUsersPS> DeleteUsers<I> newDeleteUsers(
            final Connection c)
            throws SQLException {
~~~

Example of client code:

~~~java
        try (DaoU01.DeleteUsers<XUser> deleteQueue = DaoU01.newDeleteUsers(c)) {
            while(...) {
                XUser user = ...
                deleteQueue.lazyDelete(user);     // execute delete on threshold
            }
        }   // delete on close
~~~

it is possible to force the execution of the deletes before reaching the threshold using

~~~java
                deleteQueue.flush();     // force execute delete
~~~

it is possible to define a trigger method that is called after sending delete requests to the database with the result returned by the `.executeBatch()` function

~~~java
            deleteQueue.setTrigger(this::count);  // check executeBatch() result
~~~

[![Up](go-up.png)](ConfigYaml.md) [![Next](go-previous.png)](updateBatch.md) [![Next](go-next.png)](insertKey.md)