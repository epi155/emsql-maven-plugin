package io.github.epi155.emsql.plugin;

import io.github.epi155.emsql.api.PrintModel;

import java.io.PrintWriter;
import java.io.Writer;
import java.nio.CharBuffer;

public class IndentPrintWriter implements PrintModel {
    private static final String ELSE = "} else {";
    private final PrintWriter pw;
    private final String tab;
    private int indent = 0;
    private boolean firstComma;

    public IndentPrintWriter(int step, Writer writer) {
        this.tab = CharBuffer.allocate(step).toString().replace('\u0000', ' ');
        this.pw = new PrintWriter(writer);
    }

    @Override
    public void printf(String format, Object... objects) {
        indent();
        pw.printf(format, objects);
    }

    @Override
    public void putf(String format, Object... objects) {
        pw.printf(format, objects);
    }

    private void indent() {
        for (int k = 0; k < indent; k++) {
            pw.print(tab);
        }
    }

    @Override
    public void more() {
        indent++;
    }

    @Override
    public void println() {
        pw.println();
    }

    @Override
    public void less() {
        if (indent > 0) indent--;
    }

    @Override
    public void ends() {
        if (indent > 0) {
            indent--;
            indent();
            pw.println("}");
        }
    }

    @Override
    public void orElse() {
        if (indent > 0) {
            indent--;
            indent();
            pw.println(ELSE);
            indent++;
        } else {
            pw.println(ELSE);
        }
    }

    @Override
    public void commaReset() {
        this.firstComma = true;
    }

    @Override
    public void putln() {
        pw.println();
    }

    @Override
    public void commaLn() {
        if (firstComma) {
            firstComma = false;
        } else {
            pw.println(",");
        }
    }

    @Override
    public void closeParenthesisLn() {
        pw.println(")");
    }
}
