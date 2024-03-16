## 4.2.4. InsertBatch

The InsertBatch template is designed to queue insertions of new items into the table and send them to the database in bulk when the queue reaches the preset threshold.

### Standard use

Template example

~~~yaml
  - methodName: saveUsers
    perform: !InsertBatch
      input:              # optional
        reflect: false    # optional, default false
        delegate: false   # optional, default false
      timeout: 5          # (seconds) optional, default null (system default)
      batchSize: 1024     # optional, default 1024, threshold for execute
      execSql: |
        insert into u01_user (
          SURNAME,
          GIVEN_NAME,
          BIRTH_DATE,
          BIRTH_PLACE,
          BIRTH_STATE,
          BIRTH_COUNTRY,
          CITIZENSHIP
        ) values (
          :surname,
          :givenName,
          :birthDate,
          :birthPlace,
          :birthState,
          :birthCountry,
          :citizenship
        )
~~~

Generated DAO method signature, more than 4 parameters (body omitted):

~~~java
    public static <I extends SaveUsersPS> SaveUsers<I> newSaveUsers(
            final Connection c)
            throws SQLException ;
~~~

Example of client code:

~~~java
        try (DaoU01.SaveUsers<XUser> saveQueue = DaoU01.newSaveUsers(c)) {
            while(...) {
                XUser user = ...
                saveQueue.lazyInsert(user);     // execute insert on threshold
            }
        }   // insert on close
~~~

it is possible to force the execution of the inserts before reaching the threshold using

~~~java
                saveQueue.flush();     // force execute insert
~~~

it is possible to define a trigger method that is called after sending insert requests to the database with the result returned by the `.executeBatch()` function

~~~java
            saveQueue.setTrigger(this::count);  // check executeBatch() result
~~~

Generated DAO method signature, up to 4 parameters (body omitted):

~~~java
    public static AddUsers newAddUsers(
            final Connection c)
            throws SQLException;
~~~

Example of client code:

~~~java
        try (DaoU01.AddUsers saveQueue = DaoU01.newAddUsers(c)) {
            while(...) {
                XUser user = ...
                saveQueue.lazyInsert(user.getSurname(), user.getGivenName());     // execute insert on threshold
            }
        }   // insert on close
~~~

[![Up](go-up.png)](ConfigYaml.md) [![Next](go-previous.png)](delete.md) [![Next](go-next.png)](updateBatch.md)