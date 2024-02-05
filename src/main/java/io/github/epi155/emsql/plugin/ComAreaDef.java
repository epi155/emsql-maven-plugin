package io.github.epi155.emsql.plugin;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class ComAreaDef extends ComAreaStd {
    private List<String> fields = new ArrayList<>();
}
