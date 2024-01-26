package io.github.epi155.esql.plugin.sql;

import io.github.epi155.esql.plugin.ClassContext;
import io.github.epi155.esql.plugin.ComAttribute;
import io.github.epi155.esql.plugin.IndentPrintWriter;
import io.github.epi155.esql.plugin.Tools;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.val;
import org.apache.maven.plugin.MojoExecutionException;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Setter
@Getter
@NoArgsConstructor
public abstract class SqlAction {
    protected static final int IMAX = 3;
    protected static final String REQUEST = "PS";
    protected static final String RESPONSE = "RS";
    private String execSql;
    /** seconds */ private Integer timeout;
    protected abstract ComAttribute getInput();
    protected ComAttribute getOutput() { return null; }

    public abstract JdbcStatement sql(Map<String, SqlEnum> fields) throws MojoExecutionException;

    public abstract void writeMethod(IndentPrintWriter pw, String methodName, JdbcStatement jdbc, String kPrg, ClassContext cc);

    protected void declareInput(IndentPrintWriter ipw, Map<Integer, SqlParam> iMap, String cName) {
        int iSize = iMap.size();
        if (iSize == 1) {
            ipw.commaLn();
            SqlParam parm = iMap.get(1);
            ipw.printf("        %s %s", parm.getType().getPrimitive(), parm.getName());
        } else if (iSize <= IMAX){
            iMap.forEach((k,s) -> {
                ipw.commaLn();
                ipw.printf("        %s %s", s.getType().getPrimitive(), s.getName());
            });
        } else {
            ipw.commaLn();
            if (getInput() != null && getInput().isDelegate()) {
                ipw.printf("        DI i", cName);
            } else {
                ipw.printf("        I i", cName);
            }
        }
    }
    protected void declareNewInstance(IndentPrintWriter ipw, String eSqlObject, Map<Integer, SqlParam> iMap, String cName) {
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
                ipw.putf("<DI>", cName);
            } else {
                ipw.putf("<I>", cName);
            }
        }
        ipw.putf(" new%s(%n", cName);
        ipw.printf("        Connection c)%n", cName);
        ipw.printf("        throws SQLException {%n");
    }
    protected void declareReturnNew(IndentPrintWriter ipw, String eSqlObject, Map<Integer, SqlParam> iMap, int batchSize) {
        int iSize = iMap.size();
        ipw.printf("return new %s", eSqlObject);
        if (iSize == 0) {
            ipw.putf("<Void>");
        } else if (iSize == 1) {
            ipw.putf("<>");
        } else if (iSize <= IMAX) {
            ipw.putf("%d<>", iSize);
        } else {
            ipw.putf("<>");
        }
        ipw.putf("(ps, %d) {%n", batchSize);
    }

    protected void declareInputBatch(IndentPrintWriter ipw, Map<Integer, SqlParam> iMap) {
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
    protected void declareOutput(IndentPrintWriter ipw, int oSize, ClassContext cc) {
        boolean isDelegate = getOutput() != null && getOutput().isDelegate();
        if (oSize > 1 || isDelegate) {
            ipw.commaLn();
            if (isDelegate) {
                ipw.printf("        DO o)%n");
            } else {
                cc.add("java.util.function.Supplier");
                ipw.printf("        Supplier<O> so)%n");
            }
        } else {
            ipw.putf(")%n");
        }
        ipw.printf("        throws SQLException {%n");
    }

    protected void declareOutputUse(IndentPrintWriter ipw, int oSize, String type, ClassContext cc) {
        boolean isDelegate = getOutput()!=null && getOutput().isDelegate();
        ipw.commaLn();
        if (oSize > 1) {
            if (isDelegate) {
                ipw.printf("        DO o,%n");
                ipw.printf("        Runnable co)%n");
            } else {
                cc.add("java.util.function.Supplier");
                cc.add("java.util.function.Consumer");
                ipw.printf("        Supplier<O> so,%n");
                ipw.printf("        Consumer<O> co)%n");
            }
        } else {
            if(isDelegate) {
                ipw.printf("        Runnable co)%n");
            } else {
                ipw.printf("        Consumer<%s> co)%n", type);
            }
        }
        ipw.printf("        throws SQLException {%n");
    }
    protected void fetch(IndentPrintWriter ipw, Map<Integer, SqlParam> oMap, ClassContext cc) {
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
    protected void getOutput(IndentPrintWriter ipw, Map<Integer, SqlParam> oMap, ClassContext cc) {
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

    protected void setInput(IndentPrintWriter ipw, Map<Integer, SqlParam> iMap) {
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
    protected void registerOut(IndentPrintWriter ipw, Map<Integer, SqlParam> oMap) {
        oMap.forEach((k,s) -> s.registerOutParms(ipw, k));
    }

    public void writeRequest(IndentPrintWriter ipw, String cMethodName, ClassContext cc, Collection<SqlParam> sp) {
        if (sp.size()<=IMAX) return;
        if (getInput() != null && getInput().isReflect()) return;
        if (getInput() != null && getInput().isDelegate()) {
            cc.add("java.util.function.Supplier");
            ipw.printf("public static class Delegate%s"+REQUEST+" {%n", cMethodName);
            ipw.more();
            ipw.printf("private Delegate%s"+REQUEST+"() {}%n", cMethodName);
            sp.forEach(p -> {
                String claz = p.getType().getWrapper();
                ipw.printf("protected Supplier<%s> %s;%n", claz, p.getName());
            });
            ipw.printf("public static Builder%s"+REQUEST+" builder() { return new Builder%1$s"+REQUEST+"(); }%n", cMethodName);
            ipw.printf("public static class Builder%s"+REQUEST+" {%n", cMethodName);
            ipw.more();
            ipw.printf("private Builder%s"+REQUEST+"() {}%n", cMethodName);
            sp.forEach(p -> {
                String claz = p.getType().getWrapper();
                ipw.printf("private Supplier<%s> %s;%n", claz, p.getName());
            });
            ipw.printf("public Delegate%s"+REQUEST+" build() {%n", cMethodName);
            ipw.more();
            ipw.printf("Delegate%s"+REQUEST+" result = new Delegate%1$s"+REQUEST+"();%n", cMethodName);
            sp.forEach(p -> ipw.printf("result.%s = %1$s==null ? () -> null : %1$s;%n", p.getName()));
            ipw.printf("return  result;%n");
            ipw.ends();
            sp.forEach(p -> ipw.printf("public Builder%s"+REQUEST+" %s(Supplier<%s> %2$s) { this.%2$s = %2$s; return this; }%n",
                    cMethodName, p.getName(), p.getType().getWrapper()));
            ipw.ends();
        } else {
            ipw.printf("public interface %s"+REQUEST+" {%n", cMethodName);
            ipw.more();
            sp.forEach(p -> {
                String cName = Tools.capitalize(p.getName());
                String claz = p.getType().getPrimitive();
                ipw.printf("%s get%s();%n", claz, cName);
            });
        }
        ipw.ends();
    }

    public void writeResponse(IndentPrintWriter ipw, String cMethodName, ClassContext cc, Collection<SqlParam> sp) {
        if (sp.size()<=1) return;
        if (getOutput()!=null && getOutput().isReflect()) return;
        if (getOutput()!=null && getOutput().isDelegate()) {
            cc.add("java.util.function.Consumer");
            ipw.printf("public static class Delegate%s"+RESPONSE+" {%n", cMethodName);
            ipw.more();
            sp.forEach(p -> {
                String claz = p.getType().getWrapper();
                ipw.printf("protected Consumer<%s> %s;%n", claz, p.getName());
            });
            ipw.printf("public static Builder%s"+RESPONSE+" builder() { return new Builder%1$s"+RESPONSE+"(); }%n", cMethodName);
            ipw.printf("public static class Builder%s"+RESPONSE+" {%n", cMethodName);
            ipw.more();
            ipw.printf("private Builder%s"+RESPONSE+"() {}%n", cMethodName);
            sp.forEach(p -> {
                String claz = p.getType().getWrapper();
                ipw.printf("private Consumer<%s> %s;%n", claz, p.getName());
            });
            ipw.printf("public Delegate%s"+RESPONSE+" build() {%n", cMethodName);
            ipw.more();
            ipw.printf("Delegate%s"+RESPONSE+" result = new Delegate%1$s"+RESPONSE+"();%n", cMethodName);
            sp.forEach(p -> ipw.printf("result.%s = %1$s==null ? it -> {} : %1$s;%n", p.getName()));
            ipw.printf("return  result;%n");
            ipw.ends();
            sp.forEach(p -> ipw.printf("public Builder%s"+RESPONSE+" %s(Consumer<%s> %2$s) { this.%2$s = %2$s; return this; }%n",
                    cMethodName, p.getName(), p.getType().getWrapper()));
            ipw.ends();

        } else {
            ipw.printf("public interface %s"+RESPONSE+" {%n", cMethodName);
            ipw.more();
            sp.forEach(p -> {
                String cName = Tools.capitalize(p.getName());
                String claz = p.getType().getPrimitive();
                ipw.printf("void set%s(%s s);%n", cName, claz);
            });
        }
        ipw.ends();
    }
    protected void docBegin(IndentPrintWriter ipw) {
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
    protected void docInput(IndentPrintWriter ipw, Map<Integer, SqlParam> iMap) {
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
    protected void docOutput(IndentPrintWriter ipw, Map<Integer, SqlParam> oMap) {
        if (oMap.size() > 1) {
            ipw.printf(" * @param so output constructor%n");
        }
    }

    protected void docOutputUse(IndentPrintWriter ipw, Map<Integer, SqlParam> oMap) {
        if (oMap.size() > 1) {
            ipw.printf(" * @param so output constructor%n");
        }
        ipw.printf(" * @param co output consumer%n");
    }

    protected void docEnd(IndentPrintWriter ipw) {
        ipw.printf(" * @throws SQLException SQL error%n");
        ipw.printf(" */%n");
    }
    protected void declareGenerics(IndentPrintWriter ipw, String cName, int iSize, int oSize) {
        ComAttribute ia = getInput();
        ComAttribute oa = getOutput();
        boolean inpIsReflect = ia != null && ia.isReflect();
        boolean inpIsDelegate = ia != null && ia.isDelegate();
        boolean outIsReflect = oa != null && oa.isReflect();
        boolean outIsDelegate = oa != null && oa.isDelegate();
        if (oSize == 1) {
            if (iSize <= IMAX) {
                // nop
            } else {
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
    protected void debugAction(IndentPrintWriter ipw, String kPrg, Map<Integer, SqlParam> iMap, ClassContext cc) {
        if (cc.isDebug()) {
            int iSize = iMap.size();
            ipw.printf("if (log.isDebugEnabled()) {%n");
            ipw.more();
            ipw.printf("ESqlTrace.showQuery(Q_%s, () -> {%n", kPrg);
            ipw.more();
            ipw.printf("Object[] parms =  new Object[]{%n");
            ipw.more();
            if ( iSize <= IMAX) {
                iMap.forEach((k,v) ->
                        ipw.printf("%s%s%n", v.getName(), k<iSize?",":""));
            } else {
                boolean isReflect = getInput() != null && getInput().isReflect();
                boolean isDelegate = getInput() != null && getInput().isDelegate();
                if (isReflect) {
                    iMap.forEach((k,v) ->
                            ipw.printf("ESQL.get(i, \"%s\", %s.class)%s%n", v.getName(), v.getType().getWrapper(), k<iSize?",":""));
                } else if (isDelegate) {
                    iMap.forEach((k,v) ->
                            ipw.printf("i.%s.get()%s%n", v.getName(), k<iSize?",":""));
                } else {
                    iMap.forEach((k,v) ->
                            ipw.printf("i.get%s()%s%n", Tools.capitalize(v.getName()), k<iSize?",":""));
                }
            }
            ipw.less();
            ipw.printf("};%n");
            ipw.printf("return parms;%n");
            ipw.less();
            ipw.printf("});%n");
            ipw.ends();
        }
    }

}
