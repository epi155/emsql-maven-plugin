package io.github.epi155.emsql.api;

import org.apache.maven.plugin.MojoExecutionException;

public interface MethodModel {
    String getMethodName();
    PerformModel getPerform();

    void setMethodName(String it);
    void setPerform(PerformModel it);

    void writeCode(PrintModel pm, int count) throws MojoExecutionException;
}
