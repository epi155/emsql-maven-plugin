package io.github.epi155.emsql.commons;

import io.github.epi155.emsql.api.InOutFieldsModel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class ComAreaDef extends ComAreaStd implements InOutFieldsModel {
    private List<String> fields = new ArrayList<>();
}
