package io.github.epi155.esql.plugin.sql;

import io.github.epi155.esql.plugin.IndentPrintWriter;
import io.github.epi155.esql.plugin.SqlParam;
import io.github.epi155.esql.plugin.Tools;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.val;
import org.apache.maven.plugin.MojoExecutionException;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@SuperBuilder
@NoArgsConstructor
public abstract class SqlAction {
    protected static final int IMAX = 3;
    protected static final String REQUEST = "Request";
    protected static final String RESPONSE = "Response";
    private String query;
    /** seconds */ private Integer timeout;
    /** DO NOT USE, reduces performance by 14% */ private boolean reflect;

    public abstract JdbcStatement sql() throws MojoExecutionException;

    public abstract void writeMethod(IndentPrintWriter pw, String methodName, JdbcStatement jdbc, String kPrg, Set<String> set);

    protected void declareInput(IndentPrintWriter ipw, Map<Integer, SqlParam> iMap, String cName) {
        int iSize = iMap.size();
        if (iSize == 1) {
            ipw.commaLn();
            SqlParam parm = iMap.get(1);
            ipw.printf("        %s %s", parm.getType().getRaw(), parm.getName());
        } else if (iSize <= IMAX){
            iMap.forEach((k,s) -> {
                ipw.commaLn();
                ipw.printf("        %s %s", s.getType().getRaw(), s.getName());
            });
        } else {
            ipw.commaLn();
            if (reflect) {
                ipw.printf("        T i", cName);
            } else {
                ipw.printf("        %s"+REQUEST+" i", cName);
            }
        }
    }
    protected void declareNewInstance(IndentPrintWriter ipw, String eSqlObject, Map<Integer, SqlParam> iMap, String cName) {
        int iSize = iMap.size();
        ipw.printf("public static %s", eSqlObject);
        if (iSize == 0) {
            ipw.putf("<Void>");
        } else if (iSize == 1) {
            ipw.putf("<%s>", iMap.get(1).getType().getAccess());
        } else if (iSize <= IMAX) {
            ipw.putf("%d<%s>", iSize, iMap.values().stream().map(it -> it.getType().getAccess()).collect(Collectors.joining(", ")));
        } else {
            if (reflect) {
                ipw.putf("<T>", cName);
            } else {
                ipw.putf("<%s"+REQUEST+">", cName);
            }
        }
        ipw.putf(" new%s(%n", cName);
        ipw.printf("        Connection c)%n", cName);
        ipw.printf("        throws SQLException {%n");
    }
    protected void declareReturnNew(IndentPrintWriter ipw, String eSqlObject, Map<Integer, SqlParam> iMap, String cName) {
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
        ipw.putf("() {%n", cName);
    }

