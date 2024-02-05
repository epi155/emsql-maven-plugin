package io.github.epi155.emsql.plugin.sql;

import io.github.epi155.emsql.plugin.ClassContext;
import io.github.epi155.emsql.plugin.ComAttribute;
import io.github.epi155.emsql.plugin.IndentPrintWriter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.val;
import org.apache.maven.plugin.MojoExecutionException;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static io.github.epi155.emsql.plugin.Tools.capitalize;
import static io.github.epi155.emsql.plugin.Tools.getOf;

@Setter
@Getter
@NoArgsConstructor
public abstract class SqlAction {
    public static final int IMAX = 3;
    protected static final String REQUEST = "PS";
    protected static final String RESPONSE = "RS";
    private String execSql;
    /** seconds */ private Integer timeout;
    protected abstract ComAttribute getInput();
    protected ComAttribute getOutput() { return null; }

    public abstract JdbcStatement sql(Map<String, SqlEnum> fields) throws MojoExecutionException;

    public abstract void writeMethod(IndentPrintWriter pw, String methodName, JdbcStatement jdbc, String kPrg, ClassContext cc);

    public void declareInput(IndentPrintWriter ipw, Map<Integer, SqlParam> iMap, String cName) {
        int iSize = iMap.size();
        if (iSize == 1) {
            ipw.commaLn();
            SqlParam parm = iMap.get(1);
            ipw.printf("        final %s %s", parm.getType().getPrimitive(), parm.getName());
        } else if (iSize <= IMAX){
            iMap.forEach((k,s) -> {
                ipw.commaLn();
                ipw.printf("        final %s %s", s.getType().getPrimitive(), s.getName());
            });
        } else {
            ipw.commaLn();
            if (getInput() != null && getInput().isDelegate()) {
                ipw.printf("        final DI i", cName);
            } else {
                ipw.printf("        final I i", cName);
            }
        }
    }
    public void declareNewInstance(IndentPrintWriter ipw, String eSqlObject, Map<Integer, SqlParam> iMap, String cName) {
        int iSize = iMap.size();
        ipw.printf("public static ");
        declareGenerics(ipw, cName, iSize, 1);
        ipw.putf("%s", eSqlObject);
        if (iSize == 0) {
            ipw.putf("<Void>");
        } else if (iSize == 1) {
            ipw.putf("<%s>", iMap.get(1).getType().getWrapper());
        } else if (iSize <= IMAX) {
            ipw.putf("%d<%s>", iSize, iMap.values().stream().map(it -> it.getType().getWrapper()).collect(Collectors.joining(", ")));
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
    public void declareReturnNew(IndentPrintWriter ipw, ClassContext cc, String eSqlObject, Map<Integer, SqlParam> iMap, int batchSize) {
        ipw.printf("return new %s", eSqlObject);
        cc.anonymousGenerics(ipw, iMap, getInput());
        ipw.putf("(ps, %d) {%n", batchSize);
    }

    public void declareInputBatch(IndentPrintWriter ipw, Map<Integer, SqlParam> iMap) {
        int iSize = iMap.size();
        if (iSize == 0) {
            ipw.printf("        Void nil");     // any use case ??
        } else if (iSize == 1) {
            SqlParam parm = iMap.get(1);
            ipw.printf("        %s %s", parm.getType().getWrapper(), parm.getName());
        } else if (iSize <= IMAX) {
            iMap.forEach((k,s) -> {
                ipw.printf("        %s %s", s.getType().getWrapper(), s.getName());
                if (k<iSize) ipw.commaLn();
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

    public void setInput(IndentPrintWriter ipw, Map<Integer, SqlParam> iMap) {
        int iSize = iMap.size();
        if (iSize == 1) {
            iMap.get(1).setValue(ipw, 1);
        } else if (iSize <= IMAX) {
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

    public void writeRequest(IndentPrintWriter ipw, String cMethodName, ClassContext cc, Collection<SqlParam> sp) {
        if (sp.size()<=IMAX) return;
        if (getInput() != null && getInput().isReflect()) return;
        if (getInput() != null && getInput().isDelegate()) {
            ipw.printf("public static class Delegate%s"+REQUEST+" {%n", cMethodName);
            ipw.more();
            ipw.printf("private Delegate%s"+REQUEST+"() {}%n", cMethodName);
            sp.forEach(p -> {
                String claz = p.getType().getWrapper();
                ipw.printf("protected %s<%s> %s;%n", cc.supplier(), claz, p.getName());
            });
            ipw.printf("public static Builder%s"+REQUEST+" builder() { return new Builder%1$s"+REQUEST+"(); }%n", cMethodName);
            ipw.printf("public static class Builder%s"+REQUEST+" {%n", cMethodName);
            ipw.more();
            ipw.printf("private Builder%s"+REQUEST+"() {}%n", cMethodName);
            sp.forEach(p -> {
                String claz = p.getType().getWrapper();
                ipw.printf("private %s<%s> %s;%n", cc.supplier(), claz, p.getName());
            });
            ipw.printf("public Delegate%s"+REQUEST+" build() {%n", cMethodName);
            ipw.more();
            ipw.printf("Delegate%s"+REQUEST+" result = new Delegate%1$s"+REQUEST+"();%n", cMethodName);
            cc.delegateRequestFields(ipw, sp);
            ipw.printf("return  result;%n");
            ipw.ends();
            sp.forEach(p -> ipw.printf("public Builder%s"+REQUEST+" %s(%s<%s> %2$s) { this.%2$s = %2$s; return this; }%n",
                    cMethodName, p.getName(), cc.supplier(), p.getType().getWrapper()));
            ipw.ends();
        } else {
            ipw.printf("public interface %s"+REQUEST+" {%n", cMethodName);
            ipw.more();
            sp.forEach(p -> {
                String cName = capitalize(p.getName());
                String claz = p.getType().getPrimitive();
                ipw.printf("%s %s%s();%n", claz, getOf(p), cName);
            });
        }
        ipw.ends();
    }

    public void writeResponse(IndentPrintWriter ipw, String cMethodName, ClassContext cc, Collection<SqlParam> sp) {
        if (sp.size()<=1) return;
        if (getOutput()!=null && getOutput().isReflect()) return;
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
            sp.forEach(p -> {
                String cName = capitalize(p.getName());
                String claz = p.getType().getPrimitive();
                ipw.printf("void set%s(%s s);%n", cName, claz);
            });
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
    }
    public void docInput(IndentPrintWriter ipw, Map<Integer, SqlParam> iMap) {
        int iSize = iMap.size();
        if (iSize == 1) {
            SqlParam parm = iMap.get(1);
            ipw.printf(" * @param %s :%1$s (parameter #1)%n", parm.getName());
        } else if (iSize <= IMAX){
            iMap.forEach((k,s) -> ipw.printf(" * @param %s :%1$s (parameter #%d)%n", s.getName(), k));
        } else {
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
    public void declareGenerics(IndentPrintWriter ipw, String cName, int iSize, int oSize) {
        ComAttribute ia = getInput();
        ComAttribute oa = getOutput();
        boolean inpIsReflect = ia != null && ia.isReflect();
        boolean inpIsDelegate = ia != null && ia.isDelegate();
        boolean outIsReflect = oa != null && oa.isReflect();
        boolean outIsDelegate = oa != null && oa.isDelegate();
        if (oSize <= 1) {
            if (iSize > IMAX) {
                if (inpIsReflect) {
                    ipw.putf("<I> ");
                } else if (inpIsDelegate){
                    ipw.putf("<DI extends Delegate%s"+REQUEST+"> ",cName);
                } else {
                    ipw.putf("<I extends %s"+REQUEST+"> ",cName);
                }
            }
        } else {
            if (iSize <= IMAX) {
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
    public void debugAction(IndentPrintWriter ipw, String kPrg, Map<Integer, SqlParam> iMap, ClassContext cc) {
        if (cc.isDebug()) {
            int iSize = iMap.size();
            ipw.printf("if (log.isDebugEnabled()) {%n");
            ipw.more();
            ipw.printf("SqlTrace.showQuery(Q_%s, ", kPrg);
            cc.traceParameterBegin(ipw);
            ipw.more();
            ipw.printf("Object[] parms =  new Object[]{%n");
            ipw.more();
            val eol = new Eol(iSize);
            if ( iSize <= IMAX) {
                iMap.forEach((k,v) ->
                        ipw.printf("%s%s%n", v.getName(), eol.nl()));
            } else {
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
                            ipw.printf("i.%s%s()%s%n", getOf(v), capitalize(v.getName()), eol.nl()));
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
