## 4. Configuration YAML details

The configuration file starts with the definition of the package name and the class name

~~~yaml
packageName: com.example    # String
className: DaoFoo           # String
...
~~~

follows the declaration of the input and output fields which will then be used in the methods

~~~yaml
...
declare:      # input/output fields
  fieldName1: fieldTYpe     # Map<String, SqlEnum> NOT NULL field
  fieldName2: fieldTYpe?    # Map<String, SqlEnum> NULLABLE field
  ...
...
~~~

`fieldName` are case-sensitive, `fieldType` are case-insensitive, the question
mark after the data type indicates that the field is nullable in the database
(kotlin style).

The currently managed field types are shown in the following table:

| Field Type|JDBC Type|java type|
|-----------|---------|---------|
|BOOL<br>BOOLEAN    | BOOLEAN | boolean |
|BOOL?<br>BOOLEAN?  | BOOLEAN | Boolean |
|SHORT<br>SMALLINT  |SMALLINT | short   |
|SHORT?<br>SMALLINT?|SMALLINT | Short   |
|INT<br>INTEGER     | INTEGER | int     |
|INT?<br>INTEGER?   | INTEGER | Integer |
|BIGINT<br>BIGINTEGER<br>LONG    | BIGINT | long
|BIGINT?<br>BIGINTEGER?<br>LONG? | BIGINT | Long
|DOUBLE | DOUBLE | double |
|DOUBLE?| DOUBLE | Double |
|FLOAT  | FLOAT  | float  |
|FLOAT? | FLOAT  | Float  |
|NUMERIC<br>NUMBER  | NUMERIC | BigDecimal |
|NUMERIC?<br>NUMBER?| NUMERIC | BigDecimal |
|VARCHAR       | VARCHAR   | String        |
|VARCHAR?      | VARCHAR   | String        |
|CHAR          | CHAR      | String        |
|CHAR?         | CHAR      | String        |
|DATE          | DATE      | Date          |
|DATE?         | DATE      | Date          |
|TIMESTAMP     | TIMESTAMP | Timestamp     |
|TIMESTAMP?    | TIMESTAMP | Timestamp     |
|TIME          | TIME      | Time          |
|TIME?         | TIME      | Time          |
|LOCALDATE     | DATE      | LocalDate     |
|LOCALDATE?    | DATE      | LocalDate     |
|LOCALDATETIME | TIMESTAMP | LocalDateTime |
|LOCALDATETIME?| TIMESTAMP | LocalDateTime |
|LOCALTIME     | TIME      | LocalTime     |
|LOCALTIME?    | TIME      | LocalTime     |

The list of fields is not used to create a single common class for all methods, but is consulted, for each method, to create dedicated interfaces (with getters for the input values, and with setters for the values of output).

If up to 3 parameters are required in input, these are passed directly as method arguments; if more than 3 parameters are required, a wrapper interface is created.

Similarly, if only 1 value is returned in the output, it is returned directly from the method, otherwise a wrapper interface is created.

After the declaration of the fields, the list of methods with their parameters follows

~~~yaml
...
methods:
  - methodName: selectFoo   # String
    perform: !SelectSingle  # SqlAction
      ...
~~~

Naturally `methodName` is the name of the method, `perform` is one of the models developed to manage possible accesses to the database.

For model details refer to:

4.1) Query<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[4.1.1) SelectSingle](SelectSingle.md)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[4.1.2) SelectOptional](SelectOptional.md)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[4.1.3) SelectList](SelectList.md)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[4.1.4) CursorForSelect](CursorForSelect.md)<br/>
4.2) Manipulation<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[4.2.1) Insert](insert.md)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[4.2.2) Update](update.md)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[4.2.3) Delete](delete.md)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[4.2.4) InsertBatch](insertBatch.md)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[4.2.5) UpdateBatch](updateBatch.md)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[4.2.6) DeleteBatch](deleteBatch.md)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[4.2.7) InsertReturnKeys](insertKey.md)<br/>
4.3) Procedure<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[4.3.1) CallProcedure](#58)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[4.2.2) InlineProcedure](#58)<br/>



all models have the parameters in common: `timeout` (optional) the time, in seconds, to wait for the query to be executed and `execSql` the query to execute:

~~~yaml
    ...
    perform: !SelectSingle
      timeout: 5          # (seconds) optional, default null (system default)
      ...
      execSql: |
        select current_date into :today
        from dual
~~~

each model has its own dedicated set of parameters.

### Reflect Input/Output values

In general the `reflecŧ` flag is present in the input and output parameters

~~~yaml
  - methodName:   # String
    perform:      # SqlAction
      input:
        reflect:        # boolean
      output:
        reflect:        # boolean
~~~

If the reflect flag is activated, DTO interfaces are not generated. Input and
output class fields are handled using java reflection.
In this way it is also possible to access structured objects (`:foo.bar` becomes
`getFoo().getBar()` or `getFoo().setBar()` depending on whether it is an input or
output); in the case of output parameters, intermediate nodes are also created
if they are null; in the case of input parameters the nodes are traversed in
safe-call mode, as in kotlin `foo?.bar`, if `getFoo()` returns `null`,
it does not attempt to call `getBar()` causing a NullPointer, but returns `null` as a value

Although reflect reduces the code you need to write, it should be handled with care.
A first contraindication is that any errors only appear during code execution,
obviously accessing the fields indirectly with java reflecion is less
efficient (slower) than accessing them directly,

There are no examples using reflect in the template examples.
The code is identical to the standard one with generics that do not extend any interfaces.

### Delegate Input/Output values

In general the `delegate` flag is present in the input and output parameters

~~~yaml
  - methodName:   # String
    perform:      # SqlAction
      input:
        delegate:       # boolean
      output:
        delegate:       # boolean
~~~

If the delegate flag is enabled, DTO interfaces with direct getters and setters are replaced with DTO classes with references to getter and setter methods. The corresponding builder is also created together with the DTO classes to simplify the setting of the values references

The concept of delegated parameter is a bit convoluted, in the details of the various models there is an example of the use of delegated parameters in input and output.

