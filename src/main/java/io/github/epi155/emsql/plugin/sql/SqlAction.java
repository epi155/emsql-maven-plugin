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

@Setter
@Getter
@NoArgsConstructor
public abstract class SqlAction {
    public static final int IMAX = 3;
    protected static final String REQUEST = "PS";
    protected static final String RESPONSE = "RS";
    private String execSql;
    /** seconds */ private Integer timeout;
    private boolean tune;
    protected abstract ComAttribute getInput();
    protected ComAttribute getOutput() { return null; }

    public abstract JdbcStatement sql(Map<String, SqlEnum> fields) throws MojoExecutionException;

    public abstract void writeMethod(IndentPrintWriter pw, String methodName, JdbcStatement jdbc, String kPrg, ClassContext cc);

    public void declareInput(IndentPrintWriter ipw, @NotNull JdbcStatement jdbc, ClassContext cc) {
        if (tune) {
            cc.declareTuner(ipw);
        }
        int nSize = jdbc.getNameSize();
        if (1<=nSize && nSize<=IMAX) {
            jdbc.getNMap().forEach((name, type) -> {
                ipw.commaLn();
                ipw.printf("        final %s %s", type.getPrimitive(), name);
            });
        } else if (nSize>IMAX){
            ipw.commaLn();
            if (getInput() != null && getInput().isDelegate()) {
                ipw.printf("        final DI i");
            } else {
                ipw.printf("        final I i");
            }
        }
    }
    public void declareNewInstance(IndentPrintWriter ipw, String eSqlObject, JdbcStatement jdbc, String cName) {
        int nSize = jdbc.getNameSize();
        ipw.printf("public static ");
        declareGenerics(ipw, cName, nSize, 1);
        ipw.putf("%s", eSqlObject);
        if (nSize == 0) {
            ipw.putf("<Void>");
        } else if (nSize == 1) {
            jdbc.getNMap().forEach((name,type) -> ipw.putf("<%s>", type.getWrapper())); // once
        } else if (nSize <= IMAX) {
            ipw.putf("%d<%s>", nSize, jdbc.getNMap().values().stream().map(SqlEnum::getWrapper).collect(Collectors.joining(", ")));
        } else {
            if (getInput() != null && getInput().isDelegate()) {
                ipw.putf("<DI>");
            } else {
                ipw.putf("<I>");
            }
        }
        ipw.putf(" new%s(%n", cName);
        ipw.printf("        final Connection c)%n", cName);
        ipw.printf("        throws SQLException {%n");
    }
    public void declareReturnNew(IndentPrintWriter ipw, ClassContext cc, String eSqlObject, JdbcStatement jdbc, int batchSize) {
        ipw.printf("return new %s", eSqlObject);
        cc.anonymousGenerics(ipw, jdbc, getInput());
        ipw.putf("(ps, %d) {%n", batchSize);
    }

    public void declareInputBatch(IndentPrintWriter ipw, JdbcStatement jdbc) {
        int nSize = jdbc.getNameSize();
        if (nSize == 0) {
            ipw.printf("        Void nil");     // any use case ??
        } else if (1<=nSize && nSize<= IMAX) {
            AtomicInteger k = new AtomicInteger();
            jdbc.getNMap().forEach((name,type) -> {
                ipw.printf("        %s %s", type.getWrapper(), name);
                if (k.incrementAndGet() < nSize) ipw.commaLn();
            });
        } else {
            if (getInput() != null && getInput().isDelegate()) {
                ipw.printf("        DI i");
            } else {
                ipw.printf("        I i");
            }
        }
    }
    public void declareOutput(IndentPrintWriter ipw, int oSize, ClassContext cc) {
        boolean isDelegate = getOutput() != null && getOutput().isDelegate();
        if (oSize > 1 || isDelegate) {
            ipw.commaLn();
            if (isDelegate) {
                ipw.printf("        final DO o)%n");
            } else {
                ipw.printf("        final %s<O> so)%n", cc.supplier());
            }
        } else {
            ipw.putf(")%n");
        }
        ipw.printf("        throws SQLException {%n");
    }

