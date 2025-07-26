## 4.2.3. SelectListDyn


in applicazioni interattive
può capitare di avere query con dei
filtri attivabili a discrezione dell'utente.
Questo porta a una proliferazione delle query.
Se ho 4 parametri opzionali devo preparare 2^4=16 query.
Query molto simili con rischi di errore.

questo modello permette di definire parametri
opzionali che possono essere attivati a runtime.
In questo modo è sufficiente definire 1 query.
La query che viene eseguita è costruita a runtime
in base ai parametri di esecuzione della query.

### Standard use

Template example

~~~yaml
  - methodName: certificateList
    perform: !SelectListDyn
      input:              # optional
        reflect: false    # optional, default false
        delegate: false   # optional, default false
      output:             # optional
        reflect: false    # optional, default false
      timeout: 5          # (seconds) optional, default null (system default)
      fetchSize: 2048     # optional, default null (system default)
      execSql: |
        select
          A.K_FINGERPRINT, C.K_FINGERPRINT_ISSUER, A.CNAME, A.CORG, A.CUNIT,
          C.NOT_BEFORE, C.NOT_AFTER, A.KEY_SIZE, A.KEY_ALGO, A.C_TYPE,
          C.SERIAL, C.CD_REVOKE, C.TS_REVOKE, C.USAGE_PAD, C.VERS,
          B.CNAME, B.CORG, B.CUNIT, 
          C.ULTIMATE, C.R_COUNT, C.C_FINGERPRINT
        into
          :fingerprint, :fingerprintIssuer, :commonName, :organization, :orgUnit,
          :notBefore, :notAfter, :keySize, :keyAlgo, :cType,
          :serial, :cdRevoke, :tsRevoke, :usagePad, :vers,
          :issuer.commonName, :issuer.organization, :issuer.orgUnit, 
          :ultimate, :count, :cFingerprint
        from C01_CERT_BASE A
        join C05_CERT_BIND C on C.K_FINGERPRINT = A.K_FINGERPRINT
        join C01_CERT_BASE B on B.K_FINGERPRINT = C.K_FINGERPRINT_ISSUER
        where A.C_TYPE = :cType
        order by C.NOT_AFTER asc, C.NOT_BEFORE desc, A.CNAME asc, A.K_FINGERPRINT asc , C.SERIAL
      optionalAnd:
        cnLike: A.CNAME LIKE :commonName
        isNotRevoked: C.CD_REVOKE is null
        isRevoked: C.CD_REVOKE is not null
        isNotExpired: C.NOT_BEFORE <= :now and C.NOT_AFTER >= :now
        isExpired: (:now < C.NOT_BEFORE or :now > C.NOT_AFTER)
        isRenewed: C.ULTIMATE = 0
        isNotRenewed: C.ULTIMATE = 1
~~~

Generated DAO Builder method signature (body omitted):

~~~java
    public static <O extends CertificateListRS> CertificateListBuilder<O> certificateList(
            final Connection c,
            final int cType,
            final Supplier<O> so);
~~~

Example of client code:

~~~java
        List<DtoCertificateList> certs = DaoU01.certificateList(c, 1, DtoCertificateList::new)
                                            .isNotExpired(LocalDate.now())
                                            .isNotRevoked()
                                            .cnLike("%JOHN%")
                                            .list();
~~~


[![Up](go-up.png)](ConfigYaml.md) [![Next](go-previous.png)](SelectOptional.md) [![Next](go-next.png)](CursorForSelect.md)