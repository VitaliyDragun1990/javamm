
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

package com.revenat.javamm.code.fragment.function;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import com.revenat.javamm.code.fragment.FunctionName;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.revenat.juinit.addons.ReplaceCamelCase;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
@DisplayName("an abstract function name")
class OverloadableFunctionNameTest {

    @ParameterizedTest
    @CsvSource(delimiter = ':', value = {
        "f: 0: f()",
        "f: 3: f(a,b,c)",
        "f: 10: f(a,b,c,d,e,f,g,h,i,j)",
        "f: 53: f(a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z,a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z,a)"
    })
    @Order(1)
    void shouldProvideFullNameWithPlaceholderForEachArgument(final String name,
                                                             final int argumentCount,
                                                             final String expectedRepresentation) {
        final OverloadableFunctionName functionName = createOverloadableName(name, argumentCount);

        assertStringRepresentation(functionName, expectedRepresentation);
    }

    @Test
    @Order(2)
    void shouldBeEqualToAnotherOverloadableFunctionNameWithSameNameAndArgumentCount() {
        final OverloadableFunctionName functionNameA = createOverloadableName("f", 0);
        final OverloadableFunctionName functionNameB = createOverloadableName("f", 0);

        assertThat(functionNameA, equalTo(functionNameB));
    }

    @Test
    @Order(3)
    void shouldNotBeEqualToAnotherOverloadableFunctionNameWithSameNameButDifferentArgumentCount() {
        final OverloadableFunctionName functionNameA = createOverloadableName("f", 0);
        final OverloadableFunctionName functionNameB = createOverloadableName("f", 1);

        assertThat(functionNameA, is(not(equalTo(functionNameB))));
    }

    @Test
    @Order(4)
    void shouldNotBeEqualToAnotherOverloadableFunctionNameWithDifferentNameButSameArgumentCount() {
        final OverloadableFunctionName functionNameA = createOverloadableName("f", 1);
        final OverloadableFunctionName functionNameB = createOverloadableName("a", 1);

        assertThat(functionNameA, is(not(equalTo(functionNameB))));
    }

    @Test
    @Order(5)
    void shouldBeEqualToNonOverloadableFunctionNameWithSameNameRegardlessOfArgumentCount() {
        final OverloadableFunctionName overloadableWithoutArgs = createOverloadableName("f", 0);
        final OverloadableFunctionName overloadableWith2Args = createOverloadableName("f", 2);
        final FunctionName simple = createSimpleName("f");

        assertThat(overloadableWithoutArgs, equalTo(simple));
        assertThat(overloadableWith2Args, equalTo(simple));
    }

    @Test
    @Order(6)
    void shouldCompareByFullNamesInLexicographicalOrderAgainsAnotherOverloadableFunctionName() {
        assertInRelativeOrder(createOverloadableName("a", 0), createOverloadableName("a", 5));
        assertInRelativeOrder(createOverloadableName("aa", 2), createOverloadableName("ab", 2));
        assertInRelativeOrder(createOverloadableName("aa", 2), createOverloadableName("aa", 2));
    }

    @Test
    @Order(7)
    void shouldCompareByNamesInLexicographicalOrderAgainsAnotherNonOverloadableFunctionName() {
        assertInRelativeOrder(createOverloadableName("a", 5), createSimpleName("a"));
        assertInRelativeOrder(createOverloadableName("aaaa", 0), createSimpleName("aaab"));
    }

    @Test
    @Order(8)
    void shouldHaveSameHashcodeForSameNameRegardlessOfArgumentCount() {
        assertEqualHashcode(createOverloadableName("a", 0), createOverloadableName("a", 5));
        assertEqualHashcode(createOverloadableName("aaa", 5), createOverloadableName("aaa", 100));

        assertNotEqualHashcode(createOverloadableName("aab", 0), createOverloadableName("aba", 0));
    }

    private void assertEqualHashcode(final FunctionName nameA, final FunctionName nameB) {
        assertThat(nameA.hashCode(), equalTo(nameB.hashCode()));
    }

    private void assertNotEqualHashcode(final FunctionName nameA, final FunctionName nameB) {
        assertThat(nameA.hashCode(), is(not(equalTo(nameB.hashCode()))));
    }

    private void assertInRelativeOrder(final FunctionName expectedFirst, final FunctionName expectedSecond) {
        assertThat(expectedFirst.compareTo(expectedSecond), is(lessThanOrEqualTo(0)));
    }

    private void assertStringRepresentation(final OverloadableFunctionName functionName, final String expectedRepresentation) {
        assertThat(functionName.toString(), equalTo(expectedRepresentation));
    }

    private OverloadableFunctionName createOverloadableName(final String name, final int argumentCount) {
        return new SimpleOverloadableFunctionName(name, argumentCount);
    }

    private FunctionName createSimpleName(final String name) {
        return new SimpleFunctionName(name);
    }

    private class SimpleOverloadableFunctionName extends OverloadableFunctionName {

        public SimpleOverloadableFunctionName(final String name, final int argumentCount) {
            super(name, argumentCount);
        }
    }

    private class SimpleFunctionName implements FunctionName {
        private final String name;

        private SimpleFunctionName(final String name) {
            this.name = name;
        }

        @Override
        public int compareTo(final FunctionName o) {
            return 0;
        }

        @Override
        public String getName() {
            return name;
        }

    }
}
