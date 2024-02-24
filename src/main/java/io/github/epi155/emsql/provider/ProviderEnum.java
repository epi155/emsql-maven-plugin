package io.github.epi155.emsql.provider;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.CodeProvider;
import io.github.epi155.emsql.pojo.PojoFactory;

public enum ProviderEnum implements CodeProvider {
    POJO{
        @Override
        public CodeFactory getInstance() {
            return new PojoFactory();
        }
    }
}
