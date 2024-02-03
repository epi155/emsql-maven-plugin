package io.github.epi155.emsql.plugin;

import lombok.Builder;
import lombok.NonNull;

@Builder
public class MojoContext {
    @NonNull
    public final String sourceDirectory;
    public final String group;
    public final String artifact;
    public final String version;
    public final boolean debug;
    public final boolean java7;
}