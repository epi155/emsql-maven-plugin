package io.github.epi155.emsql.commons.dpl;

import java.util.List;

public interface ApiInline {
    String getExecSql();

    List<String> getOutFields();
}
