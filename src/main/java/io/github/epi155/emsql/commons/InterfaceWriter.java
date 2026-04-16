package io.github.epi155.emsql.commons;

import io.github.epi155.emsql.api.InvalidQueryException;
import io.github.epi155.emsql.api.PrintModel;

public interface InterfaceWriter {
    String signature();

    void writeDelegate(PrintModel ipw, String iName, boolean java7) throws InvalidQueryException;

    void writeStandard(PrintModel ipw, String iName);

    boolean isDelegate();
}
