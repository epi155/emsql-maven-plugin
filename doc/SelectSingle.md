## SelectSingle 


Template example

~~~yaml
methods:
  - methodName: MyMethod
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
