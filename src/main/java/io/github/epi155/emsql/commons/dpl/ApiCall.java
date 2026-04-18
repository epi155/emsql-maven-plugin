package io.github.epi155.emsql.commons.dpl;

import io.github.epi155.emsql.api.FieldsModel;
import org.jetbrains.annotations.NotNull;

public interface ApiCall {
    @NotNull String getExecSql();

    FieldsModel getInputOutput();

    FieldsModel getOutput();
}