    public void declareOutputUse(IndentPrintWriter ipw, int oSize, String type, ClassContext cc) {
        boolean isDelegate = getOutput()!=null && getOutput().isDelegate();
        ipw.commaLn();
        if (oSize > 1) {
            if (isDelegate) {
                ipw.printf("        DO o,%n");
                ipw.printf("        Runnable co)%n");
            } else {
                ipw.printf("        %s<O> so,%n", cc.supplier());
                ipw.printf("        %s<O> co)%n", cc.consumer());
            }
        } else {
            if(isDelegate) {
                ipw.printf("        Runnable co)%n");
            } else {
                ipw.printf("        %s<%s> co)%n", cc.consumer(), type);
            }
        }
        ipw.printf("        throws SQLException {%n");
    }
    public void fetch(IndentPrintWriter ipw, Map<Integer, SqlParam> oMap, ClassContext cc) {
        if (oMap.size() > 1) {
            if (getOutput()!=null && getOutput().isReflect()) {
                ipw.printf("O o = so.get();%n");
                oMap.forEach((k,s) -> s.pullParameter(ipw, k));
            } else if (getOutput()!=null && getOutput().isDelegate()){
                oMap.forEach((k,s) -> s.fetchDelegateParameter(ipw, k, cc));
            } else {
                ipw.printf("O o = so.get();%n");
                oMap.forEach((k,s) -> s.fetchParameter(ipw, k, cc));
            }
        } else {
            oMap.forEach((k,v) -> v.fetchValue(ipw, k, cc));
        }
    }
    public void getOutput(IndentPrintWriter ipw, Map<Integer, SqlParam> oMap, ClassContext cc) {
        if (oMap.size() > 1) {
            ipw.printf("O o = so.get();%n");
//            if (reflect) {
//                oMap.forEach((k,s) -> s.pullParameter(ipw, k, set));
//            } else {
                oMap.forEach((k,s) -> s.getParameter(ipw, k, cc));
//            }
        } else {
            oMap.forEach((k,v) -> v.getValue(ipw, k, cc)); // once
        }
    }

    public void setInput(IndentPrintWriter ipw, JdbcStatement jdbc) {
        int nSize = jdbc.getNameSize();
        Map<Integer, SqlParam> iMap = jdbc.getIMap();
        if (1<=nSize && nSize<= IMAX) {
            iMap.forEach((k,s) -> s.setValue(ipw, k));
        } else {
            boolean isReflect = getInput() != null && getInput().isReflect();
            boolean isDelegate = getInput() != null && getInput().isDelegate();
            if (isReflect) {
                iMap.forEach((k, s) -> s.pushParameter(ipw, k));
            } else if (isDelegate) {
                iMap.forEach((k, s) -> s.setDelegateParameter(ipw, k));
            } else {
                iMap.forEach((k, s) -> s.setParameter(ipw, k));
            }
        }
    }
    public void registerOut(IndentPrintWriter ipw, Map<Integer, SqlParam> oMap) {
        oMap.forEach((k,s) -> s.registerOutParms(ipw, k));
    }

    public void writeRequest(IndentPrintWriter ipw, String cMethodName, ClassContext cc, Map<String, SqlEnum> sp) throws MojoExecutionException {
        if (sp.size()<=IMAX) return;
        if (getInput() != null && getInput().isReflect()) return;
        if (getInput() != null && getInput().isDelegate()) {
            List<String> badNames = sp.keySet().stream().filter(it -> it.contains(".")).collect(Collectors.toList());
            if (!badNames.isEmpty()) {
                throw new MojoExecutionException("Invalid names for delegate fields: " + String.join(",", badNames));
            }
        }
        writeRequestInterface(ipw, cMethodName, cc, sp);
    }

