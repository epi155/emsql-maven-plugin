package io.github.epi155.esql.plugin;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

@Data
@Slf4j
public class SqlApi {
    private String packageName;
    private String className;
    private List<SqlMethod> methods;

    private static final String DOT_JAVA = ".java";
    private SortedSet<String> importSet = new TreeSet<>();

    public void create(MojoContext cx) throws FileNotFoundException, MojoExecutionException {
        log.info("Creating {} ...", className);
        File srcMainJava = new File(cx.sourceDirectory);
        File pkgFolder = new File(srcMainJava, packageName.replace('.', File.separatorChar));
        File clsFile = new File(pkgFolder, className+DOT_JAVA);
        try (PrintWriter pw = new PrintWriter(clsFile)) {
            writePackage(pw, cx);

            importSet.add("java.sql.*");

            StringWriter swCls = new StringWriter();
            IndentPrintWriter ipw = new IndentPrintWriter(4, swCls);
            classBegin(ipw);
            int kMethod = 0;
            for(val method: methods) {
                log.info("- method {} ...", method.getMethodName());
                ipw.println();
                writeMethod(ipw, method, ++kMethod, importSet);
            }
            ipw.ends(); // close class

            importSet.forEach(it -> pw.printf("import %s;%n", it));
            pw.println();

            pw.print(swCls);
        }
        log.info("Created.");

    }

    private void writeMethod(IndentPrintWriter ipw, SqlMethod method, int km, Set<String> set) throws MojoExecutionException {
        method.writeQuery(ipw, km, set);
    }


    private void classBegin(IndentPrintWriter pw) {
        pw.printf("public class %s {%n", className);
        pw.more();
        pw.printf("private %s() {}%n", className);
    }

    private void writePackage(PrintWriter pw, MojoContext cx) {
        writeCopyright(pw, cx);
        pw.printf("package %s;%n%n", packageName);
    }

    private void writeCopyright(PrintWriter pw, MojoContext cx) {
        String now = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
        pw.println("/*");
        pw.printf(" * Code Generated at %s%n", now);
        pw.printf(" * Plugin: %s:%s:%s%n", cx.group, cx.artifact, cx.version);
        pw.println(" */");
    }
}
