## 4.2.5. SelectListDyn


In interactive applications, it's possible to have queries with filters that can be activated at the user's discretion.
This leads to query proliferation.
If I have 4 optional parameters, I have to prepare 2^4=16 queries.
Very similar queries with the risk of errors and code duplication.

This template allows you to define optional parameters that can be activated at runtime.
This way, you only need to define one query.
The actual query is constructed at runtime based on the optional parameters provided.

The method generated in this case does not return the list, but a builder that allows you to set optional parameters
and finally get the list.

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

`optionalAnd` is defined as `Map`,
the value used as key will be used as method name to set corresponding parameters in builder

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


[![Up](go-up.png)](ConfigYaml.md) [![Next](go-previous.png)](CursorForSelect.md) [![Next](go-next.png)](insert.md)