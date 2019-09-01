
/*
 * Copyright (c) 2019. http://devonline.academy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.revenat.javamm.cmd;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;

import com.revenat.javamm.code.fragment.SourceCode;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.revenat.juinit.addons.ReplaceCamelCase;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a file source code")
class FileSourceCodeTest {
    private static final String TEST_FILE_NAME = "src/test/resources/test.javamm";

    @Test
    @Order(1)
    void canNotBeCreatedForInvalidFile() {
        assertThrows(IOException.class, () -> new FileSourceCode("invalid"));
    }

    @Test
    @Order(2)
    void canBeCreatedForValidFile() throws IOException {
        new FileSourceCode(TEST_FILE_NAME);
    }

    @Test
    @Order(3)
    void shouldContainFileContent() throws IOException {
        final SourceCode sourceCode = new FileSourceCode(TEST_FILE_NAME);

        final List<String> fileContent = sourceCode.getLines();

        assertThat(fileContent, hasSize(2));
        assertThat(fileContent, hasItem(equalTo("test")));
        assertThat(fileContent, hasItem(equalTo("")));
    }

    @Test
    @Order(4)
    void shouldContainFileName() throws IOException {
        final SourceCode sourceCode = new FileSourceCode(TEST_FILE_NAME);

        assertThat(sourceCode.getModuleName(), equalTo("test.javamm"));
    }
}
