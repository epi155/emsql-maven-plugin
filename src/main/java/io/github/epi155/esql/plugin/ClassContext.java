package io.github.epi155.esql.plugin;

import io.github.epi155.esql.plugin.sql.SqlEnum;
import lombok.Getter;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class ClassContext {
    @Getter
    private final boolean debug;
    private final SortedSet<String> importSet = new TreeSet<>();
    @Getter
    private final Map<String, SqlEnum> fields;

    public ClassContext(MojoContext cx, Map<String, SqlEnum> declare) {
        this.debug = cx.debug;
        this.fields = declare;
        importSet.add("java.sql.*");
        if (debug) {
            importSet.add("io.github.epi155.esql.runtime.ESqlTrace");
        }
    }

    public void writeImport(PrintWriter pw) {
        importSet.forEach(it -> pw.printf("import %s;%n", it));
    }

    public void add(String s) {
        importSet.add(s);
    }

    public void addAll(Collection<String> requires) {
        importSet.addAll(requires);
    }

}
