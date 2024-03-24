## 4.3.5. UpdateBatch

The UpdateBatch pattern is designed to queue item updates from the table and send them in bulk to the database when the queue reaches the preset threshold.

### Standard use

Template example

~~~yaml
  - methodName: updateUsers
    perform: !UpdateBatch
      input:              # optional
        reflect: false    # optional, default false
        delegate: false   # optional, default false
      timeout: 5          # (seconds) optional, default null (system default)
      batchSize: 1024     # optional, default 1024, threshold for execute
      execSql: |
      execSql: |
        update u01_user
        set BIRTH_COUNTRY = :birthCountry
          , CITIZENSHIP   = :citizenship
        where SURNAME     = :surname
          and GIVEN_NAME  = :givenName
          and BIRTH_DATE  = :birthDate
          and BIRTH_PLACE = :birthPlace
          and BIRTH_STATE = :birthState
~~~

Generated DAO method signature (body omitted):

~~~java
    public static <I extends UpdateUsersPS> UpdateUsers<I> newUpdateUsers(
            final Connection c)
            throws SQLException {
~~~

Example of client code:

~~~java
        try (DaoU01.UpdateUsers<XUser> updateQueue = DaoU01.newUpdateUsers(c)) {
            while(...) {
                XUser user = ...
                updateQueue.lazyUpdate(user);     // execute update on threshold
            }
        }   // update on close
~~~

it is possible to force the execution of the updates before reaching the threshold using

~~~java
                saveQueue.flush();     // force execute update
~~~

it is possible to define a trigger method that is called after sending delete requests to the database with the result returned by the `.executeBatch()` function

~~~java
            updateQueue.setTrigger(this::count);  // check executeBatch() result
~~~

[![Up](go-up.png)](ConfigYaml.md) [![Next](go-previous.png)](insertBatch.md) [![Next](go-next.png)](deleteBatch.md)