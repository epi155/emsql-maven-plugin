package io.github.epi155.emsql.api;

import java.util.List;

public interface OutFieldsModel extends OutputModel {
    List<String> getFields();

    void setFields(List<String> it);

    void setReflect(boolean b);

    void setDelegate(boolean b);
}
