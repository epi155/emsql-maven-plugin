package io.github.epi155.emsql.api;

import java.util.List;

public interface InOutFieldsModel extends OutFieldsModel {
    List<String> getFields();
    void setFields(List<String> it);

    void setReflect(boolean b);
    void setDelegate(boolean b);
}
