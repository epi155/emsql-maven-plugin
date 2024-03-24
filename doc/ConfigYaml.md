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
  fieldName1: fieldTYpe     # Map<String, SqlKind> NOT NULL field
  fieldName2: fieldTYpe?    # Map<String, SqlKind> NULLABLE field
  ...
...
~~~

`fieldName` are case-sensitive, `fieldType` are case-insensitive, the question
mark after the data type indicates that the field is nullable in the database
(kotlin style).

The currently managed field types are shown in the following:<br/>
4.1) Tables<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[4.1.1) String Data Types](typeString.md)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[4.1.2) Numeric Data Types](typeNumber.md)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[4.1.3) Date and Time Data Types](typeDateTime.md)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[4.1.4) Binary Data Types](typeBinary.md)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[4.1.5) Unicode Data Types](typeUnicode.md)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[4.1.6) Other Data Types](typeOther.md)<br/>

The list of fields is not used to create a single common class for all methods, but is consulted, for each method, to create dedicated interfaces (with getters for the input values, and with setters for the values of output).

If up to 4 parameters are required in input, these are passed directly as method arguments; if more than 4 parameters are required, a wrapper interface is created.

Similarly, if only 1 value is returned in the output, it is returned directly from the method, otherwise a wrapper interface is created.

If the field names are structured, i.e. contain the dot character, nested DAO interfaces will be generated.

> It is possible to form a `List` field so that it can be used in an `IN` clause, see the [examples](listField.md)

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

4.2) Query<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[4.2.1) SelectSingle](SelectSingle.md)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[4.2.2) SelectOptional](SelectOptional.md)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[4.2.3) SelectList](SelectList.md)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[4.2.4) CursorForSelect](CursorForSelect.md)<br/>
4.3) Manipulation<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[4.3.1) Insert](insert.md)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[4.3.2) Update](update.md)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[4.3.3) Delete](delete.md)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[4.3.4) InsertBatch](insertBatch.md)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[4.3.5) UpdateBatch](updateBatch.md)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[4.3.6) DeleteBatch](deleteBatch.md)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[4.3.7) InsertReturnKeys](insertKey.md)<br/>
4.4) Procedure<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[4.4.1) CallProcedure](callProc.md)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[4.4.2) InlineProcedure](inlineProc.md)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[4.4.3) CallBatch](callBatch.md)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;[4.4.4) InlineBatch](inlineBatch.md)<br/>



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

[![Up](go-up.png)](../README.md) [![Next](go-previous.png)](plugin.md)