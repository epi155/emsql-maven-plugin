package io.github.epi155.esql.plugin;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ComAreaStd implements ComAttribute {
    /** DO NOT USE, reduces performance by 14% */ private boolean reflect;
    /** ?? */ private boolean delegate;
}
