package io.github.epi155.emsql.commons;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ComAreaLst implements ComAttribute {
    /**
     * DO NOT USE, reduces performance by 14%
     */
    private boolean reflect;
}
