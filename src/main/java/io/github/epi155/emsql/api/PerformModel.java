package io.github.epi155.emsql.api;

import org.apache.maven.plugin.MojoExecutionException;

import java.util.Map;

public interface PerformModel {
    String getExecSql();
    Integer getTimeout();
    boolean isTune();
    void setExecSql(String it);
    void setTimeout(Integer it);
    void setTune(boolean it);

    default InputModel getInput() { return null; }
    default OutputModel getOutput() { return null; }


    void writeCode(PrintModel ipw, Map<String, SqlDataType> fields, String kPrg) throws MojoExecutionException ;
}
