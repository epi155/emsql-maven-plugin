## <a id="44">4.4. SelectSingle</a>


Template example

~~~yaml
methods:
  - methodName: myMethod
    perform: !SelectSingle
      input:
        reflect: false    # optional, default false
        delegate: false   # optional, default false
        fields:           # optional, input parameters
          dsPrd: VarChar
      output:
        reflect: false    # optional, default false
        delegate: false   # optional, default false
        fields:           # required, output parameters
          idPrd: Int
          idCat: Int?
          idSco: Int?
          idUni: Int
      timeout: 0      # (seconds) optional, default null (system default)
      query: |
        select 
          id_prd,
          id_cat,
          id_sco,
          id_uni
        into
          :idPrd,
          :idCat,
          :idSco,
          :idUni
        from C03_PRODOTTO
        where ds_prd like :dsPrd
~~~

Generated DAO code (method body omitted)

~~~java
    public static <O extends MyMethodResponse> O myMethod(
            Connection c,
            String dsPrd,
            Supplier<O> so)
            throws SQLException ;

~~~
