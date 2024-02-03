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

Generated DAO method signature (body omitted):

~~~java
    public static <I extends SaveUsersPS> ESqlInsertBatch<I> newSaveUsers(
            final Connection c)
            throws SQLException ;
~~~

Example of client code:

~~~java
        try (ESqlInsertBatch<XUser> saveQueue = DaoU01.newSaveUsers(c)) {
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
