package io.github.epi155.emsql.pojo;

import io.github.epi155.emsql.api.OutFieldsModel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class ComAreaDef extends ComAreaStd implements OutFieldsModel {
    private List<String> fields = new ArrayList<>();
}
