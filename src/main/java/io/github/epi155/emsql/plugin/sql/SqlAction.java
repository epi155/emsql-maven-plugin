package io.github.epi155.emsql.plugin.sql;

import io.github.epi155.emsql.plugin.ClassContext;
import io.github.epi155.emsql.plugin.ComAttribute;
import io.github.epi155.emsql.plugin.IndentPrintWriter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.val;
import org.apache.maven.plugin.MojoExecutionException;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static io.github.epi155.emsql.plugin.Tools.*;

@NoArgsConstructor
@Setter
public abstract class SqlAction {
    public static final int IMAX = 3;
    public static final String REQUEST = "PS";
    public static final String RESPONSE = "RS";
    @Getter
    private String execSql;
    /** seconds */ private Integer timeout;
    private boolean tune;
    public abstract ComAttribute getInput();
    public ComAttribute getOutput() { return null; }

    public abstract JdbcStatement sql(Map<String, SqlKind> fields) throws MojoExecutionException;

    public abstract void writeMethod(IndentPrintWriter pw, String methodName, JdbcStatement jdbc, String kPrg);

    public void declareInput(IndentPrintWriter ipw, @NotNull JdbcStatement jdbc) {
        if (tune) {
            cc.declareTuner(ipw);
        }
        int nSize = mc.nSize();
        if (1<=nSize && nSize<=IMAX) {
            jdbc.getNMap().forEach((name, type) -> {
                ipw.commaLn();
                if (type.isScalar() || type.columns() <=1) {
                    ipw.printf("        final %s %s", type.getPrimitive(), name);
                } else {
                    cc.add("java.util.List");
                    ipw.printf("        final List<%s> %s", type.getGeneric(), name);
                }
            });
        } else if (nSize>IMAX){
            ipw.commaLn();
            if (mc.isInputDelegate()) {
                ipw.printf("        final DI i");
            } else {
                ipw.printf("        final I i");
            }
        }
    }
    public void declareNewInstance(@NotNull IndentPrintWriter ipw, String eSqlObject, @NotNull JdbcStatement jdbc, String cName) {
        ipw.printf("public static ");
        declareGenerics(ipw, cName, jdbc.getTKeys());
        ipw.putf("%s", eSqlObject);
        plainGenericsNew(ipw, jdbc);
        ipw.putf(" new%s(%n", cName);
        ipw.printf("        final Connection c)%n", cName);
        ipw.printf("        throws SQLException {%n");
    }

    public static void plainGenericsNew(IndentPrintWriter ipw, JdbcStatement jdbc) {
        int nSize = mc.nSize();
        if (nSize == 0) {
            ipw.putf("<Void>");
        } else if (nSize == 1) {
            jdbc.getNMap().forEach((name,type) -> ipw.putf("<%s>", type.getWrapper())); // once
        } else if (nSize <= IMAX) {
            ipw.putf("%d<%s>", nSize, jdbc.getNMap().values().stream().map(SqlKind::getWrapper).collect(Collectors.joining(", ")));
        } else {
            if (mc.isInputDelegate()) {
                ipw.putf("<DI>");
            } else {
                ipw.putf("<I>");
            }
        }
    }

    public void declareReturnNew(@NotNull IndentPrintWriter ipw, String eSqlObject, JdbcStatement jdbc, int batchSize) {
        ipw.printf("return new %s", eSqlObject);
        cc.anonymousGenerics(ipw, jdbc);
        ipw.putf("(ps, %d) {%n", batchSize);
    }

    public void declareInputBatch(IndentPrintWriter ipw, @NotNull JdbcStatement jdbc) {
        int nSize = mc.nSize();
        if (nSize == 0) {
            ipw.printf("        Void nil");     // any use case ??
        } else if (1<=nSize && nSize<= IMAX) {
            AtomicInteger k = new AtomicInteger();
            jdbc.getNMap().forEach((name,type) -> {
                ipw.printf("        %s %s", type.getWrapper(), name);
                if (k.incrementAndGet() < nSize) ipw.commaLn();
            });
        } else {
            if (mc.isInputDelegate()) {
                ipw.printf("        DI i");
            } else {
                ipw.printf("        I i");
            }
        }
    }
    public void declareOutput(IndentPrintWriter ipw) {
        if (mc.oSize() > 1 || mc.isOutoutDelegate()) {
            ipw.commaLn();
            if (mc.isOutoutDelegate()) {
                ipw.printf("        final DO o)%n");
            } else {
                ipw.printf("        final %s<O> so)%n", cc.supplier());
            }
        } else {
            ipw.putf(")%n");
        }
        ipw.printf("        throws SQLException {%n");
    }

