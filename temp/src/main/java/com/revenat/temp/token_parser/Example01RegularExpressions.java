
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

package com.revenat.temp.token_parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Vitaliy Dragun
 *
 */
public class Example01RegularExpressions {
    private static final String ALL_OPERATORS_PATTERN =
            "\\+{1,2}=?|\\-{1,2}=?|\\*=?|\\/=?|%=?|!=?|>{1,3}=?|<{1,2}=?|&{1,2}=?|\\|{1,2}=?|\\^=?|={1,2}|[()\\=\\[\\]{};,?:~]";

    private static final String WORDS_PATTERN ="\\b\\w+";
    private static final String STRING_LITERAL_PATTERN ="\"([^\"\\\\]|\\\\.)*\"|\'([^\'\\\\]|\\\\.)*\'";

    private static final String OPERATOR_DATA = "+ ++ += - -- -= * *= / /= % %=" +
            "> >> >= >>> >>= >>>= < << <= <<=" +
            "! != = == & && &= | || |= ^ ^= ~" +
            "?" +
            "( )" +
            "{ }" +
            "[ ]" +
            ":" +
            "," +
            ";";
    private static final String WORDS_DATA = "test test123_ 025987test____ _____ 0000";
    private static final String STRING_LITERAL_DATA = "\"test\" 128 not_a_test 'test'  \"'test'\"  '\"test\"' \"\\\"test\\\"\" '\\'test\\''";
    private static final String TEST_DATA = "a + 10 == 20 ? 'yes' : \"no\" // comment here";

    private static final List<String> DATA = List.of(TEST_DATA);

    public static void main(final String[] args) {
        final Pattern pattern = Pattern.compile(ALL_OPERATORS_PATTERN + "|" + WORDS_PATTERN + "|" + STRING_LITERAL_PATTERN);

        for (final String datum  : DATA) {
            final Matcher matcher = pattern.matcher(datum);
            final List<String> tokens = new ArrayList<>();

            while(matcher.find()) {
                final String token = matcher.group();
                tokens.add(token);
            }

            tokens.forEach(token -> System.out.print(token + ","));
            System.out.println();
        }
    }
}
