
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

package com.revenat.javamm.code.syntax;

import com.revenat.javamm.code.fragment.operator.BinaryOperator;
import com.revenat.javamm.code.fragment.operator.TernaryConditionalOperator;
import com.revenat.javamm.code.fragment.operator.UnaryOperator;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toUnmodifiableSet;

/**
 * Contains all possible delimiters supported in the Javamm language syntax
 *
 * @author Vitaliy Dragun
 */
@SuppressWarnings("ALL")
public final class Delimiters {

    public static final String START_MULTI_LINE_COMMENT = "/*";

    public static final String END_MULTI_LINE_COMMENT = "*/";

    public static final String START_SINGLE_LINE_COMMENT = "//";

    public static final Set<Character> IGNORED_DELIMITERS = Set.of(' ', '\u00A0', '\n', '\t', '\r');

    public static final String DOUBLE_QUOTATION = "\"";

    public static final String SINGLE_QUOTATION = "'";

    public static final Set<Character> STRING_DELIMITERS = Set.of('\'', '"');

    public static final String OPENING_CURLY_BRACE = "{";

    public static final String CLOSING_CURLY_BRACE = "}";

    public static final String OPENING_SQUARE_BRACKET = "[";

    public static final String CLOSING_SQUARE_BRACKET = "]";

    public static final String OPENING_PARENTHESIS = "(";

    public static final String CLOSING_PARENTHESIS = ")";

    /*
      "+", "++", "+=", "-", "--", "-=", "*", "*=", "/", "/=", "%", "%=",
      ">", ">>", ">=", ">>>", ">>=", ">>>=", "<", "<<", "<=", "<<=",
      "!", "!=", "=", "==", "&", "&&", "&=", "|", "||", "|=", "^", "^=", "~",
      "?"
    */
    public static final Set<String> OPERATOR_TOKEN_DELIMITERS = operatorTokenDelimiters();

    /**
     * https://www.cis.upenn.edu/~matuszek/General/JavaSyntax/parentheses.html
     * any -> brackets
     * () - parentheses
     * {} - curly brackets
     * [] - square brackets
     * <> - angle brackets
     */
    private static final Set<String> NOT_OPERATOR_TOKEN_DELIMITERS = Set.of(
        OPENING_PARENTHESIS, CLOSING_PARENTHESIS,
        OPENING_CURLY_BRACE, CLOSING_CURLY_BRACE,
        OPENING_SQUARE_BRACKET, CLOSING_SQUARE_BRACKET,
        ":",
        ",",
        ";"
    );

    public static final Set<String> SIGNIFICANT_TOKEN_DELIMITERS =
        Stream.of(
            OPERATOR_TOKEN_DELIMITERS.stream(),
            NOT_OPERATOR_TOKEN_DELIMITERS.stream()
        ).flatMap(identity())
            .collect(toUnmodifiableSet());

    public static final Set<String> SIGNIFICANT_TOKEN_DELIMITERS_WITH_COMMENTS =
        Stream.of(
            OPERATOR_TOKEN_DELIMITERS.stream(),
            NOT_OPERATOR_TOKEN_DELIMITERS.stream(),
            Stream.of(START_MULTI_LINE_COMMENT, END_MULTI_LINE_COMMENT, START_SINGLE_LINE_COMMENT)
        ).flatMap(identity())
            .collect(toUnmodifiableSet());

    private Delimiters() {
    }

    private static Set<String> operatorTokenDelimiters() {
        return Stream.of(
            binaryOperatorTokenProvider(),
            unaryOperatorTokenProvider(),
            ternaryOperatorTokenProvider(),
            Stream.of("=")
        ).flatMap(identity())
            .collect(toUnmodifiableSet());
    }

    private static Stream<String> binaryOperatorTokenProvider() {
        return Arrays.stream(BinaryOperator.values())
            .filter(op -> op != BinaryOperator.PREDICATE_TYPEOF)
            .map(BinaryOperator::getCode);
    }

    private static Stream<String> unaryOperatorTokenProvider() {
        return Arrays.stream(UnaryOperator.values())
            .map(UnaryOperator::getCode);
    }

    private static Stream<String> ternaryOperatorTokenProvider() {
        return Stream.of(TernaryConditionalOperator.OPERATOR.getCode());
    }
}
