package io.github.epi155.emsql.plugin;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.InvalidQueryException;
import io.github.epi155.emsql.api.MethodModel;
import io.github.epi155.emsql.api.TypeModel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Setter
@Slf4j
public class DaoClassConfig {
    @Getter
    private String packageName;
    @Getter
    private String className;
    private Map<String, TypeModel> declare = new LinkedHashMap<>();
    private List<MethodModel> methods;

    private static final String DOT_JAVA = ".java";

    public void create(MojoContext pc, CodeFactory factory) throws FileNotFoundException, InvalidQueryException {
        log.info("Creating {} ...", className);
        File srcMainJava = new File(pc.sourceDirectory);
        File pkgFolder = new File(srcMainJava, packageName.replace('.', File.separatorChar));
        File clsFile = new File(pkgFolder, className+DOT_JAVA);
        factory.classContext(pc, declare);
        try (PrintWriter pw = new PrintWriter(clsFile)) {
            writePackage(pw, pc);
            StringWriter swCls = new StringWriter();
            IndentPrintWriter ipw = new IndentPrintWriter(4, swCls);

            factory.createClass(ipw, className, methods, declare)
                    .accept(pw);

            pw.print(swCls);
        }
        log.info("Created.");
        pc.incClasses();
    }

    private void writePackage(PrintWriter pw, MojoContext mc) {
        writeCopyright(pw, mc);
        pw.printf("package %s;%n%n", packageName);
    }

    private void writeCopyright(PrintWriter pw, MojoContext mc) {
        String now = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
        pw.println("/*");
        pw.printf(" * Code Generated at %s%n", now);
        pw.printf(" * Plugin: %s:%s:%s%n", mc.group, mc.artifact, mc.version);
        pw.println(" */");
    }
}
