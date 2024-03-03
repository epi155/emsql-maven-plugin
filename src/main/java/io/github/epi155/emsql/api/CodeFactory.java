package io.github.epi155.emsql.api;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

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

    Consumer<PrintWriter> createClass(PrintModel pw, String className, List<MethodModel> methods, Map<String, TypeModel> declare) throws InvalidQueryException;

    TypeModel getInstance(String value, MapContext mapContext);

    void classContext(PluginContext pc, Map<String, TypeModel> declare);

    CallBatchModel newCallBatchModel();

    InlineBatchModel newInlineBatchModel();
}