    protected void declareInputBatch(IndentPrintWriter ipw, Map<Integer, SqlParam> iMap, String cName) {
        int iSize = iMap.size();
        if (iSize == 1) {
            SqlParam parm = iMap.get(1);
            ipw.printf("        %s %s", parm.getType().getAccess(), parm.getName());
        } else if (iSize <= IMAX) {
            iMap.forEach((k,s) -> {
                ipw.printf("        %s %s", s.getType().getAccess(), s.getName());
                if (k<iSize) ipw.commaLn();
            });
        } else {
            if (reflect) {
                ipw.printf("        T i");
            } else {
                ipw.printf("        %s"+REQUEST+" i", cName);
            }
        }
    }
    protected void declareOutput(IndentPrintWriter ipw, int oSize, Set<String> set) {
        if (oSize > 1) {
            ipw.commaLn();
            set.add("java.util.function.Supplier");
            ipw.printf("        Supplier<R> so)%n");
        } else {
            ipw.putf(")%n");
        }
        ipw.printf("        throws SQLException {%n");
    }
    protected void declareOutputUpdate(IndentPrintWriter ipw, int oSize, String type, Set<String> set) {
        ipw.commaLn();
        set.add("java.util.function.Function");
        if (oSize > 1) {
            set.add("java.util.function.Supplier");
            ipw.printf("        Supplier<R> so,%n");
            ipw.printf("        Function<R,Optional<R>> uo)%n");
        } else {
            ipw.printf("        Function<%s,Optional<%1$s>> uo)%n", type);
        }
        ipw.printf("        throws SQLException {%n");
    }
    protected void declareOutputUse(IndentPrintWriter ipw, int oSize, String type, Set<String> set) {
        ipw.commaLn();
        set.add("java.util.function.Consumer");
        if (oSize > 1) {
            set.add("java.util.function.Supplier");
            ipw.printf("        Supplier<R> so,%n");
            ipw.printf("        Consumer<R> co)%n");
        } else {
            ipw.printf("        Consumer<%s> co)%n", type);
        }
        ipw.printf("        throws SQLException {%n");
    }
    protected void fetch(IndentPrintWriter ipw, Map<Integer, SqlParam> oMap, Set<String> set) {
        if (oMap.size() > 1) {
            ipw.printf("R o = so.get();%n");
            if (reflect) {
                oMap.forEach((k,s) -> s.pullParameter(ipw, k, set));
            } else {
                oMap.forEach((k,s) -> s.fetchParameter(ipw, k, set));
            }
        } else {
            oMap.forEach((k,v) -> v.fetchValue(ipw, k, set));
        }
    }
    protected void getOutput(IndentPrintWriter ipw, Map<Integer, SqlParam> oMap, Set<String> set) {
        if (oMap.size() > 1) {
            ipw.printf("R o = so.get();%n");
//            if (reflect) {
//                oMap.forEach((k,s) -> s.pullParameter(ipw, k, set));
//            } else {
                oMap.forEach((k,s) -> s.getParameter(ipw, k, set));
//            }
        } else {
            oMap.forEach((k,v) -> v.getValue(ipw, k, set)); // once
        }
    }


    protected void update(IndentPrintWriter ipw, Map<Integer, SqlParam> oMap, Set<String> set) {
        if (oMap.size() > 1) {
            oMap.forEach((k,s) -> s.updateParameter(ipw, k));
        } else {
            oMap.get(1).updateValue(ipw, 1, set);
        }
    }

    protected void setInput(IndentPrintWriter ipw, Map<Integer, SqlParam> iMap) {
        int iSize = iMap.size();
        if (iSize == 1) {
            iMap.get(1).setValue(ipw, 1);
        } else if (iSize <= IMAX) {
            iMap.forEach((k,s) -> s.setValue(ipw, k));
        } else {
            if (reflect) {
                iMap.forEach((k, s) -> s.pushParameter(ipw, k));
            } else {
                iMap.forEach((k, s) -> s.setParameter(ipw, k));
            }
        }
    }
    protected void registerOut(IndentPrintWriter ipw, Map<Integer, SqlParam> oMap) {
        oMap.forEach((k,s) -> s.register(ipw, k));
    }

    public void writeRequest(IndentPrintWriter ipw, String cMethodName, Collection<SqlParam> sp) {
        if (sp.size()<=IMAX) return;
        if (reflect) return;
        ipw.printf("public interface %s"+REQUEST+" {%n", cMethodName);
        ipw.more();
        sp.forEach(p -> {
            String cName = Tools.capitalize(p.getName());
            String claz = p.getType().getRaw();
            ipw.printf("%s get%s();%n", claz, cName);
        });
        ipw.ends();
    }

    public void writeResponse(IndentPrintWriter ipw, String cMethodName, Collection<SqlParam> sp) {
        if (sp.size()<=1) return;
        if (reflect) return;
        ipw.printf("public interface %sResponse {%n", cMethodName);
        ipw.more();
        sp.forEach(p -> {
            String cName = Tools.capitalize(p.getName());
            String claz = p.getType().getRaw();
            ipw.printf("void set%s(%s s);%n", cName, claz);
        });
        ipw.ends();
    }
    protected void docBegin(IndentPrintWriter ipw) {
        ipw.printf("/**%n");
        ipw.printf(" * Template %s%n", this.getClass().getSimpleName());
        ipw.printf(" * <pre>%n");
        val lines = query.split("\n");
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
        } else if (iSize <= 2){
            iMap.forEach((k,s) -> ipw.printf(" * @param %s :%1$s (parameter #%d)%n", s.getName(), k));
        } else {
            ipw.printf(" * @param i input parameter wrapper%n");
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
        ipw.printf("*/%n");
    }

}
