package io.github.epi155.emsql.plugin;

import io.github.epi155.emsql.api.PluginContext;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder @Getter
public class MojoContext implements PluginContext {
    @NonNull
    public final String sourceDirectory;
    public final String group;
    public final String artifact;
    public final String version;
    public final boolean debug;
    public final boolean java7;
    @Builder.Default
    private int nmClasses = 0;
    @Builder.Default
    private int nmMethods = 0;

    public void incClasses() { nmClasses++; }
    public void incMethods() { nmMethods++; }
}
