## 4.2.6. CursorForSelectDyn

The CursorForSelectDyn template is designed for situations where you need to handle many objects one at a time and have optional filter parameters.

### Standard use (Imperative)

Template example

~~~yaml
  - methodName: certificateCursor
    perform: !CursorForSelectDyn
      input:              # optional
        reflect: false    # optional, default false
        delegate: false   # optional, default false
      output:             # optional
        reflect: false    # optional, default false
        delegate: false   # optional, default false
      timeout: 5          # (seconds) optional, default null (system default)
      fetchSize: 2048     # optional, default null (system default)
      mode: IP            # optional, default IP (Imperative), else FP (Functional)
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

Generated DAO method signature (body omitted):

~~~java
    public static <O extends CertificateCursorRS> CertificateCursorBuilder<O> certificateCursor(
            final Connection c,
            final int cType,
            final Supplier<O> so)
            throws SQLException {
        return new CertificateCursorBuilder<>(c,
        cType,
        so);
~~~

The returned object allows you to add optional parameters and loop through the list similar to an iterator.
Example of client code:

~~~java
        try(SqlCursor<DtoCertificate> crs = DaoU01.certificateCursor(c, 1, DtoCertificate::new)
                                        .isNotExpired(LocalDate.now())
                                        .isNotRevoked()
                                        .cnLike("%JOHN%")
                                        .open() ) {
            while (crs.hasNext()) {
                DtoCertificate cert = crs.fetchNext();
                // consume cert ...
            }
        }   // close cursor (rs, ps)
~~~

### Standard use (Functional)

Template example as above with `FP` instead of `IP`.

Generated DAO method signature as above but with `forEach' instead of 'open'

Example of client code:

~~~java
        DaoU01.certificateCursor(c, 1, DtoCertificate::new)
                .isNotExpired(LocalDate.now())
                .isNotRevoked()
                .cnLike("%JOHN%")
                .forEach(cert -> {
                    // consume cert ...
                });
~~~

[![Up](go-up.png)](ConfigYaml.md) [![Next](go-previous.png)](SelectListDyn.md) [![Next](go-next.png)](insert.md)