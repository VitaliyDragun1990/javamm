
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

package com.revenat.javamm.compiler.integration;

import com.revenat.javamm.code.fragment.ByteCode;
import com.revenat.javamm.code.fragment.SourceCode;
import com.revenat.javamm.compiler.Compiler;
import com.revenat.javamm.compiler.CompilerConfigurator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Vitaliy Dragun
 */
public abstract class AbstractIntegrationTest {

    private static final String ANY_MODULE = "module1";

    private final Compiler compiler = new CompilerConfigurator().getCompiler();

    protected ByteCode wrapInsideMainFunctionAndCompile(final List<String> lines, final boolean withClosingCurlyBrace) {
        final List<String> result = new ArrayList<>();
        result.add("function main() {");
        result.addAll(lines);
        if (withClosingCurlyBrace) {
            result.add("}");
        }

        return compile(result);
    }

    protected ByteCode compile(final List<String> lines) {
        return compiler.compile(new SourceCode() {

            @Override
            public String getModuleName() {
                return ANY_MODULE;
            }

            @Override
            public List<String> getLines() {
                return Collections.unmodifiableList(lines);
            }
        });
    }
}
