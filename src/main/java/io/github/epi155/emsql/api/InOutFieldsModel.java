package io.github.epi155.emsql.api;

import java.util.List;

public interface InOutFieldsModel extends OutFieldsModel {
    @Override List<String> getFields();
    @Override void setFields(List<String> it);
}