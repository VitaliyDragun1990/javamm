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

package com.revenat.javamm.cmd;

import com.revenat.juinit.addons.ReplaceCamelCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a javamm virtual machine launcher")
class JavammCMDLauncherTest {

    private static final PrintStream REAL_ERR = System.err;

    @Mock
    private PrintStream errMock;

    @BeforeEach
    void setup() {
        System.setErr(errMock);
    }

    @AfterEach
    void tearDown() {
        System.setErr(REAL_ERR);
    }

    @Test
    void shouldLaunchVirtualMachineIfFileWithSourceCodeSpecifiedAsArguments(@TempDir Path sourceFileDir) throws IOException {
        Path testSourceFile = createFileWithContent(sourceFileDir, "test.javamm", List.of("function main() {", "}"));

        JavammCMDLauncher.main(testSourceFile.toString());

        verify(errMock, never()).println(anyString());
    }

    @Test
    void shouldFailIfMainFunctionNotFound() {
        JavammCMDLauncher.main();

        verify(errMock)
            .println(contains("Runtime error: Main function not found, please define the main function as: 'function main()'"));
    }

    private Path createFileWithContent(final Path fileDir,
                                       final String fileName,
                                       final List<String> fileContent) throws IOException {
        Path file = fileDir.resolve(fileName);
        Files.write(file, fileContent);
        return file;
    }
}