    public void declareOutputUse(@NotNull IndentPrintWriter ipw, String type) {
        ipw.commaLn();
        if (mc.oSize() > 1) {
            if (mc.isOutoutDelegate()) {
                ipw.printf("        DO o,%n");
                ipw.printf("        Runnable co)%n");
            } else {
                ipw.printf("        %s<O> so,%n", cc.supplier());
                ipw.printf("        %s<O> co)%n", cc.consumer());
            }
        } else {
            if(mc.isOutoutDelegate()) {
                ipw.printf("        Runnable co)%n");
            } else {
                ipw.printf("        %s<%s> co)%n", cc.consumer(), type);
            }
        }
        ipw.printf("        throws SQLException {%n");
    }
    public void fetch(IndentPrintWriter ipw, @NotNull Map<Integer, SqlParam> oMap) {
        if (oMap.size() > 1) {
            if (mc.isOutoutReflect()) {
                ipw.printf("O o = so.get();%n");
                oMap.forEach((k,s) -> s.pullParameter(ipw, k));
            } else if (mc.isOutoutDelegate()){
                oMap.forEach((k,s) -> s.fetchDelegateParameter(ipw, k));
            } else {
                ipw.printf("O o = so.get();%n");
                oMap.forEach((k,s) -> s.fetchParameter(ipw, k));
            }
        } else {
            oMap.forEach((k,v) -> v.fetchValue(ipw, k));
        }
    }
    public void getOutput(IndentPrintWriter ipw, @NotNull Map<Integer, SqlParam> oMap) {
        if (oMap.size() > 1) {
            ipw.printf("O o = so.get();%n");
//            if (reflect) {
//                oMap.forEach((k,s) -> s.pullParameter(ipw, k, set));
//            } else {
                oMap.forEach((k,s) -> s.getParameter(ipw, k));
//            }
        } else {
            oMap.forEach((k,v) -> v.getValue(ipw, k)); // once
        }
    }

    public void setInput(@NotNull IndentPrintWriter ipw, @NotNull JdbcStatement jdbc) {
        ipw.printf("int ki=0;%n");
        int nSize = mc.nSize();
        Map<Integer, SqlParam> iMap = jdbc.getIMap();
        if (1<=nSize && nSize<= IMAX) {
            iMap.forEach((k,s) -> {
                if (s.getType().isScalar() || s.getType().columns() <= 1) {
                    s.setValue(ipw);
                } else if (mc.isInputReflect()) {
                    s.pushParameter(ipw);
                } else {
                    s.setValue(ipw);
                }
            });
        } else {
            if (mc.isInputReflect()) {
                iMap.forEach((k, s) -> s.pushParameter(ipw));
            } else if (mc.isInputDelegate()) {
                iMap.forEach((k, s) -> s.setDelegateParameter(ipw));
            } else {
                iMap.forEach((k, s) -> s.setParameter(ipw));
            }
        }
    }
    public void registerOut(IndentPrintWriter ipw, @NotNull Map<Integer, SqlParam> oMap) {
        oMap.forEach((k,s) -> s.registerOutParms(ipw, k));
    }

    public void writeRequest(IndentPrintWriter ipw, String cMethodName, @NotNull Map<String, SqlKind> sp) throws MojoExecutionException {
        if (sp.size()<=IMAX) return;
        if (mc.isInputReflect()) return;
        if (mc.isInputDelegate()) {
            List<String> badNames = sp.keySet().stream().filter(it -> it.contains(".")).collect(Collectors.toList());
            if (!badNames.isEmpty()) {
                throw new MojoExecutionException("Invalid names for delegate fields: " + String.join(",", badNames));
            }
        }
        writeRequestInterface(ipw, cMethodName, sp);
    }

