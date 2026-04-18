package io.github.epi155.emsql.commons;

import io.github.epi155.emsql.api.InOutFieldsModel;
import io.github.epi155.emsql.api.InputModel;
import io.github.epi155.emsql.api.OutputModel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class ComAreaDef implements InputModel, OutputModel, InOutFieldsModel {
    private List<String> fields = new ArrayList<>();
}