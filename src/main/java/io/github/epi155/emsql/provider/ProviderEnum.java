package io.github.epi155.emsql.provider;

import io.github.epi155.emsql.api.CodeFactory;
import io.github.epi155.emsql.api.CodeProvider;
import io.github.epi155.emsql.pojo.PojoFactory;
import io.github.epi155.emsql.spring.SpringFactory;

public enum ProviderEnum implements CodeProvider {
    POJO {
        @Override
        public CodeFactory getInstance(boolean b) {
            return new PojoFactory(b);
        }
    },
    SPRING {
        @Override
        public CodeFactory getInstance(boolean b) {
            return new SpringFactory(b);
        }
    },
}

