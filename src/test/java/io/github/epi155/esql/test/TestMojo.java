package io.github.epi155.esql.test;

import io.github.epi155.emsql.plugin.SqlMojo;
import io.github.epi155.emsql.provider.ProviderEnum;
import lombok.SneakyThrows;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
//@Order(1)
class TestMojo {
    private static final String[] MODULES = {
            "daoSubSelect.yaml",
            "daoArray.yaml",
            "daoBatch.yaml",
            "daoBin.yaml",
            "daoBlob.yaml",
            "daoBool.yaml",
            "daoBStream.yaml",
            "daoByte.yaml",
            "daoChar.yaml",
            "daoClob.yaml",
            "daoCStream.yaml",
            "daoCursor.yaml",
            "daoDate.yaml",
            "daoDelete.yaml",
            "daoDouble.yaml",
            "daoFloat.yaml",
            "daoInsert.yaml",
            "daoInt.yaml",
            "daoLDate.yaml",
            "daoList2.yaml",
            "daoList.yaml",
            "daoLNVCharS.yaml",
            "daoLNVChar.yaml",
            "daoLong.yaml",
            "daoLTime.yaml",
            "daoLTmst.yaml",
            "daoLVarBinS.yaml",
            "daoLVarBin.yaml",
            "daoLVCharS.yaml",
            "daoLVChar.yaml",
            "daoNBool.yaml",
            "daoNChar.yaml",
            "daoNClob.yaml",
            "daoNCStream.yaml",
            "daoNest.yaml",
            "daoNumb.yaml",
            "daoProc.yaml",
            "daoRef.yaml",
            "daoRow.yaml",
            "daoSelect.yaml",
            "daoShort.yaml",
            "daoTime.yaml",
            "daoTmst.yaml",
            "daoUpdate.yaml",
            "daoUrl.yaml",
            "daoVarBin.yaml",
            "daoVChar.yaml",
            "daoVNChar.yaml",
            "daoXml.yaml",
            "ddSelect.yaml",
            "doSelect.yaml",
            "drSelect.yaml",
            "odSelect.yaml",
            "ooSelect.yaml",
            "orSelect.yaml",
            "rdSelect.yaml",
            "roSelect.yaml",
            "rrSelect.yaml",
            "daoListD.yaml",
            "daoCursorD.yaml",
    };

    @Test
    void generateTestLite() {
        SqlMojo mojo = new SqlMojo();
        mojo.setGenerateDirectory(new File("target/generated-test-sources/esql7"));
        mojo.setPlugin(new PluginDescriptor() {
            public String getGroupId() {
                return "io.github.epi155";
            }

            public String getArtifactId() {
                return "emsql-maven-plugin";
            }

            public String getVersion() {
                return "TEST";
            }
        });
        mojo.setDebugCode(true);
        mojo.setJava7(false);
        mojo.setConfigDirectory(new File("src/test/resources"));
        mojo.setModules(new String[]{"daoCursorD.yaml"});
        mojo.setProvider(ProviderEnum.POJO.name());


        File pomFile = new File("pom.xml");
        MavenProject project = getProject(pomFile.toPath());
        mojo.setProject(project);

        mojo.setAddCompileSourceRoot(false);
        mojo.setAddTestCompileSourceRoot(false);

        Assertions.assertDoesNotThrow(mojo::execute);
    }

    @Test
    void generateTest7() {
        SqlMojo mojo = new SqlMojo();
        mojo.setGenerateDirectory(new File("target/generated-test-sources/esql7"));
        mojo.setPlugin(new PluginDescriptor() {
            public String getGroupId() {
                return "io.github.epi155";
            }

            public String getArtifactId() {
                return "emsql-maven-plugin";
            }

            public String getVersion() {
                return "TEST";
            }
        });
        mojo.setDebugCode(true);
        mojo.setJava7(true);
        mojo.setConfigDirectory(new File("src/test/resources"));
        mojo.setModules(MODULES);
        mojo.setProvider(ProviderEnum.POJO.name());


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
            public String getGroupId() {
                return "io.github.epi155";
            }

            public String getArtifactId() {
                return "emsql-maven-plugin";
            }

            public String getVersion() {
                return "TEST";
            }
        });
        mojo.setDebugCode(true);
        mojo.setJava7(false);
        mojo.setConfigDirectory(new File("src/test/resources"));
        mojo.setModules(MODULES);
        mojo.setProvider(ProviderEnum.SPRING.name());


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
