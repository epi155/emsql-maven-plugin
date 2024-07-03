package io.github.epi155.emsql.commons;

import io.github.epi155.emsql.api.SqlDataType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class Tools {

    private Tools() {}

    interface ApiStore<T> {

        T close(String text);

        void push(String parm);
        default String getPlaceholder() { return "?"; }
    }

    public static String oneLine(@NotNull String text) {
        StringBuilder sb = new StringBuilder();
        char[] ac = text.toCharArray();
        boolean wasBreak = isBreak(ac[0]);
        byte minus = 0;
        for (val c: ac) {
            boolean isBreak = isBreak(c);
            if (!isBreak) {
                if (c=='-') {
                    if (minus<2) {
                        minus++;
                    }
                } else {
                    if (minus==1) {
                        sb.append("-");
                        minus = 0;
                    }
                    if (minus==0) {
                        sb.append(c);
                    }
                }
            } else if (!wasBreak) {
                minus = 0;
                sb.append(' ');
            }
            wasBreak = isBreak;
        }
        return sb.toString().replaceAll("\\s\\s+", " ").trim();
    }

    private static boolean isBreak(char c) {
        return c=='\n' || c=='\r';
    }
    private static final char[] BREAK_PARMS = {' ', ',',')',';','\n','=','+','-','*','/'};
    public static SqlStatement replacePlaceholder(String text, Map<String, SqlDataType> fields, boolean enableList) {
        int ixCol = text.indexOf(':');
        if (ixCol<0) {
            // there are no parameters
            return new SqlStatement(text, Map.of());
        }
        int ixEnd = indexOf(text, ixCol + 1);
        if (ixEnd<0) {
            // only one parameter at end-of-text (no space)
            String parm = text.substring(ixCol + 1);
            SqlDataType type = fields.get(parm);
            if (type==null)
                throw new InvalidSqlParameter(parm, fields);
            Map<Integer, SqlParam> map = new HashMap<>();
            map.put(1, new SqlParam(parm, type));   // mutable map required
            if (type.isScalar()){
                return new SqlStatement(text.substring(0, ixCol)+"?", map);
            } else if (enableList) {
                return new SqlStatement(text.substring(0, ixCol)+"[#1]", map);
            } else {
                throw new InvalidSqlParameter(parm);
            }
        }
        String parm = text.substring(ixCol + 1, ixEnd);

        class MapStore implements ApiStore<SqlStatement> {
            private final Map<Integer, SqlParam> iMap = new LinkedHashMap<>();
            private int k=1;
            @Getter
            private String placeholder;

            public void push(String parm) {
                if (parm.isEmpty())
                    return;
                SqlDataType type = fields.get(parm);
                if (type==null) {
                       throw new InvalidSqlParameter(parm, fields);
                } else {
                    if (type.isScalar()) {
                        this.placeholder = "?";
                    } else if (enableList) {
                        this.placeholder = "[#"+k+"]";
                    } else {
                        throw new InvalidSqlParameter(parm);
                    }
                    iMap.put(k++, new SqlParam(parm, type));
                }
            }

            public SqlStatement close(String s) {
                return new SqlStatement(s, iMap);
            }
        }
        val store = new MapStore();
        store.push(parm);
        return deepScan(text, ixCol, ixEnd, store);
    }

    private static <R> R deepScan(String text, int ixCol, int ixEnd, ApiStore<R> store) {
        String parm;
        val sb = new StringBuilder();
        sb.append(text, 0, ixCol).append(store.getPlaceholder());
        while(ixEnd<text.length()) {
            int ixOld = ixEnd;
            ixCol = text.indexOf(':', ixEnd);
            if (ixCol<0) {
                // there are no more parameters
                sb.append(text.substring(ixOld));
                return store.close(sb.toString());
            }
            ixEnd = indexOf(text, ixCol + 1);
            if (ixEnd<0) {
                // parameter at end-of-text (no space)
                parm = text.substring(ixCol + 1);
                store.push(parm);
                sb.append(text, ixOld, ixCol).append(store.getPlaceholder());
                return store.close(sb.toString());
            }
            parm = text.substring(ixCol + 1, ixEnd);
            store.push(parm);
            sb.append(text, ixOld, ixCol).append(store.getPlaceholder());
        }

        return null;    // dead exit point
    }
    public static JdbcStatement replacePlaceholder(
            String text,
            Map<String, SqlDataType> iFields,
            Map<String, SqlDataType> oFields,
            Map<String, SqlDataType> ioFields
    ) {
        int ixCol = text.indexOf(':');
        if (ixCol<0) {
            // there are no parameters
            return new JdbcStatement(text, Map.of(), Map.of());
        }
        int ixEnd = indexOf(text, ixCol + 1);
        if (ixEnd<0) {
            // only one parameter at end-of-text (no space)
            String parm = text.substring(ixCol + 1);
            SqlDataType type = ioFields.get(parm);
            if (type != null) {
                return new JdbcStatement(
                        text.substring(0, ixCol)+"?",
                        Map.of(1, new SqlParam(parm, type)),
                        Map.of(1, new SqlParam(parm, type)));
            }
            type = iFields.get(parm);
            if (type != null) {
                return new JdbcStatement(
                        text.substring(0, ixCol)+"?",
                        Map.of(1, new SqlParam(parm, type)),
                        Map.of());
            }
            type = oFields.get(parm);
            if (type != null) {
                return new JdbcStatement(
                        text.substring(0, ixCol) + "?",
                        Map.of(),
                        Map.of(1, new SqlParam(parm, type)));
            }
            throw new InvalidSqlParameter(parm, iFields, oFields);
        }
        String parm = text.substring(ixCol + 1, ixEnd);

        class MapStore implements ApiStore<JdbcStatement> {
            private final Map<Integer, SqlParam> iMap = new LinkedHashMap<>();
            private final Map<Integer, SqlParam> oMap = new LinkedHashMap<>();
            private int k=1;

            public void push(String parm) {
                if (parm.isEmpty())
                    return;
                SqlDataType type = ioFields.get(parm);
                if (type != null) {
                    iMap.put(k, new SqlParam(parm, type));
                    oMap.put(k++, new SqlParam(parm, type));
                    return;
                }
                type = iFields.get(parm);
                if (type != null) {
                    iMap.put(k++, new SqlParam(parm, type));
                    return;
                }
                type = oFields.get(parm);
                if (type != null) {
                    oMap.put(k++, new SqlParam(parm, type));
                    return;
                }
                throw new InvalidSqlParameter(parm, iFields, oFields);
            }

            public JdbcStatement close(String s) {
                return new JdbcStatement(s, iMap, oMap);
            }
        }
        val store = new MapStore();
        store.push(parm);
        return deepScan(text, ixCol, ixEnd, store);
    }

    public static JdbcStatement replacePlaceholder(String text, Map<String, SqlDataType> iFields, Map<String, SqlDataType> oFields) {
        int ixCol = text.indexOf(':');
        if (ixCol<0) {
            // there are no parameters
            return new JdbcStatement(text, Map.of(), Map.of());
        }
        int ixEnd = indexOf(text, ixCol + 1);
        if (ixEnd<0) {
            // only one parameter at end-of-text (no space)
            String parm = text.substring(ixCol + 1);
            SqlDataType type = iFields.get(parm);
            if (type==null) {
                type = oFields.get(parm);
                if (type == null) {
                    throw new InvalidSqlParameter(parm, iFields, oFields);
                } else {
                    return new JdbcStatement(
                            text.substring(0, ixCol) + "?",
                            Map.of(),
                            Map.of(1, new SqlParam(parm, type)));
                }
            } else {
                return new JdbcStatement(
                        text.substring(0, ixCol)+"?",
                        Map.of(1, new SqlParam(parm, type)),
                        Map.of());
            }
        }
        String parm = text.substring(ixCol + 1, ixEnd);

        class MapStore implements ApiStore<JdbcStatement> {
            private final Map<Integer, SqlParam> iMap = new LinkedHashMap<>();
            private final Map<Integer, SqlParam> oMap = new LinkedHashMap<>();
            private int k=1;

            public void push(String parm) {
                if (parm.isEmpty())
                    return;
                SqlDataType type = iFields.get(parm);
                if (type==null) {
                    type = oFields.get(parm);
                    if (type==null) {
                        throw new InvalidSqlParameter(parm, iFields, oFields);
                    } else {
                        oMap.put(k++, new SqlParam(parm, type));
                    }
                } else {
                    iMap.put(k++, new SqlParam(parm, type));
                }
            }

            public JdbcStatement close(String s) {
                return new JdbcStatement(s, iMap, oMap);
            }
        }
        val store = new MapStore();
        store.push(parm);
        return deepScan(text, ixCol, ixEnd, store);
    }
    @NotNull
    public static Map<Integer, SqlParam> mapPlaceholder(String text, Map<String, SqlDataType> fields) {
        int ixCol = text.indexOf(':');
        if (ixCol<0) {
            // there are no parameters
            return Map.of();
        }
        int ixEnd = indexOf(text, ixCol + 1);
        if (ixEnd<0) {
            // only one parameter at end-of-text (no space)
            String parm = text.substring(ixCol + 1);
            SqlDataType type = fields.get(parm);
            if (type==null)
                throw new IllegalArgumentException("Invalid SQL parameter "+parm );
            return Map.of(1, new SqlParam(parm, type));
        }
        String parm = text.substring(ixCol + 1, ixEnd);
        SqlDataType type = fields.get(parm);
        if (type==null)
            throw new IllegalArgumentException("Invalid SQL parameter "+parm );
        val map = new LinkedHashMap<Integer, SqlParam>();
        int k=1;
        map.put(k++, new SqlParam(parm, type));
        while(ixEnd<text.length()) {
            ixCol = text.indexOf(':', ixEnd);
            if (ixCol<0) {
                // there are no more parameters
                return map;
            }
            ixEnd = indexOf(text, ixCol + 1);
            if (ixEnd<0) {
                // parameter at end-of-text (no space)
                parm = text.substring(ixCol + 1);
                type = fields.get(parm);
                if (type==null)
                    throw new IllegalArgumentException("Invalid SQL parameter "+parm );
                map.put(k, new SqlParam(parm, type));
                return map;
            }
            parm = text.substring(ixCol + 1, ixEnd);
            type = fields.get(parm);
            if (type==null)
                throw new IllegalArgumentException("Invalid SQL parameter "+parm );
            map.put(k++, new SqlParam(parm, type));
        }

        return Map.of();
    }

    private static int indexOf(String text, int i) {
        int k = -1;
        for(val c: BREAK_PARMS) {
            int ix = text.indexOf(c, i);
            if (ix >= 0 && (k<0 || ix<k)) {
                k = ix;
            }
        }
        return k;
    }

    public static String capitalize(@NotNull String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    @Setter @Getter
    @AllArgsConstructor
    public static class SqlStatement {
        private final String text;
        private final Map<Integer, SqlParam> map;
    }

    public static String getterOf(SqlParam parm) {
        String name = parm.getName();
        int kDot = name.indexOf('.');
        if (kDot < 0) {
            return parm.getType().getterPrefix() + capitalize(name);
        }
        String ante = name.substring(0, kDot);
        String post = name.substring(kDot+1);
        return "get" + capitalize(ante) + "()." + getterOf(new SqlParam(post, parm.getType()));
    }
    public static String setterOf(String name) {
        int kDot = name.indexOf('.');
        if (kDot < 0) {
            return "set" + capitalize(name);
        }
        String ante = name.substring(0, kDot);
        String post = name.substring(kDot+1);
        return "get" + capitalize(ante) + "()." + setterOf(post);
    }

    public static String normalizeName(String name) {
        int kDot = name.lastIndexOf('.');
        if (kDot<0)
            return name;
        return name.substring(kDot+1);
    }

}
