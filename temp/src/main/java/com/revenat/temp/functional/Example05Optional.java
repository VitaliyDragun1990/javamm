
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

package com.revenat.temp.functional;

import java.util.Optional;
import java.util.Random;

/**
 * @author Vitaliy Dragun
 *
 */
@SuppressWarnings("CheckStyle")
public class Example05Optional {

    private static final boolean NOT_NULL = new Random().nextBoolean();

    private Example05Optional() {
    }

    public static void main(final String[] args) {
        //Imperative
        final String s1 = nextString1();
        if (s1 != null) {
            System.out.println(s1.length());
        } else {
            System.out.println(-1);
        }

        //Imperative
        final Optional<String> s2Optional = nextString2();
        if (s2Optional.isPresent()) {
            System.out.println(s2Optional.get().length());
        } else {
            System.out.println(-1);
        }

        //Functional
        System.out.println(nextString2().map(String::length).orElse(-1));
    }

    private static String nextString1() {
        return NOT_NULL ? "hello world" : null;
    }

    private static Optional<String> nextString2() {
        return NOT_NULL ? Optional.of("hello world") : Optional.empty();
    }
}
