/*
 *
 *  Copyright (c) 2019. http://devonline.academy
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.revenat.javamm.ide.component.impl;

import com.revenat.javamm.code.exception.ConfigException;
import com.revenat.javamm.ide.component.CodeTemplateStorage;
import com.revenat.javamm.ide.model.CodeTemplate;
import com.revenat.juinit.addons.ReplaceCamelCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.revenat.javamm.ide.test.helper.CustomAsserts.assertErrorMessageContains;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a code template storage")
class CodeTemplateStorageTest {

    @Test
    void shouldFailIfDuplicateKeyFound() {
        CodeTemplateStorageImpl.Builder builder = new CodeTemplateStorageImpl.Builder();
        final String key = "test";
        final String template = "template";
        final String newTemplate = "new template";

        builder.addTemplate(template, key);
        final ConfigException e = assertThrows(ConfigException.class, () -> builder.addTemplate(newTemplate, key));

        assertErrorMessageContains(e, "Duplicate found: key=%s, value1=%s, value2=%s", key, template, newTemplate);
    }

    @Test
    void shouldProvideTemplateByKey() {
        final String key = "key";
        final CodeTemplateStorage templateStorage = new CodeTemplateStorageImpl.Builder()
            .addTemplate("template", key)
            .build();

        final Optional<CodeTemplate> result = templateStorage.getTemplate(key);

        assertTrue(result.isPresent());
    }

    @Test
    void shouldReturnEmptyOptionalIfNoTemplateFount() {
        final String key = "key";
        final CodeTemplateStorage templateStorage = new CodeTemplateStorageImpl.Builder()
            .addTemplate("template", key)
            .build();

        final String newKey = "newKey";
        final Optional<CodeTemplate> optionalTemplate = templateStorage.getTemplate(newKey);

        assertFalse(optionalTemplate.isPresent());
    }
}