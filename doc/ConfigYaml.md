## 4. Configuration YAML details

~~~yaml
packageName:  # String
className:    # String
methods:      # List<SqlMethod>
  - methodName:   # String
    perform:      # SqlAction
      timeout:        # Integer (seconds)
      reflect:        # boolean
      delegate:       # boolean
      # fields depending on SqlAction template
~~~

In the default configuration the plugin, in addition to the DAO that executes 
the query, generates the input interface with getters and the output interface 
with setters.

If the input values are up to 3 they are passed directly as arguments to the 
created DAO method. The input interface is not created.

If a single value is returned in the output, the value is returned directly, 
otherwise the output interface is created.

A first limitation of DTO interfaces is that they have a flat structure.
if the output of the query is the fields of class A, I can have the fields of 
the class set for free if I can set A implements DaoOutput, but if I can't add 
the interface implementation, using DTO interfaces is not of much help. 
A similar situation occurs for the input class.

If the reflect flag is activated, DTO interfaces are not generated. Input and 
output class fields are handled using java reflection.
In this way it is also possible to access structured objects (:foo.bar becomes 
getFoo().getBar() or getFoo().setBar() depending on whether it is an input or 
output), in the case of output parameters, intermediate nodes are also created 
if they are null.

Although reflect reduces the code you need to write, it is not recommended.
A first contraindication is that any errors only appear during code execution, 
obviously accessing the fields indirectly with java reflecion is less 
efficient (slower) than accessing them directly,

The delegate flag replaces the creation of DTO interfaces with direct getters 
and setters, with DTO classes with indirect getters and setters.

The getters of the delegated DAO class do not return the field values, but the 
pointers of the getters to call to retrieve the field values.
The setters of the generated DAO class do not contain the values of the fields, 
but pointers to the setters to use to set the fields.

In addition to the delegated DTO class, a builder is generated to facilitate 
setting the getter and setter pointers