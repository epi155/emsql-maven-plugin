package io.github.epi155.emsql.commons;

import io.github.epi155.emsql.api.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.val;
import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static io.github.epi155.emsql.commons.Contexts.*;
import static io.github.epi155.emsql.commons.Tools.capitalize;
import static io.github.epi155.emsql.commons.Tools.getterOf;

@Getter
@NoArgsConstructor
@Setter
public abstract class SqlAction {
    private String execSql;
    /** seconds */ private Integer timeout;
    private boolean tune;
    public abstract InputModel getInput();
    public OutputModel getOutput() { return null; }

    public abstract JdbcStatement sql(Map<String, SqlDataType> fields) throws InvalidQueryException;

    public abstract void writeMethod(PrintModel pw, String methodName, JdbcStatement jdbc, String kPrg);


    public void writeCode(PrintModel ipw, String kPrg) throws InvalidQueryException {
        JdbcStatement jdbc = sql(cc.getFields());
        String sQuery = jdbc.getText();
        ipw.printf("private static final String Q_%s = \"%s\";%n", kPrg, StringEscapeUtils.escapeJava(sQuery));
        /*-------------------------------------------------*/
        writeMethod(ipw, mc.getName(), jdbc, kPrg);
        /*-------------------------------------------------*/

        String cName = capitalize(mc.getName());
        writeRequest(ipw, cName, jdbc.getNMap());
        writeResponse(ipw, cName, jdbc.getOMap().values());
        jdbc.flush();

        if (mc.isInputReflect() || mc.isOutputReflect()) {
            cc.add("io.github.epi155.emsql.runtime.EmSQL");
        }

        jdbc.getIMap().values().forEach(it -> cc.addAll(it.getType().requires()));
        jdbc.getOMap().values().forEach(it -> cc.addAll(it.getType().requires()));

    }