    private void writeRequestInterface(IndentPrintWriter ipw, String methodName, Map<String, SqlKind> sp) {
        if (mc.isInputDelegate()) {
            ipw.printf("public static class Delegate%s"+REQUEST, methodName);
            writeRequestGenerics(ipw,sp);
            ipw.printf(" {%n");
            ipw.more();
            ipw.printf("private Delegate%s"+REQUEST+"() {}%n", methodName);
            sp.forEach((name, type) -> {
                String claz = type.getWrapper();
                ipw.printf("protected %s<%s> %s;%n", cc.supplier(), claz, name);
            });
            ipw.printf("public static Builder%s"+REQUEST+" builder() { return new Builder%1$s"+REQUEST+"(); }%n", methodName);
            ipw.printf("public static class Builder%s"+REQUEST, methodName);
            writeRequestGenerics(ipw,sp);
            ipw.printf(" {%n", methodName);
            ipw.more();
            ipw.printf("private Builder%s"+REQUEST+"() {}%n", methodName);
            sp.forEach((name, type) -> {
                String claz = type.getWrapper();
                ipw.printf("private %s<%s> %s;%n", cc.supplier(), claz, name);
            });
            ipw.printf("public Delegate%s"+REQUEST+" build() {%n", methodName);
            ipw.more();
            ipw.printf("Delegate%s"+REQUEST+" result = new Delegate%1$s"+REQUEST+"();%n", methodName);
            cc.delegateRequestFields(ipw, sp);
            ipw.printf("return  result;%n");
            ipw.ends();
            sp.forEach((name, type) -> ipw.printf("public Builder%s"+REQUEST+" %s(%s<%s> %2$s) { this.%2$s = %2$s; return this; }%n",
                    methodName, name, cc.supplier(), type.getWrapper()));
            ipw.ends();
        } else {
            ipw.printf("public interface %s" + REQUEST, methodName);
            writeRequestGenerics(ipw, sp);
            ipw.printf(" {%n");
            Map<String, Map<String, SqlKind>> next = throughGetter(ipw, sp);
            next.forEach((n, np) -> writeRequestInterface(ipw, capitalize(n), np));
        }
        ipw.ends();
    }

    private void writeRequestGenerics(IndentPrintWriter ipw, Map<String, SqlKind> sp) {
        sp.forEach((name, kind) -> {
            if (!kind.isScalar() && kind.columns()>1) {
                ipw.putf("<%s extends %s%s>", kind.getGeneric(), capitalize(name), REQUEST);
            }
        });
    }

    public static Map<String, Map<String, SqlKind>> throughGetter(IndentPrintWriter ipw, Map<String, SqlKind> map) {
        ipw.more();
        Map<String, Map<String, SqlKind>> next = new LinkedHashMap<>();
        map.forEach((name, type) -> {
            int kDot = name.indexOf('.');
            if (kDot < 0) {
                String cName = capitalize(name);
                String claz = type.getPrimitive();
                ipw.printf("%s %s%s();%n", claz, getOf(type), cName);
            } else {
                String ante = name.substring(0, kDot);
                String post = name.substring(kDot + 1);
                Map<String, SqlKind> flds = next.computeIfAbsent(ante, k -> new HashMap<>());
                flds.put(post, type);
            }
        });
        next.keySet().forEach(name -> {
            String cName = capitalize(name);
            String claz = cName+REQUEST;
            ipw.printf("%s get%s();%n", claz, cName);
        });
        return next;
    }

    public void writeResponse(IndentPrintWriter ipw, String cMethodName, @NotNull Collection<SqlParam> sp) throws MojoExecutionException {
        if (sp.size()<=1) return;
        if (mc.isOutoutReflect()) return;
        if (mc.isOutoutDelegate()) {
            List<String> badNames = sp.stream().map(SqlParam::getName).filter(it -> it.contains(".")).collect(Collectors.toList());
            if (!badNames.isEmpty()) {
                throw new MojoExecutionException("Invalid names for delegate fields: " + String.join(",", badNames));
            }
        }
        writeResponseInterface(ipw, cMethodName, sp);
    }

