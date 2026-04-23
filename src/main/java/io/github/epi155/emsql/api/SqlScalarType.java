package io.github.epi155.emsql.api;

public interface SqlScalarType extends SqlDataType {
    void rsGetValue(PrintModel ipw, int k);
    void csGetValue(PrintModel ipw, int k);
    void registerOut(PrintModel ipw, int k);
}
