package io.github.epi155.emsql.commons;

import io.github.epi155.emsql.api.InputModel;
import io.github.epi155.emsql.api.OutputModel;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ComAreaStd implements InputModel, OutputModel {
    /** DO NOT USE, reduces performance by 14% */ private boolean reflect;
    /** ?? */ private boolean delegate;
}
