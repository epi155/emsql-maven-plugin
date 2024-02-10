package io.github.epi155.esql.test;

import io.github.epi155.emsql.plugin.SqlMojo;
import lombok.SneakyThrows;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(1)
class TestMojo {
    private static final String[] MODULES = {
            "daoList2.yaml",
            "daoList.yaml",
            "daoNest.yaml",
        "daoBool.yaml",
                "daoShort.yaml",
                "daoInt.yaml",
                "daoLong.yaml",
                "daoVChar.yaml",
                "daoChar.yaml",
                "daoDate.yaml",
                "daoTmst.yaml",
                "daoNumb.yaml",
                "daoLDate.yaml",
                "daoLTmst.yaml",
                "daoTime.yaml",
                "daoLTime.yaml",
                "daoDouble.yaml",
                "daoFloat.yaml",
                "daoCursor.yaml",
                "daoBatch.yaml",
                "daoInsert.yaml",
                "daoDelete.yaml",
                "daoUpdate.yaml",
                "daoSelect.yaml",
            "daoProc.yaml",
    };

    @Test
    void generateTest7() {
        SqlMojo mojo = new SqlMojo();
        mojo.setGenerateDirectory(new File("target/generated-test-sources/esql7"));
        mojo.setPlugin(new PluginDescriptor() {
            public String getGroupId() { return "io.github.epi155"; }
            public String getArtifactId() { return "emsql-maven-plugin"; }
            public String getVersion() { return "TEST"; }
        });
        mojo.setDebugCode(true);
        mojo.setJava7(true);
        mojo.setConfigDirectory(new File("src/test/resources"));
        mojo.setModules(MODULES);


        File pomFile = new File("pom.xml");
        MavenProject project = getProject(pomFile.toPath());
        mojo.setProject(project);

        mojo.setAddCompileSourceRoot(false);
        mojo.setAddTestCompileSourceRoot(false);

        Assertions.assertDoesNotThrow(mojo::execute);
    }

    @Test
    void generateTest8() {
        SqlMojo mojo = new SqlMojo();
        mojo.setGenerateDirectory(new File("target/generated-test-sources/esql8"));
        mojo.setPlugin(new PluginDescriptor() {
            public String getGroupId() { return "io.github.epi155"; }
            public String getArtifactId() { return "emsql-maven-plugin"; }
            public String getVersion() { return "TEST"; }
        });
        mojo.setDebugCode(true);
        mojo.setJava7(false);
        mojo.setConfigDirectory(new File("src/test/resources"));
        mojo.setModules(MODULES);


        File pomFile = new File("pom.xml");
        MavenProject project = getProject(pomFile.toPath());
        mojo.setProject(project);

        mojo.setAddCompileSourceRoot(false);
        mojo.setAddTestCompileSourceRoot(false);

        Assertions.assertDoesNotThrow(mojo::execute);
    }

    @SneakyThrows
    MavenProject getProject(Path pomPath) {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        try (InputStream is = Files.newInputStream(pomPath)) {
            Model model = reader.read(is);
            return new MavenProject(model);
        }
    }
}
