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

package com.revenat.javamm.compiler.component.impl;

import com.revenat.javamm.code.exception.ConfigException;
import com.revenat.javamm.code.fragment.Operator;
import com.revenat.javamm.code.fragment.operator.UnaryOperator;
import com.revenat.javamm.compiler.component.OperatorPrecedenceResolver;
import com.revenat.javamm.compiler.test.helper.CustomAsserts;
import com.revenat.juinit.addons.ReplaceCamelCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("an operator precedence resolver")
class OperatorPrecedenceResolverImplTest {

    private OperatorPrecedenceResolver operatorPrecedenceResolver = new OperatorPrecedenceResolverImpl();

    @Test
    @Order(1)
    void shouldFailToProvidePrecedenceIfOperatorIsUnknown() {
        Operator unknownOperator = mock(Operator.class, "unknownOperator");

        ConfigException e =
            assertThrows(ConfigException.class, () -> operatorPrecedenceResolver.getPrecedence(unknownOperator));

        CustomAsserts.assertErrorMessageContains(e, "Precedence not defined for %s", unknownOperator);
    }

    @Test
    @Order(2)
    void shouldProvidePrecedenceForOperator() {
        int expectedPrecedence = OperatorPrecedenceResolverImpl.MAX_PRECEDENCE - 1;

        assertThat(operatorPrecedenceResolver.getPrecedence(UnaryOperator.INCREMENT), equalTo(expectedPrecedence));
    }

    @Test
    @Order(3)
    void shouldReturnTrueIfOneOperatorHasLowerPrecedenceThatTheOther() {
        Operator withHigherPrecedence = UnaryOperator.INCREMENT;
        Operator withLowerPrecedence = UnaryOperator.HASH_CODE;

        assertTrue(operatorPrecedenceResolver.hasLowerPrecedence(withLowerPrecedence, withHigherPrecedence));
    }

    @Test
    @Order(4)
    void shouldReturnFalseIfOneOperatorDoesNotHaveLowerPrecedenceThatTheOther() {
        Operator withHigherPrecedence = UnaryOperator.INCREMENT;
        Operator withLowerPrecedence = UnaryOperator.HASH_CODE;

        assertFalse(operatorPrecedenceResolver.hasLowerPrecedence(withHigherPrecedence, withLowerPrecedence));
    }
}