package io.github.epi155.emsql.commons;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;

public interface ClassContext {
    void incMethods();

    boolean isDebug();

    void writeImport(PrintWriter wr);

    void add(String s);

    Map<String, SqlDataType> getFields();

    void traceParameterBegin(PrintModel ipw);

    void traceParameterEnds(PrintModel ipw);

    void addAll(Collection<String> requires);

    void declareTuner(PrintModel ipw);

    String supplier();

    String consumer();

    String optional();

    default void newLine(PrintModel ipw, boolean tune) {
    }

    void validate(String query, Class<? extends SqlAction> claz, Map<Integer, SqlParam> parameters);

    String inPrepare(String name, Collection<SqlParam> values, InputMask mask);
    String outPrepare(String name, Collection<SqlOutParam> values, OutputMask mask);

    void writeInterfaces(PrintModel pw);

    String deduplicate(String name, InterfaceWriter iw);
}
