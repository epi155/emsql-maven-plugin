package io.github.epi155.esql.plugin;

import io.github.epi155.esql.plugin.sql.SqlApi;
import io.github.epi155.esql.plugin.td.TdProgrammingModeEnum;
import io.github.epi155.esql.plugin.td.TdSqlEnum;
import io.github.epi155.esql.plugin.td.dml.*;
import io.github.epi155.esql.plugin.td.dql.TdCursorForSelect;
import io.github.epi155.esql.plugin.td.dql.TdSelectList;
import io.github.epi155.esql.plugin.td.dql.TdSelectOptional;
import io.github.epi155.esql.plugin.td.dql.TdSelectSingle;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

@Mojo(name = "generate",
        defaultPhase = LifecyclePhase.GENERATE_SOURCES,
        requiresDependencyResolution = ResolutionScope.COMPILE,
        requiresDependencyCollection = ResolutionScope.COMPILE
)
@Slf4j
public class SqlMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project.build.directory}/generated-sources/esql",
            property = "maven.esql.generateDirectory", required = true)
    @Setter
    private File generateDirectory;

    @Parameter(defaultValue = "${project.build.resources[0].directory}",
            property = "maven.esql.settingsDirectory", required = true)
    @Setter
    private File settingsDirectory;

    @Parameter(required = true)
    @Setter
    private String[] modules;

    @Parameter(defaultValue = "true",
            property = "maven.esql.debugCode", required = true)
    @Setter
    private Boolean debugCode;

    @Parameter(defaultValue = "${project}", readonly = true)
    @Setter
    private MavenProject project;
    @Parameter(defaultValue = "${plugin}", readonly = true, required = true) @Setter
    protected org.apache.maven.plugin.descriptor.PluginDescriptor plugin;

    /**
     * If set to true (default), adds target directory as a compile source root
     * of this Maven project.
     */
    @SuppressWarnings("CanBeFinal")
    @Parameter(defaultValue = "true", property = "maven.esql.addCompileSourceRoot")
    @Setter
    private boolean addCompileSourceRoot = true;

    /**
     * If set to true, adds target directory as a test compile source root of
     * this Maven project. Default value is false.
     */
    @SuppressWarnings("CanBeFinal")
    @Parameter(defaultValue = "false", property = "maven.esql.addTestCompileSourceRoot")
    @Setter
    private boolean addTestCompileSourceRoot = false;
    private final Set<String> classLogbook = new HashSet<>();

    @Override
    public void execute() throws MojoExecutionException {
        Logger.getLogger("org.yaml.snakeyaml.introspector").setLevel(Level.SEVERE);

        Constructor c1 = new Constructor(SqlApi.class, new LoaderOptions());
        c1.addTypeDescription(new TdSqlEnum());
        c1.addTypeDescription(new TdProgrammingModeEnum());
        c1.addTypeDescription(new TdSelectOptional());
        c1.addTypeDescription(new TdSelectSingle());
        c1.addTypeDescription(new TdSelectList());
        c1.addTypeDescription(new TdCursorForSelect());
        c1.addTypeDescription(new TdDelete());
        c1.addTypeDescription(new TdUpdate());
        c1.addTypeDescription(new TdInsert());
        c1.addTypeDescription(new TdDeleteBatch());
        c1.addTypeDescription(new TdUpdateBatch());
        c1.addTypeDescription(new TdInsertBatch());
        c1.addTypeDescription(new TdInsertReturnGeneratedKeys());
        c1.addTypeDescription(new TdInsertReturningInto());
//        c1.addTypeDescription(new TdCursorForUpdate());   // da rivedere

        Yaml apiYaml = new Yaml(c1);

        try {
            /*-----------------*/
            loadSqlApi(apiYaml);
            /*-----------------*/
            setupMavenPaths(generateDirectory);

            log.info("Done.");
        } catch (Exception e) {
            log.error(e.toString());
            throw new MojoExecutionException("Failed to execute plugin", e);
        }
    }

    private void setupMavenPaths(File srcMain) {
        if (addCompileSourceRoot) {
            project.addCompileSourceRoot(srcMain.getPath());
        }
        if (addTestCompileSourceRoot) {
            project.addTestCompileSourceRoot(srcMain.getPath());
        }
    }

    private void loadSqlApi(Yaml yaml) throws IOException, MojoExecutionException {
        val cx = MojoContext.builder()
                .sourceDirectory(generateDirectory.getPath())
                .group(plugin.getGroupId())
                .artifact(plugin.getArtifactId())
                .version(plugin.getVersion())
                .debug(debugCode)
                .build();

        for (val module : modules) {
            log.info("Loading API from {}", module);
            File apiFile = new File(settingsDirectory, module);
            if (!apiFile.exists()) {
                log.warn("Setting {} does not exist, ignored.", module);
                continue;
            }
            try (InputStream inputStream = Files.newInputStream(apiFile.toPath())) {
                SqlApi api = yaml.load(inputStream);
                makeDirectory(generateDirectory, api.getPackageName());
                /*----------------*/
                generateApi(cx, api);
                /*----------------*/
            }
        }
    }

    private void generateApi(MojoContext cx, SqlApi api) throws MojoExecutionException, FileNotFoundException {
        val classFullName = api.getPackageName()+"."+api.getClassName();
        if (! classLogbook.add(classFullName)) {
            throw new MojoExecutionException("Class <" + classFullName + "> duplicated");
        }
        /*-----------*/
        api.create(cx);
        /*-----------*/
    }

public void makeDirectory(@NotNull File base, @Nullable String packg) throws MojoExecutionException {
        if (!base.exists()) {
            log.debug("Source Directory <{}> does not exist, creating", base.getAbsolutePath());
            if (!base.mkdirs())
                throw new MojoExecutionException("Error creating Source Directory <" + base.getName() + ">");
        }
        if (!base.isDirectory()) throw new MojoExecutionException("Source Directory <" + base.getName() + "> is not a Directory");
        if (packg == null) return;
        StringTokenizer st = new StringTokenizer(packg, ".");
        String cwd = base.getAbsolutePath();
        while (st.hasMoreElements()) {
            val d = st.nextElement();
            val tmp = cwd + File.separator + d;
            mkdir(tmp);
            cwd = tmp;
        }
    }
    private static void mkdir(String tmp) throws MojoExecutionException {
        val f = new File(tmp);
        if ((!f.exists()) && (!f.mkdir()))
            throw new MojoExecutionException("Cannot create directory <" + tmp + ">");
    }

}
