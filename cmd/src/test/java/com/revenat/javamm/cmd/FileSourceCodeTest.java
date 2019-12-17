
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

import com.revenat.javamm.code.fragment.SourceCode;
import com.revenat.juinit.addons.ReplaceCamelCase;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.lang.String.format;
import static java.lang.System.lineSeparator;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a file source code")
class FileSourceCodeTest {
    private static Path tempFile;

    private SourceCode sourceCode;

    @BeforeAll
    static void createTempFile() throws IOException {
        tempFile = Files.createTempFile("test", "javamm");
        Files.writeString(tempFile, format("1%s2%s3", lineSeparator(), lineSeparator()), StandardCharsets.UTF_8);
    }

    @AfterAll
    static void deleteTempFile() throws IOException {
        if (tempFile != null) {
            Files.deleteIfExists(tempFile);
        }
    }

    @BeforeEach
    void createSourceCode() throws IOException {
        sourceCode = new FileSourceCode(getPathTo(tempFile));
    }

    private String getPathTo(final Path file) {
        return file.toString();
    }


    private String getNameFor(final Path file) {
        return file.getFileName().toString();
    }

    @Test
    @Order(1)
    void canNotBeCreatedForInvalidFile() {
        assertThrows(IOException.class, () -> new FileSourceCode("invalid"));
    }

    @Test
    @Order(2)
    void shouldContainFileContent() {
        final List<String> fileContent = sourceCode.getLines();

        assertThat(fileContent, equalTo(List.of("1", "2", "3")));
    }

    @Test
    @Order(3)
    void shouldContainFileName() {
        assertThat(sourceCode.getModuleName(), equalTo(getNameFor(tempFile)));
    }

    @Test
    @Order(4)
    void shouldContainRelativePathToSourceFile() {
        assertPathToSourceFile(sourceCode);
    }

    private void assertPathToSourceFile(final SourceCode sourceCode) {
        assertThat(sourceCode.toString(), equalTo(relativePathFor(tempFile)));
    }

    private String relativePathFor(final Path file) {
        return file.toString();
    }
}
