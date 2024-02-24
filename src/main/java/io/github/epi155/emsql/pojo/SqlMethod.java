package io.github.epi155.emsql.pojo;

import io.github.epi155.emsql.api.MethodModel;
import io.github.epi155.emsql.api.PerformModel;
import io.github.epi155.emsql.api.PrintModel;
import lombok.Getter;
import lombok.Setter;
import org.apache.maven.plugin.MojoExecutionException;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import static io.github.epi155.emsql.pojo.Tools.cc;
import static io.github.epi155.emsql.pojo.Tools.mc;

@Setter
@Getter
public class SqlMethod implements MethodModel {
    private String methodName;
    private PerformModel perform;

    private static final NumberFormat NF = new DecimalFormat("0000");
    public void writeCode(PrintModel ipw, int km) throws MojoExecutionException {
        mc = new MethodContext(this);
        String kPrg = NF.format(km);
        perform.writeCode(ipw, cc.getFields(), kPrg);
    }
}