    public void declareInput(PrintModel ipw, @NotNull JdbcStatement jdbc) {
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

    public static void plainGenericsNew(PrintModel ipw, JdbcStatement jdbc) {
        int nSize = mc.nSize();
        if (nSize == 0) {
            ipw.putf("<Void>");
        } else if (nSize == 1) {
            jdbc.getNMap().forEach((name,type) -> ipw.putf("<%s>", type.getWrapper())); // once
        } else if (nSize <= IMAX) {
            ipw.putf("%d<%s>", nSize, jdbc.getNMap().values().stream().map(SqlDataType::getWrapper).collect(Collectors.joining(", ")));
        } else {
            if (mc.isInputDelegate()) {
                ipw.putf("<DI>");
            } else {
                ipw.putf("<I>");
            }
        }
    }

    public void declareReturnNew(@NotNull PrintModel ipw, String eSqlObject, JdbcStatement jdbc, int batchSize) {
        ipw.printf("return new %s", eSqlObject);
        cc.anonymousGenerics(ipw, jdbc);
        ipw.putf("(ps, %d) {%n", batchSize);
    }

    public void declareInputBatch(PrintModel ipw, @NotNull JdbcStatement jdbc) {
        int nSize = mc.nSize();
        if (nSize == 0) {
            ipw.printf("        Void nil");     // any use case ??
        } else if (1<=nSize && nSize<= IMAX) {
            AtomicInteger k = new AtomicInteger();
            jdbc.getNMap().forEach((name,type) -> {
                ipw.printf("        final %s %s", type.getWrapper(), name);
                if (k.incrementAndGet() < nSize) ipw.commaLn();
            });
        } else {
            if (mc.isInputDelegate()) {
                ipw.printf("        final DI i");
            } else {
                ipw.printf("        final I i");
            }
        }
    }
    public void declareOutput(PrintModel ipw) {
        if (mc.oSize() < 2) {
            ipw.putf(")%n");
        } else {
            ipw.commaLn();
            if (mc.isOutputDelegate()) {
                ipw.printf("        final DO o)%n");
            } else {
                ipw.printf("        final %s<O> so)%n", cc.supplier());
            }
        }
        ipw.printf("        throws SQLException {%n");
    }

    public void declareOutputUse(@NotNull PrintModel ipw, String type) {
        ipw.commaLn();
        if (mc.oSize() > 1) {
            if (mc.isOutputDelegate()) {
                ipw.printf("        DO o,%n");
                ipw.printf("        Runnable co)%n");
            } else {
                ipw.printf("        %s<O> so,%n", cc.supplier());
                ipw.printf("        %s<O> co)%n", cc.consumer());
            }
        } else {
            if(mc.isOutputDelegate()) {
                ipw.printf("        Runnable co)%n");
            } else {
                ipw.printf("        %s<%s> co)%n", cc.consumer(), type);
            }
        }
        ipw.printf("        throws SQLException {%n");
    }
    public void fetch(PrintModel ipw, @NotNull Map<Integer, SqlParam> oMap) {
        if (oMap.size() > 1) {
            if (mc.isOutputReflect()) {
                ipw.printf("O o = so.get();%n");
                oMap.forEach((k,s) -> s.pullParameter(ipw, k));
            } else if (mc.isOutputDelegate()){
                oMap.forEach((k,s) -> s.fetchDelegateParameter(ipw, k));
            } else {
                ipw.printf("O o = so.get();%n");
                oMap.forEach((k,s) -> s.fetchParameter(ipw, k));
            }
        } else {
            oMap.forEach((k,v) -> v.fetchValue(ipw, k));
        }
    }
    public void getOutput(PrintModel ipw, @NotNull Map<Integer, SqlParam> oMap) {
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

    public void setInput(@NotNull PrintModel ipw, @NotNull JdbcStatement jdbc) {
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
    public void registerOut(PrintModel ipw, @NotNull Map<Integer, SqlParam> oMap) {
        oMap.forEach((k,s) -> s.registerOutParms(ipw, k));
    }

    public void writeRequest(PrintModel ipw, String cMethodName, @NotNull Map<String, SqlDataType> sp) throws InvalidQueryException {
        if (sp.size()<=IMAX) return;
        if (mc.isInputReflect()) return;
        if (mc.isInputDelegate()) {
            List<String> badNames = sp.keySet().stream().filter(it -> it.contains(".")).collect(Collectors.toList());
            if (!badNames.isEmpty()) {
                throw new InvalidQueryException("Invalid names for delegate fields: " + String.join(",", badNames));
            }
        }
        writeRequestInterface(ipw, cMethodName, sp);
    }

    private void writeRequestInterface(PrintModel ipw, String methodName, Map<String, SqlDataType> sp) {
        if (mc.isInputDelegate()) {
            ipw.printf("public static class Delegate%s"+REQUEST, methodName);
            writeRequestGenerics(ipw,sp);
            ipw.putf(" {%n");
            ipw.more();
            ipw.printf("private Delegate%s"+REQUEST+"() {}%n", methodName);
            sp.forEach((name, type) -> {
                String claz = type.getWrapper();
                ipw.printf("protected %s<%s> %s;%n", cc.supplier(), claz, name);
            });
            ipw.printf("public static Builder%s"+REQUEST+" builder() { return new Builder%1$s"+REQUEST+"(); }%n", methodName);
            ipw.printf("public static class Builder%s"+REQUEST, methodName);
            writeRequestGenerics(ipw,sp);
            ipw.putf(" {%n", methodName);
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
            ipw.putf(" {%n");
            Map<String, Map<String, SqlDataType>> next = throughGetter(ipw, sp);
            next.forEach((n, np) -> writeRequestInterface(ipw, capitalize(n), np));
        }
        ipw.ends();
    }

    private void writeRequestGenerics(PrintModel ipw, Map<String, SqlDataType> sp) {
        sp.forEach((name, kind) -> {
            if (!kind.isScalar() && kind.columns()>1) {
                ipw.putf("<%s extends %s%s>", kind.getGeneric(), capitalize(name), REQUEST);
            }
        });
    }

    public static Map<String, Map<String, SqlDataType>> throughGetter(PrintModel ipw, Map<String, SqlDataType> map) {
        ipw.more();
        Map<String, Map<String, SqlDataType>> next = new LinkedHashMap<>();
        map.forEach((name, type) -> {
            int kDot = name.indexOf('.');
            if (kDot < 0) {
                String cName = capitalize(name);
                String claz = type.getPrimitive();
                ipw.printf("%s %s%s();%n", claz, type.getterPrefix(), cName);
            } else {
                String ante = name.substring(0, kDot);
                String post = name.substring(kDot + 1);
                Map<String, SqlDataType> flds = next.computeIfAbsent(ante, k -> new HashMap<>());
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

    public void writeResponse(PrintModel ipw, String cMethodName, @NotNull Collection<SqlParam> sp) throws InvalidQueryException {
        if (sp.size()<=1) return;
        if (mc.isOutputReflect()) return;
        if (mc.isOutputDelegate()) {
            List<String> badNames = sp.stream().map(SqlParam::getName).filter(it -> it.contains(".")).collect(Collectors.toList());
            if (!badNames.isEmpty()) {
                throw new InvalidQueryException("Invalid names for delegate fields: " + String.join(",", badNames));
            }
        }
        writeResponseInterface(ipw, cMethodName, sp);
    }

    private void writeResponseInterface(PrintModel ipw, String cMethodName, Collection<SqlParam> sp) {
        if (mc.isOutputDelegate()) {
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

    public void docInput(PrintModel ipw, @NotNull JdbcStatement jdbc) {
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
    public void docOutput(PrintModel ipw, @NotNull Map<Integer, SqlParam> oMap) {
        if (oMap.size() > 1) {
            ipw.printf(" * @param so output constructor%n");
        }
    }

    public void docOutputUse(PrintModel ipw, @NotNull Map<Integer, SqlParam> oMap) {
        if (oMap.size() > 1) {
            ipw.printf(" * @param so output constructor%n");
        }
        ipw.printf(" * @param co output consumer%n");
    }

    public void docEnd(@NotNull PrintModel ipw) {
        ipw.printf(" * @throws SQLException SQL error%n");
        ipw.printf(" */%n");
    }
    public void declareGenerics(PrintModel ipw, String cName, List<String> inFlds) {
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
                if (mc.isOutputReflect()) {
                    ipw.putf("<O");
                } else if (mc.isOutputDelegate()){
                    ipw.putf("<DO extends Delegate%s" + RESPONSE, cName);
                } else {
                    ipw.putf("<O extends %s" + RESPONSE, cName);
                }
                genericIn(ipw, inFlds, ",", null);
                ipw.putf("> ");
            } else {
                if (mc.isInputReflect()) {
                    ipw.putf("<I");
                    if(!inFlds.isEmpty())
                        for(int k=1; k<=inFlds.size(); k++) {
                            ipw.putf(",L%d",k);
                        }
                    if (mc.isOutputReflect()) {
                        ipw.putf(",O> ");
                    } else if (mc.isOutputDelegate()) {
                        ipw.putf(",DO extends Delegate%1$s" + RESPONSE + "> ", cName);
                    } else {
                        ipw.putf(",O extends %1$s" + RESPONSE + "> ", cName);
                    }
                } else if (mc.isInputDelegate()) {
                    ipw.putf("<DI extends Delegate%1$s"+REQUEST, cName);
                    if(!inFlds.isEmpty())
                        genericInner(ipw, inFlds);
                    if (mc.isOutputReflect()) {
                        ipw.putf(",O> ");
                    } else if (mc.isOutputDelegate()) {
                        ipw.putf(",DO extends Delegate%1$s" + RESPONSE + "> ", cName);
                    } else {
                        ipw.putf(",O extends %1$s" + RESPONSE + "> ", cName);
                    }
                } else {
                    ipw.putf("<I extends %1$s"+REQUEST, cName);
                    if(!inFlds.isEmpty())
                        genericInner(ipw, inFlds);
                    if (mc.isOutputReflect()) {
                        ipw.putf(",O> ");
                    } else if (mc.isOutputDelegate()) {
                        ipw.putf(",DO extends Delegate%1$s" + RESPONSE + "> ", cName);
                    } else {
                        ipw.putf(",O extends %1$s" + RESPONSE + "> ", cName);
                    }
                }
            }
        }
    }

    private void genericInner(PrintModel ipw, List<String> flds) {
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
                ipw.putf(",L%d extends %s%s", ++k, capitalize(fld), REQUEST);
            }
        }
    }

    private void genericIn(PrintModel ipw, List<String> inFlds, String prefix, String suffix) {
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

    public void debugAction(PrintModel ipw, String kPrg, JdbcStatement jdbcStatement) {
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

    public void setQueryHints(PrintModel ipw) {
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
    public void expandIn(@NotNull PrintModel ipw, @NotNull Map<Integer, SqlParam> notScalar, String kPrg) {
        cc.add(ClassContextImpl.RUNTIME_EMSQL);
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
    public void openQuery(PrintModel ipw, JdbcStatement jdbc, String kPrg) {
        Map<Integer, SqlParam> notScalar = notScalar(jdbc.getIMap());
        if (notScalar.isEmpty()) {
            ipw.printf("try (PreparedStatement ps = c.prepareStatement(Q_%s)) {%n", kPrg);
        } else {
            expandIn(ipw, notScalar, kPrg);
            ipw.printf("try (PreparedStatement ps = c.prepareStatement(query)) {%n");
        }

    }
}
