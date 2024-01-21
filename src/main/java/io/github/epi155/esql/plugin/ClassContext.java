package io.github.epi155.esql.plugin;

import lombok.Getter;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

public class ClassContext {
    @Getter
    private final boolean debug;
    private final SortedSet<String> importSet = new TreeSet<>();

    public ClassContext(MojoContext cx) {
        this.debug = cx.debug;
        importSet.add("java.sql.*");
        if (debug) {
            importSet.add("io.github.epi155.esql.runtime.ESQL");
            importSet.add("java.util.List");
            importSet.add("java.util.ArrayList");
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
