/*
 * Copyright (c) 2019. http://devonline.academy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.revenat.temp.junit;

/**
 * @author devonline
 * @link http://devonline.academy/javamm
 */
public final class DivBinaryCalculatorMainVersion1 {

    private DivBinaryCalculatorMainVersion1() {
    }

    public static void main(final String[] args) {
        final BinaryCalculator calculator = new DivBinaryCalculator();


        System.out.println("12 / 0 = " + calculator.calculate(12, 0));
        System.out.println("12 / 4 = " + calculator.calculate(12, 4));
    }
}