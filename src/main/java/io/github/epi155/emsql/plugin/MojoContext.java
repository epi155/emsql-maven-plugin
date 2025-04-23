package io.github.epi155.emsql.plugin;

import io.github.epi155.emsql.api.PluginContext;
import io.github.epi155.emsql.commons.SqlAction;
import io.github.epi155.emsql.commons.SqlParam;
import io.github.epi155.emsql.spi.ParserProvider;
import io.github.epi155.emsql.spi.SqlParser;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

@Getter
public class MojoContext implements PluginContext {
    public final String sourceDirectory;
    public final String group;
    public final String artifact;
    public final String version;
    public final boolean debug;
    public final boolean java7;
    public final SqlParser parser;
    private int nmClasses = 0;
    private int nmMethods = 0;

    public MojoContext(
            @NonNull String sourceDirectory,
            String group,
            String artifact,
            String version,
            boolean debug,
            boolean java7,
            String parserName
    ) {
        this.sourceDirectory = sourceDirectory;
        this.group = group;
        this.artifact = artifact;
        this.version = version;
        this.debug = debug;
        this.java7 = java7;

        ServiceLoader<ParserProvider> providers = ServiceLoader.load(ParserProvider.class);
        if (parserName==null) {
            parser = providers.findFirst().map(ParserProvider::create).orElse(null);
        } else {
            parser = findByName(parserName, providers);
        }
    }

    private static SqlParser findByName(String name, @NotNull Iterable<ParserProvider> providers) {
        for (ParserProvider provider: providers) {
            if (name.equals(provider.name())) {
                return provider.create();
            }
        }
        throw new IllegalArgumentException("ParserProvider " + name+ " not found.");
    }

    public void validate(String query, Class<? extends SqlAction> claz, Map<Integer, SqlParam> parameters) {
        if (parser==null) return;
        List<Mul> muls = replacer(parameters);
        if (!muls.isEmpty()) {
            query = expandQueryParameters(query, muls);
        }
        parser.validate(query, claz);
    }

    private static String expandQueryParameters(String query, List<Mul> muls) {
        for(Mul mul: muls) {
            query = mul.replace(query);
        }
        return query;
    }

    private static List<Mul> replacer(Map<Integer, SqlParam> parameters) {
        List<Mul> reps = new LinkedList<>();
        parameters.forEach((k,v) -> {
            if (!v.getType().isScalar()) {
                reps.add(new Mul(k, v.getType().columns()));
            }
        });
        return reps;
    }

    public void incClasses() { nmClasses++; }
    public void incMethods() { nmMethods++; }

    private static class Mul {
        private final int nth;
        private final int cols;

        private Mul(int nth, int cols) {
            this.nth = nth;
            this.cols = cols;
        }

        public String replace(String query) {
            String placeholder = "[#" + nth + "]";
            int ks = query.indexOf(placeholder);
            if (ks<0)
                return query;   // dead branch
            StringBuilder sb = new StringBuilder();
            sb.append(query, 0, ks);
            if (cols==1) {
                sb.append('?');
            } else {
                sb.append('(');
                for(int kc=1; kc<=cols; kc++) {
                    sb.append('?');
                    if (kc< cols)sb.append(',');
                }
                sb.append(')');
            }
            sb.append(query.substring(ks+placeholder.length()));
            return sb.toString();
        }
    }

}
