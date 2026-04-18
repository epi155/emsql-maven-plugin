package io.github.epi155.emsql.commons.dpl;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ApiCall {
    @NotNull String getExecSql();

    List<String> getInOutFields();

    List<String> getOutFields();
}
