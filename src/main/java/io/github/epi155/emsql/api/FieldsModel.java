package io.github.epi155.emsql.api;

import java.util.List;

public interface FieldsModel {
    List<String> getFields();
    void setFields(List<String> it);
}