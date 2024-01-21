## <a id="44">4.4. SelectSingle</a>


Template example

~~~yaml
methods:
  - methodName: myMethod
    perform: !SelectSingle
      inFields:       # optional, input parameters
        dsPrd: VarChar
      outFields:      # required, output parameters
        idPrd: Int
        idCat: Int?
        idSco: Int?
        idUni: Int
      timeout: 0      # (seconds) optional, default null (system default)
      reflect: false  # optional, default false
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

Generated DAO code (metod body omitted)

~~~java
    public static <R extends MyMethodResponse> R myMethod(
            Connection c,
            String dsPrd,
            Supplier<R> so)
            throws SQLException ;

~~~
