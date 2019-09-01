
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

package com.revenat.javamm.code.fragment.operation;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.revenat.javamm.code.fragment.SourceLine;

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
@DisplayName("a 'println' operation")
class PrintlnOperationTest {
    private static final String TEXT = "\'Hello Java\'";
    private static final SourceLine OPERATION_SOURCE_LINE = new SourceLine("test", 1, List.of("println", "(", TEXT, ")"));

    @Test
    @Order(1)
    void canNotBeCreatedWithoutSourceLine() {
        assertThrows(NullPointerException.class, () -> new PrintlnOperation(null, TEXT));
    }

    @Test
    @Order(2)
    void canNotBeCreatedWithoutText() {
        assertThrows(NullPointerException.class, () -> new PrintlnOperation(OPERATION_SOURCE_LINE, null));
    }

    @Test
    void shouldReturnTextToPrint() {
        final PrintlnOperation operation = new PrintlnOperation(OPERATION_SOURCE_LINE, TEXT);

        assertThat(operation.getText(), equalTo(TEXT));
    }

}
