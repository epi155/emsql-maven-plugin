package io.github.epi155.emsql.commons.dpl;

import io.github.epi155.emsql.api.OutFieldsModel;

public interface ApiInline {
    String getExecSql();

    OutFieldsModel getOutput();
}
