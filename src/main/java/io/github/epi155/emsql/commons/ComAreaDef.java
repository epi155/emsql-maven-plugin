package io.github.epi155.emsql.commons;

import io.github.epi155.emsql.api.FieldsModel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class ComAreaDef implements FieldsModel {
    private List<String> fields = new ArrayList<>();
}