    private void writeResponseInterface(IndentPrintWriter ipw, String cMethodName, Collection<SqlParam> sp) {
        if (mc.isOutoutDelegate()) {
            ipw.printf("public static class Delegate%s"+RESPONSE+" {%n", cMethodName);
            ipw.more();
            sp.forEach(p -> {
                String claz = p.getType().getWrapper();
                ipw.printf("protected %s<%s> %s;%n", cc.consumer(), claz, p.getName());
            });
            ipw.printf("public static Builder%s"+RESPONSE+" builder() { return new Builder%1$s"+RESPONSE+"(); }%n", cMethodName);
            ipw.printf("public static class Builder%s"+RESPONSE+" {%n", cMethodName);
            ipw.more();
            ipw.printf("private Builder%s"+RESPONSE+"() {}%n", cMethodName);
            sp.forEach(p -> {
                String claz = p.getType().getWrapper();
                ipw.printf("private %s<%s> %s;%n", cc.consumer(), claz, p.getName());
            });
            ipw.printf("public Delegate%s"+RESPONSE+" build() {%n", cMethodName);
            ipw.more();
            ipw.printf("Delegate%s"+RESPONSE+" result = new Delegate%1$s"+RESPONSE+"();%n", cMethodName);
            cc.delegateResponseFields(ipw, sp);
            ipw.printf("return  result;%n");
            ipw.ends();
            sp.forEach(p -> ipw.printf("public Builder%s"+RESPONSE+" %s(%s<%s> %2$s) { this.%2$s = %2$s; return this; }%n",
                    cMethodName, p.getName(), cc.consumer(), p.getType().getWrapper()));
            ipw.ends();

        } else {
            ipw.printf("public interface %s"+RESPONSE+" {%n", cMethodName);
            ipw.more();
            Map<String, List<SqlParam>> next = new LinkedHashMap<>();
            for(val p: sp) {
                String name = p.getName();
                int kDot = name.indexOf('.');
                if (kDot < 0) {
                    String cName = capitalize(p.getName());
                    String claz = p.getType().getPrimitive();
                    ipw.printf("void set%s(%s s);%n", cName, claz);
                } else {
                    String ante = name.substring(0, kDot);
                    String post = name.substring(kDot + 1);
                    List<SqlParam> flds = next.computeIfAbsent(ante, k -> new ArrayList<>());
                    flds.add(new SqlParam(post, p.getType()));
                }
            }
            next.keySet().forEach(name -> {
                String cName = capitalize(name);
                String claz = cName+RESPONSE;
                ipw.printf("%s get%s();%n", claz, cName);
            });
            next.forEach((n,np) -> writeResponseInterface(ipw, capitalize(n), np));

        }
        ipw.ends();
    }

    public void docBegin(@NotNull IndentPrintWriter ipw) {
        ipw.printf("/**%n");
        ipw.printf(" * Template %s%n", this.getClass().getSimpleName());
        ipw.printf(" * <pre>%n");
        val lines = execSql.split("\n");
        for(val line: lines) {
            ipw.printf(" * %s%n", line);
        }
        ipw.printf(" * </pre>%n");
        ipw.printf(" *%n");
        ipw.printf(" * @param c connection%n");
        if (tune)
            ipw.printf(" * @param u query hints setting%n");
    }
    public void docInput(IndentPrintWriter ipw, @NotNull JdbcStatement jdbc) {
        int nSize = mc.nSize();
        if (1<=nSize && nSize<=IMAX) {
            AtomicInteger count = new AtomicInteger();
            jdbc.getNMap().forEach((name, type) -> ipw.printf(" * @param %s :%1$s (parameter #%d)%n", name, count.incrementAndGet()));
        } else if (nSize>IMAX) {
            if (mc.isInputDelegate()) {
                ipw.printf(" * @param i input parameter delegate%n");
            } else {
                ipw.printf(" * @param i input parameter wrapper%n");
            }
        }
    }
    public void docOutput(IndentPrintWriter ipw, @NotNull Map<Integer, SqlParam> oMap) {
        if (oMap.size() > 1) {
            ipw.printf(" * @param so output constructor%n");
        }
    }

    public void docOutputUse(IndentPrintWriter ipw, @NotNull Map<Integer, SqlParam> oMap) {
        if (oMap.size() > 1) {
            ipw.printf(" * @param so output constructor%n");
        }
        ipw.printf(" * @param co output consumer%n");
    }