    private void writeRequestInterface(IndentPrintWriter ipw, String methodName, ClassContext cc, Map<String, SqlEnum> sp) {
        if (getInput() != null && getInput().isDelegate()) {
            ipw.printf("public static class Delegate%s"+REQUEST+" {%n", methodName);
            ipw.more();
            ipw.printf("private Delegate%s"+REQUEST+"() {}%n", methodName);
            sp.forEach((name, type) -> {
                String claz = type.getWrapper();
                ipw.printf("protected %s<%s> %s;%n", cc.supplier(), claz, name);
            });
            ipw.printf("public static Builder%s"+REQUEST+" builder() { return new Builder%1$s"+REQUEST+"(); }%n", methodName);
            ipw.printf("public static class Builder%s"+REQUEST+" {%n", methodName);
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
            ipw.printf("public interface %s"+REQUEST+" {%n", methodName);
            ipw.more();
            Map<String, Map<String, SqlEnum>> next = new LinkedHashMap<>();
            sp.forEach((name, type) -> {
                int kDot = name.indexOf('.');
                if (kDot < 0) {
                    String cName = capitalize(name);
                    String claz = type.getPrimitive();
                    ipw.printf("%s %s%s();%n", claz, getOf(type), cName);
                } else {
                    String ante = name.substring(0, kDot);
                    String post = name.substring(kDot + 1);
                    Map<String, SqlEnum> flds = next.computeIfAbsent(ante, k -> new HashMap<>());
                    flds.put(post, type);
                }
            });
            next.keySet().forEach(name -> {
                String cName = capitalize(name);
                String claz = cName+REQUEST;
                ipw.printf("%s get%s();%n", claz, cName);
            });
            next.forEach((n,np) -> writeRequestInterface(ipw, capitalize(n), cc, np));
        }
        ipw.ends();
    }

    public void writeResponse(IndentPrintWriter ipw, String cMethodName, ClassContext cc, Collection<SqlParam> sp) throws MojoExecutionException {
        if (sp.size()<=1) return;
        if (getOutput()!=null && getOutput().isReflect()) return;
        if (getOutput()!=null && getOutput().isDelegate()) {
            List<String> badNames = sp.stream().map(SqlParam::getName).filter(it -> it.contains(".")).collect(Collectors.toList());
            if (!badNames.isEmpty()) {
                throw new MojoExecutionException("Invalid names for delegate fields: " + String.join(",", badNames));
            }
        }
        writeResponseInterface(ipw, cMethodName, cc, sp);
    }

    private void writeResponseInterface(IndentPrintWriter ipw, String cMethodName, ClassContext cc, Collection<SqlParam> sp) {
        if (getOutput()!=null && getOutput().isDelegate()) {
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
            next.forEach((n,np) -> writeResponseInterface(ipw, capitalize(n), cc, np));

        }
        ipw.ends();
    }

