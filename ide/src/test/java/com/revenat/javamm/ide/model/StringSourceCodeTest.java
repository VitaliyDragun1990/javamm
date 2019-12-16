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

package com.revenat.javamm.ide.model;

import com.revenat.juinit.addons.ReplaceCamelCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("a string source code")
class StringSourceCodeTest {

    @Test
    void shouldProvideModuleName() {
        String moduleName = "testModule";

        StringSourceCode sourceCode = new StringSourceCode(moduleName, List.of());

        assertThat(sourceCode.getModuleName(), equalTo(moduleName));
    }

    @Test
    void shouldProvideSourceLines() {
        List<String> sourceLines = List.of("function main() {", "println('Hello world')", "}");

        StringSourceCode sourceCode = new StringSourceCode("testModule", sourceLines);

        assertThat(sourceCode.getLines(), equalTo(sourceLines));
    }
}