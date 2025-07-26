package io.github.epi155.emsql.plugin;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.CodeProvider;
import io.github.epi155.emsql.api.InvalidQueryException;
import io.github.epi155.emsql.plugin.td.*;
import io.github.epi155.emsql.plugin.td.dml.*;
import io.github.epi155.emsql.plugin.td.dpl.*;
import io.github.epi155.emsql.plugin.td.dql.*;
import io.github.epi155.emsql.provider.ProviderEnum;
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
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.ProviderNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Mojo(name = "generate",
        defaultPhase = LifecyclePhase.GENERATE_SOURCES,
        requiresDependencyResolution = ResolutionScope.COMPILE,
        requiresDependencyCollection = ResolutionScope.COMPILE
)
@Slf4j
public class SqlMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project.build.directory}/generated-sources/emsql",
            property = "maven.emsql.generate-directory", required = true)
    @Setter
    private File generateDirectory;

    @Parameter(defaultValue = "${project.build.resources[0].directory}",
            property = "maven.emsql.config-directory", required = true)
    @Setter
    private File configDirectory;

    @Parameter(required = true)
    @Setter
    private String[] modules;

    @Parameter(defaultValue = "true",
            property = "maven.emsql.debug-code", required = true)
    @Setter
    private Boolean debugCode;

    /**
     * Set codeGenerator provider. Available values are: <b>Pojo</b>, <b>Spring</b> (case insensitive)
     */
    @Parameter(property = "maven.emsql.provider")
    @Setter
    private String provider;

    @Parameter(defaultValue = "false",
            property = "maven.emsql.java7", required = true)
    @Setter
    private boolean java7;

    @Parameter(property = "maven.emsql.parser-provider")
    @Setter
    private String parserProvider;

    @Parameter(defaultValue = "${project}", readonly = true)
    @Setter
    private MavenProject project;
    @Parameter(defaultValue = "${plugin}", readonly = true, required = true)
    @Setter
    protected org.apache.maven.plugin.descriptor.PluginDescriptor plugin;

    /**
     * If set to true (default), adds target directory as a compile source root
     * of this Maven project.
     */
    @SuppressWarnings("CanBeFinal")
    @Parameter(defaultValue = "true", property = "maven.emsql.add-compile-source-root")
    @Setter
    private boolean addCompileSourceRoot = true;

    /**
     * If set to true, adds target directory as a test compile source root of
     * this Maven project. Default value is false.
     */
    @SuppressWarnings("CanBeFinal")
    @Parameter(defaultValue = "false", property = "maven.emsql.add-test-compile-source-root")
    @Setter
    private boolean addTestCompileSourceRoot = false;
    private final Set<String> classLogbook = new HashSet<>();

    public static final ThreadLocal<MapContextImpl> mapContext = new ThreadLocal<>();

    @Override
    public void execute() throws MojoExecutionException {
        Logger.getLogger("org.yaml.snakeyaml.introspector").setLevel(Level.SEVERE);

        CodeProvider codeProvider = getProvider();
        CodeFactory factory = codeProvider.getInstance();

        Constructor c1 = new MyConstructor(mapContext);
        c1.addTypeDescription(new TdProgrammingModeEnum());
        c1.addTypeDescription(new TdType(factory));
        c1.addTypeDescription(new TdMethod(factory));
        c1.addTypeDescription(new TdInput(factory));
        c1.addTypeDescription(new TdOutput(factory));
        c1.addTypeDescription(new TdOutFields(factory));
        c1.addTypeDescription(new TdInOutFields(factory));
        c1.addTypeDescription(new TdSelectOptional(factory));
        c1.addTypeDescription(new TdSelectSingle(factory));
        c1.addTypeDescription(new TdSelectList(factory));
        c1.addTypeDescription(new TdCursorForSelect(factory));
        c1.addTypeDescription(new TdSelectListDyn(factory));
        c1.addTypeDescription(new TdDelete(factory));
        c1.addTypeDescription(new TdUpdate(factory));
        c1.addTypeDescription(new TdInsert(factory));
        c1.addTypeDescription(new TdDeleteBatch(factory));
        c1.addTypeDescription(new TdUpdateBatch(factory));
        c1.addTypeDescription(new TdInsertBatch(factory));
        c1.addTypeDescription(new TdInsertReturnGeneratedKeys(factory));
        c1.addTypeDescription(new TdCallProcedure(factory));
        c1.addTypeDescription(new TdInlineProcedure(factory));
        c1.addTypeDescription(new TdCallBatch(factory));
        c1.addTypeDescription(new TdInlineBatch(factory));
        c1.addTypeDescription(new TdCommand(factory));
//        c1.addTypeDescription(new TdCursorForUpdate());   // da rivedere

        Yaml apiYaml = new Yaml(c1);

        try {
            /*-------------------------*/
            loadSqlApi(factory, apiYaml);
            /*-------------------------*/
            setupMavenPaths(generateDirectory);

            log.info("Done.");
        } catch (Exception e) {
            log.error(e.toString());
            throw new MojoExecutionException("Failed to execute plugin", e);
        }
    }

    private CodeProvider getProvider() {
        if (provider == null)
            return ProviderEnum.POJO;
        if (provider.equalsIgnoreCase(ProviderEnum.POJO.name()))
            return ProviderEnum.POJO;
        if (provider.equalsIgnoreCase(ProviderEnum.SPRING.name()))
            return ProviderEnum.SPRING;
        throw new ProviderNotFoundException("Provider "+provider+" not found. Available providers: "+
                Arrays.stream(ProviderEnum.values()).map(Enum::name).collect(Collectors.joining(","))
        );
    }

    private void setupMavenPaths(File srcMain) {
        if (addCompileSourceRoot) {
            project.addCompileSourceRoot(srcMain.getPath());
        }
        if (addTestCompileSourceRoot) {
            project.addTestCompileSourceRoot(srcMain.getPath());
        }
    }

    private void loadSqlApi(CodeFactory factory, Yaml yaml) throws IOException, MojoExecutionException, InvalidQueryException {
        val pc = new MojoContext(
                generateDirectory.getPath(),
                plugin.getGroupId(),
                plugin.getArtifactId(),
                plugin.getVersion(),
                debugCode,
                java7,
                parserProvider
        );

        for (val module : modules) {
            log.info("Loading API from {}", module);
            File apiFile = new File(configDirectory, module);
            if (!apiFile.exists()) {
                log.warn("Setting {} does not exist, ignored.", module);
                continue;
            }
            try (InputStream inputStream = Files.newInputStream(apiFile.toPath())) {
                DaoClassConfig api = yaml.load(inputStream);
                makeDirectory(generateDirectory, api.getPackageName());
                /*--------------------------*/
                generateApi(pc, factory, api);
                /*--------------------------*/
            }
        }
        log.info("Total classes ...: {}", pc.getNmClasses());
        log.info("Total methods ...: {}", pc.getNmMethods());
    }

    private void generateApi(MojoContext pc, CodeFactory factory, DaoClassConfig api) throws MojoExecutionException, FileNotFoundException, InvalidQueryException {
        val classFullName = api.getPackageName()+"."+api.getClassName();
        if (! classLogbook.add(classFullName)) {
            throw new MojoExecutionException("Class <" + classFullName + "> duplicated");
        }
        /*--------------------*/
        api.create(pc, factory);
        /*--------------------*/
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
        if (!f.exists() && !f.mkdir())
            throw new MojoExecutionException("Cannot create directory <" + tmp + ">");
    }

}