    public void docEnd(@NotNull IndentPrintWriter ipw) {
        ipw.printf(" * @throws SQLException SQL error%n");
        ipw.printf(" */%n");
    }
    public void declareGenerics(IndentPrintWriter ipw, String cName, List<String> inFlds) {
        if (mc.oSize() <= 1) {
            if (mc.nSize() > IMAX) {
                if (mc.isInputReflect()) {
                    ipw.putf("<I");
                    if(!inFlds.isEmpty())
                        genericInner(ipw, inFlds);
                } else if (mc.isInputDelegate()) {
                    ipw.putf("<DI extends Delegate%s" + REQUEST, cName);
                    if(!inFlds.isEmpty())
                        genericInner(ipw, inFlds);
                } else {
                    ipw.putf("<I extends %s" + REQUEST , cName);
                    if(!inFlds.isEmpty())
                        genericInner(ipw, inFlds);
                }
                ipw.putf("> ");
            } else {
                genericIn(ipw, inFlds, "<", "> ");
            }
        } else {
            if (mc.nSize() <= IMAX) {
                if (mc.isOutoutReflect()) {
                    ipw.putf("<O");
                } else if (mc.isOutoutDelegate()){
                    ipw.putf("<DO extends Delegate%s" + RESPONSE, cName);
                } else {
                    ipw.putf("<O extends %s" + RESPONSE, cName);
                }
                genericIn(ipw, inFlds, ",", null);
                ipw.putf("> ");
            } else {
                if (mc.isInputReflect()) {
                    if (mc.isOutoutReflect()) {
                        ipw.putf("<I,O> ");
                    } else if (mc.isOutoutDelegate()) {
                        ipw.putf("<I,DO extends Delegate%1$s" + RESPONSE + "> ", cName);
                    } else {
                        ipw.putf("<I,O extends %1$s" + RESPONSE + "> ", cName);
                    }
                } else if (mc.isInputDelegate()) {
                    if (mc.isOutoutReflect()) {
                        ipw.putf("<DI extends Delegate%1$s"+REQUEST+",O> ", cName);
                    } else if (mc.isOutoutDelegate()) {
                        ipw.putf("<DI extends Delegate%1$s"+REQUEST+",DO extends Delegate%1$s" + RESPONSE + "> ", cName);
                    } else {
                        ipw.putf("<DI extends Delegate%1$s"+REQUEST+",O extends %1$s" + RESPONSE + "> ", cName);
                    }
                } else {
                    if (mc.isOutoutReflect()) {
                        ipw.putf("<I extends %1$s"+REQUEST+",O> ", cName);
                    } else if (mc.isOutoutDelegate()) {
                        ipw.putf("<I extends %1$s"+REQUEST+",DO extends Delegate%1$s" + RESPONSE + "> ", cName);
                    } else {
                        ipw.putf("<I extends %1$s"+REQUEST+",O extends %1$s" + RESPONSE + "> ", cName);
                    }
                }
            }
        }
    }

    private void genericInner(IndentPrintWriter ipw, List<String> flds) {
        if(mc.isInputReflect()) {
            ipw.putf(",");
        } else {
            ipw.putf("<");
        }
        for(int k=1; k<=flds.size(); k++) {
            if (k>1) ipw.putf(",");
            ipw.putf("L%d",k);
        }
        if(!mc.isInputReflect()) {
            ipw.putf(">");
            int k=0;
            for (val fld: flds) {
                ipw.putf(", L%d extends %s%s", ++k, capitalize(fld), REQUEST);
            }
        }
    }

    private void genericIn(IndentPrintWriter ipw, List<String> inFlds, String prefix, String suffix) {
        if (!inFlds.isEmpty()) {
            int k = 0;
            if (prefix!=null) ipw.putf(prefix);
            for (String inFld : inFlds) {
                if (k++ > 0) ipw.putf(",");
                if (mc.isInputReflect()) {
                    ipw.putf("L%d", k);
//                } else if (isDelegate) {
//                    ipw.putf("L%d extends Delegate%s" + REQUEST, k, capitalize(inFld));
                } else {
                    ipw.putf("L%d extends %s" + REQUEST, k, capitalize(inFld));
                }
            }
            if (suffix!=null) ipw.putf(suffix);
        }
    }