    public void docBegin(IndentPrintWriter ipw) {
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
    public void docInput(IndentPrintWriter ipw, JdbcStatement jdbc) {
        int nSize = jdbc.getNameSize();
        if (1<=nSize && nSize<=IMAX) {
            AtomicInteger count = new AtomicInteger();
            jdbc.getNMap().forEach((name, type) -> ipw.printf(" * @param %s :%1$s (parameter #%d)%n", name, count.incrementAndGet()));
        } else if (nSize>IMAX) {
            if (getInput() != null && getInput().isDelegate()) {
                ipw.printf(" * @param i input parameter delegate%n");
            } else {
                ipw.printf(" * @param i input parameter wrapper%n");
            }
        }
    }
    public void docOutput(IndentPrintWriter ipw, Map<Integer, SqlParam> oMap) {
        if (oMap.size() > 1) {
            ipw.printf(" * @param so output constructor%n");
        }
    }

    public void docOutputUse(IndentPrintWriter ipw, Map<Integer, SqlParam> oMap) {
        if (oMap.size() > 1) {
            ipw.printf(" * @param so output constructor%n");
        }
        ipw.printf(" * @param co output consumer%n");
    }

    public void docEnd(IndentPrintWriter ipw) {
        ipw.printf(" * @throws SQLException SQL error%n");
        ipw.printf(" */%n");
    }
    public void declareGenerics(IndentPrintWriter ipw, String cName, int nSize, int oSize) {
        ComAttribute ia = getInput();
        ComAttribute oa = getOutput();
        boolean inpIsReflect = ia != null && ia.isReflect();
        boolean inpIsDelegate = ia != null && ia.isDelegate();
        boolean outIsReflect = oa != null && oa.isReflect();
        boolean outIsDelegate = oa != null && oa.isDelegate();
        if (oSize <= 1) {
            if (nSize > IMAX) {
                if (inpIsReflect) {
                    ipw.putf("<I> ");
                } else if (inpIsDelegate){
                    ipw.putf("<DI extends Delegate%s"+REQUEST+"> ",cName);
                } else {
                    ipw.putf("<I extends %s"+REQUEST+"> ",cName);
                }
            }
        } else {
            if (nSize <= IMAX) {
                if (outIsReflect) {
                    ipw.putf("<O> ");
                } else if (outIsDelegate){
                    ipw.putf("<DO extends Delegate%s" + RESPONSE + "> ", cName);
                } else {
                    ipw.putf("<O extends %s" + RESPONSE + "> ", cName);
                }
            } else {
                if (inpIsReflect) {
                    if (outIsReflect) {
                        ipw.putf("<I,O> ");
                    } else if (outIsDelegate) {
                        ipw.putf("<I,DO extends Delegate%1$s" + RESPONSE + "> ", cName);
                    } else {
                        ipw.putf("<I,O extends %1$s" + RESPONSE + "> ", cName);
                    }
                } else if (inpIsDelegate) {
                    if (outIsReflect) {
                        ipw.putf("<DI extends Delegate%1$s"+REQUEST+",O> ", cName);
                    } else if (outIsDelegate) {
                        ipw.putf("<DI extends Delegate%1$s"+REQUEST+",DO extends Delegate%1$s" + RESPONSE + "> ", cName);
                    } else {
                        ipw.putf("<DI extends Delegate%1$s"+REQUEST+",O extends %1$s" + RESPONSE + "> ", cName);
                    }
                } else {
                    if (outIsReflect) {
                        ipw.putf("<I extends %1$s"+REQUEST+",O> ", cName);
                    } else if (outIsDelegate) {
                        ipw.putf("<I extends %1$s"+REQUEST+",DO extends Delegate%1$s" + RESPONSE + "> ", cName);
                    } else {
                        ipw.putf("<I extends %1$s"+REQUEST+",O extends %1$s" + RESPONSE + "> ", cName);
                    }
                }
            }
        }
    }
    public void debugAction(IndentPrintWriter ipw, String kPrg, JdbcStatement jdbcStatement, ClassContext cc) {
        if (cc.isDebug()) {
            int nSize = jdbcStatement.getNameSize();
            ipw.printf("if (log.isDebugEnabled()) {%n");
            ipw.more();
            ipw.printf("SqlTrace.showQuery(Q_%s, ", kPrg);
            cc.traceParameterBegin(ipw);
            ipw.more();
            ipw.printf("Object[] parms =  new Object[]{%n");
            ipw.more();
            val eol = new Eol(jdbcStatement.getInpSize());
            if ( nSize <= IMAX) {
                jdbcStatement.getIMap().forEach((k,v) ->
                        ipw.printf("%s%s%n", v.getName(), eol.nl()));
            } else {
                Map<Integer, SqlParam> iMap = jdbcStatement.getIMap();
                boolean isReflect = getInput() != null && getInput().isReflect();
                boolean isDelegate = getInput() != null && getInput().isDelegate();
                if (isReflect) {
                    iMap.forEach((k,v) ->
                            ipw.printf("EmSQL.get(i, \"%s\", %s.class)%s%n", v.getName(), v.getType().getWrapper(), eol.nl()));
                } else if (isDelegate) {
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
}
