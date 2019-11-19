
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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Vitaliy Dragun
 *
 */
@SuppressWarnings("CheckStyle")
public class Example04SumOfNumbers {

    private Example04SumOfNumbers() {
    }

    public static void main(final String[] args) {
        final List<Integer> numbers = IntStream.range(0, 100).boxed().collect(Collectors.toUnmodifiableList());

        //Imperative
        int sum = 0;
        for (final Integer number : numbers) {
            sum += number;
        }
        System.out.println(sum);

        //Functional: reduce
        System.out.println(numbers.stream().reduce(Integer::sum).orElse(0));

        //Functional: sum
        System.out.println(numbers.stream().mapToInt(value -> value).sum());
    }
}
