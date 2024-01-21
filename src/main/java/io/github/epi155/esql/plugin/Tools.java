package io.github.epi155.esql.plugin;

import io.github.epi155.esql.plugin.sql.JdbcStatement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class Tools {
    private Tools() {}
    public static List<String> camelToSnake(String[] as) {
        List<String> result = new ArrayList<>(as.length);
        for(val s: as) {
            result.add(camelToSnake(s));
        }
        return result;
    }

    public static String camelToSnake(@NotNull String s) {
        char[] ac = s.toCharArray();
        StringBuilder sb = new StringBuilder();
        if (! Character.isLowerCase(ac[0]))
            throw new IllegalArgumentException("Invalid camel field name");
        boolean wasUpper = false;
        boolean wasDigit = false;
        for (val c: ac) {
            boolean isUpper = Character.isUpperCase(c);
            boolean isDigit = Character.isDigit(c);
            if (isUpper && !wasUpper) {
                sb.append("_");
                sb.append(Character.toLowerCase(c));
            } else if (isDigit && !wasDigit) {
                sb.append("_");
                sb.append(c);
            } else {
                sb.append(c);
            }
            wasUpper = isUpper;
            wasDigit = isDigit;
        }
        return sb.toString();
    }

    public static String oneLine(@NotNull String text) {
        StringBuilder sb = new StringBuilder();
        char[] ac = text.toCharArray();
        boolean wasBreak = isBreak(ac[0]);
        for (val c: ac) {
            boolean isBreak = isBreak(c);
            if (!isBreak) {
                sb.append(c);
            } else if (!wasBreak) {
                sb.append(' ');
            }
            wasBreak = isBreak;
        }
        return sb.toString().replaceAll("\\s\\s+", " ").trim();
    }

    private static boolean isBreak(char c) {
        return c=='\n' || c=='\r';
    }
    @NotNull
    public static SqlStatement replacePlaceholder(String text, Map<String, SqlEnum> fields) {
        log.debug("Query: {}", text);
        int ixCol = text.indexOf(':');
        if (ixCol<0) {
            // there are no parameters
            return new SqlStatement(text, Map.of());
        }
        int ixEnd = indexOf(text, ixCol + 1, ' ', ',');
        if (ixEnd<0) {
            // only one parameter at end-of-text (no space)
            String parm = text.substring(ixCol + 1);
            SqlEnum type = fields.get(parm);
            if (type==null)
                throw new InvalidSqlParameter(parm, fields);
            return new SqlStatement(text.substring(0, ixCol)+"?", Map.of(1, new SqlParam(parm, type)));
        }
        String parm = text.substring(ixCol + 1, ixEnd);

        class MapStore {
            private final Map<Integer, SqlParam> iMap = new LinkedHashMap<>();
            private int k=1;

            public void push(String parm) {
                SqlEnum type = fields.get(parm);
                if (type==null) {
                       throw new InvalidSqlParameter(parm, fields);
                } else {
                    iMap.put(k++, new SqlParam(parm, type));
                }
            }

            public SqlStatement close(String s) {
                return new SqlStatement(s, iMap);
            }
        }
        val store = new MapStore();
        store.push(parm);
        val sb = new StringBuilder();
        sb.append(text, 0, ixCol).append('?');
        while(ixEnd<text.length()) {
            int ixOld = ixEnd;
            ixCol = text.indexOf(':', ixEnd);
            if (ixCol<0) {
                // there are no more parameters
                sb.append(text.substring(ixOld));
                return store.close(sb.toString());
            }
            ixEnd = indexOf(text, ixCol + 1, ' ', ',');
            if (ixEnd<0) {
                // parameter at end-of-text (no space)
                parm = text.substring(ixCol + 1);
                store.push(parm);
                sb.append(text, ixOld, ixCol).append('?');
                return store.close(sb.toString());
            }
            parm = text.substring(ixCol + 1, ixEnd);
            store.push(parm);
            sb.append(text, ixOld, ixCol).append('?');
        }

        return new SqlStatement(text, Map.of());    // dead exit point
    }
    @NotNull
    public static JdbcStatement replacePlaceholder(String text, Map<String, SqlEnum> iFields, Map<String, SqlEnum> oFields) {
        log.debug("Query: {}", text);
        int ixCol = text.indexOf(':');
        if (ixCol<0) {
            // there are no parameters
            return new JdbcStatement(text, Map.of(), Map.of());
        }
        int ixEnd = indexOf(text, ixCol + 1, ' ', ',');
        if (ixEnd<0) {
            // only one parameter at end-of-text (no space)
            String parm = text.substring(ixCol + 1);
            SqlEnum type = iFields.get(parm);
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

        class MapStore {
            private final Map<Integer, SqlParam> iMap = new LinkedHashMap<>();
            private final Map<Integer, SqlParam> oMap = new LinkedHashMap<>();
            private int k=1;

            public void push(String parm) {
                SqlEnum type = iFields.get(parm);
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

        val sb = new StringBuilder();
        store.push(parm);
        sb.append(text, 0, ixCol).append('?');
        while(ixEnd<text.length()) {
            int ixOld = ixEnd;
            ixCol = text.indexOf(':', ixEnd);
            if (ixCol<0) {
                // there are no more parameters
                sb.append(text.substring(ixOld));
                return store.close(sb.toString());
            }
            ixEnd = indexOf(text, ixCol + 1, ' ', ',');
            if (ixEnd<0) {
                // parameter at end-of-text (no space)
                parm = text.substring(ixCol + 1);
                store.push(parm);
                sb.append(text, ixOld, ixCol).append('?');
                return store.close(sb.toString());
            }
            parm = text.substring(ixCol + 1, ixEnd);
            store.push(parm);
            sb.append(text, ixOld, ixCol).append('?');
        }

        return new JdbcStatement(text, Map.of(), Map.of());     // dead exit point
    }
    @NotNull
    public static Map<Integer, SqlParam> mapPlaceholder(String text, Map<String, SqlEnum> fields) {
        log.debug("Query: {}", text);
        int ixCol = text.indexOf(':');
        if (ixCol<0) {
            // there are no parameters
            return Map.of();
        }
//        int ixEnd = text.indexOf(' ', ixCol + 1);
        int ixEnd = indexOf(text, ixCol + 1, ' ', ',');
        if (ixEnd<0) {
            // only one parameter at end-of-text (no space)
            String parm = text.substring(ixCol + 1);
            SqlEnum type = fields.get(parm);
            if (type==null)
                throw new IllegalArgumentException("Invalid SQL parameter "+parm );
            return Map.of(1, new SqlParam(parm, type));
        }
        String parm = text.substring(ixCol + 1, ixEnd);
        SqlEnum type = fields.get(parm);
        if (type==null)
            throw new IllegalArgumentException("Invalid SQL parameter "+parm );
        val map = new LinkedHashMap<Integer, SqlParam>();
        int k=1;
        map.put(k++, new SqlParam(parm, type));
        while(ixEnd<text.length()) {
            int ixOld = ixEnd;
            ixCol = text.indexOf(':', ixEnd);
            if (ixCol<0) {
                // there are no more parameters
                return map;
            }
            ixEnd = indexOf(text, ixCol + 1, ' ', ',');
            if (ixEnd<0) {
                // parameter at end-of-text (no space)
                parm = text.substring(ixCol + 1);
                type = fields.get(parm);
                if (type==null)
                    throw new IllegalArgumentException("Invalid SQL parameter "+parm );
                map.put(k++, new SqlParam(parm, type));
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

    private static int indexOf(String text, int i, char ...cs) {
        int k = -1;
        for(val c: cs) {
            int ix = text.indexOf(c, i);
            if (ix >= 0) {
                if (k<0 || ix<k) {
                    k = ix;
                }
            }
        }
        return k;
    }

    public static String capitalize(@NotNull String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    @Data @AllArgsConstructor
    public static class SqlStatement {
        private final String text;
        private final Map<Integer, SqlParam> map;
    }
}
