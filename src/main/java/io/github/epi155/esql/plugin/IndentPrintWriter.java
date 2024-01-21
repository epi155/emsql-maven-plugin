package io.github.epi155.esql.plugin;

import java.io.PrintWriter;
import java.io.Writer;
import java.nio.CharBuffer;

public class IndentPrintWriter {
    private static final String ELSE = "} else {";
    private final PrintWriter pw;
    private final String tab;
    private int indent = 0;

    public IndentPrintWriter(int step, Writer writer) {
        this.tab = CharBuffer.allocate(step).toString().replace('\u0000', ' ');
        this.pw = new PrintWriter(writer);
    }

    public void printf(String format, Object...objects) {
        indent();
        pw.printf(format, objects);
    }
    public void putf(String format, Object...objects) {
        pw.printf(format, objects);
    }

    private void indent() {
        for(int k=0; k<indent; k++) {
            pw.print(tab);
        }
    }

    public void more() {
        indent++;
    }

    public void println() {
        pw.println();
    }

    public void less() {
        if (indent>0) indent--;
    }
    public void ends() {
        if (indent>0) {
            indent--;
            indent();
            pw.println("}");
        }
    }
    public void orElse() {
        if (indent>0) {
            indent--;
            indent();
            pw.println(ELSE);
            indent++;
        } else {
            pw.println(ELSE);
        }
    }

    public void commaLn() {
        pw.println(",");
    }

    public void closeParenthesisLn() {
        pw.println(")");
    }
}
