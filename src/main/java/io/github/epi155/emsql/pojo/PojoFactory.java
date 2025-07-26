package io.github.epi155.emsql.pojo;

import io.github.epi155.emsql.api.*;
import io.github.epi155.emsql.commons.BasicFactory;
import io.github.epi155.emsql.pojo.dml.*;
import io.github.epi155.emsql.pojo.dpl.*;
import io.github.epi155.emsql.pojo.dql.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static io.github.epi155.emsql.commons.Contexts.cc;

@Slf4j
public class PojoFactory extends BasicFactory {
    @Override
    public SelectSingleModel newSelectSingleModel() {
        return new SqlSelectSingle();
    }

    @Override
    public SelectOptionalModel newSelectOptionalModel() {
        return new SqlSelectOptional();
    }

    @Override
    public SelectListModel newSelectListModel() {
        return new SqlSelectList();
    }

    @Override
    public CursorForSelectModel newCursorForSelectModel() {
        return new SqlCursorForSelect();
    }

    @Override
    public SelectListDynModel newSelectListDynModel() {
        return new SqlSelectListDyn();
    }

    @Override
    public DeleteModel newDeleteModel() {
        return new SqlDelete();
    }

    @Override
    public InsertModel newInsertModel() {
        return new SqlInsert();
    }

    @Override
    public UpdateModel newUpdateModel() {
        return new SqlUpdate();
    }

    @Override
    public DeleteBatchModel newDeleteBatchModel() {
        return new SqlDeleteBatch();
    }

    @Override
    public InsertBatchModel newInsertBatchModel() {
        return new SqlInsertBatch();
    }

    @Override
    public UpdateBatchModel newUpdateBatchModel() {
        return new SqlUpdateBatch();
    }

    @Override
    public InsertReturnGeneratedKeysModel newInsertReturnGeneratedKeysModel() {
        return new SqlInsertReturnGeneratedKeys();
    }

    @Override
    public CallProcedureModel newCallProcedureModel() {
        return new SqlCallProcedure();
    }

    @Override
    public InlineProcedureModel newInlineProcedureModel() {
        return new SqlInlineProcedure();
    }

    @Override
    public CommandModel newCommandModel() {
        return new SqlCommand();
    }


    @Override
    public void classContext(PluginContext pc, Map<String, TypeModel> declare) {
        cc = new PojoClassContext(pc, declare);
    }

    @Override
    public CallBatchModel newCallBatchModel() {
        return new SqlCallBatch();
    }

    @Override
    public InlineBatchModel newInlineBatchModel() {
        return new SqlInlineBatch();
    }

    protected void classBegin(PrintModel pw, String className, boolean isDebug) {
        pw.printf("public class %s {%n", className);
        pw.more();
        if (isDebug) {
            pw.printf("private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(%s.class);%n", className);
        }
        pw.printf("private %s() {}%n", className);
    }
}
