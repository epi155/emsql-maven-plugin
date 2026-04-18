package io.github.epi155.emsql.commons.dpl;

import io.github.epi155.emsql.api.FieldsModel;

public interface ApiInline {
    String getExecSql();

    FieldsModel getOutput();
}
