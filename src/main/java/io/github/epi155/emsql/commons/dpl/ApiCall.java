package io.github.epi155.emsql.commons.dpl;

import io.github.epi155.emsql.api.InOutFieldsModel;
import io.github.epi155.emsql.api.OutFieldsModel;
import org.jetbrains.annotations.NotNull;

public interface ApiCall {
    @NotNull String getExecSql();

    InOutFieldsModel getInputOutput();

    OutFieldsModel getOutput();
}
