
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

package com.revenat.javamm.vm.integration;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import com.revenat.javamm.code.fragment.SourceCode;
import com.revenat.javamm.vm.VirtualMachine;
import com.revenat.javamm.vm.VirtualMachineBuilder;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;


public abstract class AbstractIntegrationTest {

    private static final String MODULE_NAME = "test";

    private final PrintStream originalOutputStream = System.out;

    private final SpyPrintStream testOutputStream = new SpyPrintStream();

    private final VirtualMachine virtualMachine = new VirtualMachineBuilder().build();

    @BeforeEach
    public void setupSpyOutput() {
        System.setOut(testOutputStream);
    }

    @AfterEach
    void setUpOriginalOutput() {
        System.setOut(originalOutputStream);
    }

    protected final void runBlock(final List<String> operations) {
        final List<String> validOperations = putInsideFunction(operations);

        virtualMachine.run(new TestSourceCode(validOperations, MODULE_NAME));
    }

    protected final void runBlock(final String operation) {
        runBlock(List.of(operation));
    }

    protected void assertExpectedOutput(final List<Object> expectedOutput) {
        assertThat(getOutput(), equalTo(expectedOutput));
    }

    private List<String> putInsideFunction(final List<String> operations) {
        final List<String> validOperations = new ArrayList<>();
        validOperations.add(""); // TODO Add function declaration here
        validOperations.addAll(operations);
        return validOperations;
    }

    protected final List<Object> getOutput() {
        return Collections.unmodifiableList(testOutputStream.getOutput());
    }

    static final class TestSourceCode implements SourceCode {

        private final String moduleName;

        private final List<String> lines;

        public TestSourceCode(final List<String> lines, final String moduleName) {
            this.lines = Collections.unmodifiableList(lines);
            this.moduleName = moduleName;
        }

        public TestSourceCode(final String line, final String moduleName) {
            this.lines = List.of(line);
            this.moduleName = moduleName;
        }

        @Override
        public String getModuleName() {
            return moduleName;
        }

        @Override
        public List<String> getLines() {
            return lines;
        }
    }

    static class SpyPrintStream extends PrintStream {
        private final List<Object> output = new ArrayList<>();

        public SpyPrintStream() {
            super(new ByteArrayOutputStream());
        }

        public List<Object> getOutput() {
            return output;
        }

        @Override
        public void println(final Object x) {
            output.add(x);
        }
    }
}