    public void debugAction(IndentPrintWriter ipw, String kPrg, JdbcStatement jdbcStatement) {
        if (cc.isDebug()) {
            int nSize = mc.nSize();
            ipw.printf("if (log.isDebugEnabled()) {%n");
            ipw.more();
            ipw.printf("SqlTrace.showQuery(Q_%s, ", kPrg);
            cc.traceParameterBegin(ipw);
            ipw.more();
            ipw.printf("Object[] parms =  new Object[]{%n");
            ipw.more();
            val eol = new Eol(mc.iSize());
            if ( nSize <= IMAX) {
                jdbcStatement.getIMap().forEach((k,v) ->
                        ipw.printf("%s%s%n", v.getName(), eol.nl()));
            } else {
                Map<Integer, SqlParam> iMap = jdbcStatement.getIMap();
                if (mc.isInputReflect()) {
                    iMap.forEach((k,v) ->
                            ipw.printf("EmSQL.get(i, \"%s\", %s.class)%s%n", v.getName(), v.getType().getContainer(), eol.nl()));
                } else if (mc.isInputDelegate()) {
                    iMap.forEach((k,v) ->
                            ipw.printf("i.%s.get()%s%n", v.getName(), eol.nl()));
                } else {
                    iMap.forEach((k,v) ->
                            ipw.printf("i.%s()%s%n", getterOf(v), eol.nl()));
                }
            }
            ipw.less();
            ipw.printf("};%n");
            ipw.printf("return parms;%n");
            cc.traceParameterEnds(ipw);
            ipw.printf("});%n");
            ipw.ends();
        }
    }

    public void setQueryHints(IndentPrintWriter ipw) {
        if (timeout != null) ipw.printf("ps.setQueryTimeout(%d);%n", timeout);
        if (tune) ipw.printf("u.accept(new SqlStmtSetImpl(ps));%n");
    }

    private static class Eol {
        private int count;

        public Eol(int size) {
            this.count = size;
        }

        public String nl() {
            return --count > 0 ? "," : "";
        }
    }

    public Map<Integer, SqlParam> notScalar(@NotNull Map<Integer, SqlParam> parameters) {
        Map<Integer, SqlParam> notScalar = new LinkedHashMap<>();
        parameters.forEach((k,v) -> {
            if (!v.getType().isScalar()) {
                notScalar.put(k,v);
            }
        });
        return notScalar;
    }
    public void expandIn(@NotNull IndentPrintWriter ipw, @NotNull Map<Integer, SqlParam> notScalar, String kPrg) {
        cc.add(ClassContext.RUNTIME_EMSQL);
        ipw.printf("final String query = EmSQL.expandQueryParameters(Q_%s%n", kPrg);
        ipw.more();
        if (mc.nSize()<=IMAX) {
            notScalar.forEach((k,v) -> ipw.printf(", new EmSQL.Mul(%d, %s.size(), %d)%n", k, v.getName(), v.getType().columns()));
        } else {
            if (mc.isInputReflect()) {
                cc.add("java.util.List");
                notScalar.forEach((k,v) -> ipw.printf(", new EmSQL.Mul(%d, EmSQL.get(i, \"%s\", List.class).size(), %d)%n", k, v.getName(), v.getType().columns()));
            } else if (mc.isInputDelegate()) {
                notScalar.forEach((k,v) -> ipw.printf(", new EmSQL.Mul(%d, i.%s.get().size(), %d)%n", k, v.getName(), v.getType().columns()));
            } else {
                notScalar.forEach((k,v) -> ipw.printf(", new EmSQL.Mul(%d, i.%s().size(), %d)%n", k, getterOf(v), v.getType().columns()));
            }
        }
        ipw.less();
        ipw.printf(");%n", kPrg);
    }
    public void openQuery(IndentPrintWriter ipw, JdbcStatement jdbc, String kPrg) {
        Map<Integer, SqlParam> notScalar = notScalar(jdbc.getIMap());
        if (notScalar.isEmpty()) {
            ipw.printf("try (PreparedStatement ps = c.prepareStatement(Q_%s)) {%n", kPrg);
        } else {
            expandIn(ipw, notScalar, kPrg);
            ipw.printf("try (PreparedStatement ps = c.prepareStatement(query)) {%n");
        }

    }
}
