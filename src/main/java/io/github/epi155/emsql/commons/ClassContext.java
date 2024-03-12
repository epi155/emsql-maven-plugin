package io.github.epi155.emsql.commons;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import io.github.epi155.emsql.api.TypeModel;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;

public interface ClassContext {
    void put(String key, TypeModel type);

    void incMethods();

    void flush(PrintModel pw);

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

    void delegateRequestFields(PrintModel ipw, Map<String, SqlDataType> sp);

    void delegateResponseFields(PrintModel ipw, Collection<SqlParam> sp);

    String optional();

    void newLine(PrintModel ipw, boolean tune);
}
