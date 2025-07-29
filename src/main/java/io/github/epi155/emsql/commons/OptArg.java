package io.github.epi155.emsql.commons;

import io.github.epi155.emsql.api.SqlDataType;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(of = {"name", "ndx"})
@RequiredArgsConstructor
public class OptArg {
    private final String name;
    private final int ndx;
    private String simpleName;
    private int ord;
    private SqlDataType sql;

    public String normalizedName() {
        return ord<=1 ? simpleName : simpleName + ord;
    }
}
