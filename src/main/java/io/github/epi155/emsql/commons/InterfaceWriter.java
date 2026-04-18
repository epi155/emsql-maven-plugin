package io.github.epi155.emsql.commons;

import io.github.epi155.emsql.api.PrintModel;

public interface InterfaceWriter {
    String signature();

    void writeStandard(PrintModel ipw, String iName);
}
