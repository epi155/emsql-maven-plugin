package io.github.epi155.emsql.plugin.sql;

import io.github.epi155.emsql.plugin.ClassContext;
import io.github.epi155.emsql.plugin.IndentPrintWriter;
import io.github.epi155.emsql.plugin.MojoContext;
import lombok.Getter;
import lombok.Setter;
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
import java.util.*;

@Setter
@Slf4j
public class SqlApi {
    @Getter
    private String packageName;
    @Getter
    private String className;
    private Map<String, SqlKind> declare = new LinkedHashMap<>();
    private List<SqlMethod> methods;

    private static final String DOT_JAVA = ".java";

    public void create(MojoContext cx) throws FileNotFoundException, MojoExecutionException {
        log.info("Creating {} ...", className);
        ClassContext cc = new ClassContext(cx, declare);
        File srcMainJava = new File(cx.sourceDirectory);
        File pkgFolder = new File(srcMainJava, packageName.replace('.', File.separatorChar));
        File clsFile = new File(pkgFolder, className+DOT_JAVA);
        try (PrintWriter pw = new PrintWriter(clsFile)) {
            Set<String> basket = new HashSet<>();
            writePackage(pw, cx);
            StringWriter swCls = new StringWriter();
            IndentPrintWriter ipw = new IndentPrintWriter(4, swCls);
            classBegin(ipw, cx);
            int kMethod = 0;
            for(val method: methods) {
                String methodName = method.getMethodName();
                log.info("- method {} ...", methodName);
                if (basket.contains(methodName)) {
                    log.warn("Duplicate method name {}, skipped", methodName);
                } else {
                    ipw.println();
                    /*------------------------------------*/
                    writeMethod(ipw, method, ++kMethod, cc);
                    /*------------------------------------*/
                    basket.add(methodName);
                }
            }
            cc.flush(ipw);
            ipw.ends(); // close class

            cc.writeImport(pw);
            pw.println();

            pw.print(swCls);
        }
        log.info("Created.");

    }

    private void writeMethod(IndentPrintWriter ipw, SqlMethod method, int km, ClassContext cc) throws MojoExecutionException {
        method.writeQuery(ipw, km, cc);
    }


    private void classBegin(IndentPrintWriter pw, MojoContext cx) {
        pw.printf("public class %s {%n", className);
        pw.more();
        if (cx.debug) {
            pw.printf("private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(%s.class);%n", className);
        }
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
