package io.github.epi155.emsql.api;

import io.github.epi155.emsql.plugin.MapContext;
import org.apache.maven.plugin.MojoExecutionException;

import java.util.List;
import java.util.Map;

public interface CodeFactory {
    MethodModel newMethodModel();

    InputModel newInputModel();
    OutputModel newOutputModel();
    OutFieldsModel newOutFieldsModel();

    SelectSingleModel newSelectSingleModel();
    SelectOptionalModel newSelectOptionalModel();
    SelectListModel newSelectListModel();
    CursorForSelectModel newCursorForSelectModel();
    DeleteModel newDeleteModel();
    InsertModel newInsertModel();
    UpdateModel newUpdateModel();
    DeleteBatchModel newDeleteBatchModel();
    InsertBatchModel newInsertBatchModel();
    UpdateBatchModel newUpdateBatchModel();
    InsertReturnGeneratedKeysModel newInsertReturnGeneratedKeysModel();
    CallProcedureModel newCallProcedureModel();
    InlineProcedureModel newInlineProcedureModel();

    void createClass(PrintModel pw, String className, List<MethodModel> methods, Map<String, TypeModel> declare) throws MojoExecutionException;

    TypeModel getInstance(String value, MapContext mapContext);

    void classContext(PluginContext pc, Map<String, TypeModel> declare);
}
