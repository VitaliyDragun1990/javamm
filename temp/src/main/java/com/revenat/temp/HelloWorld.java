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


package com.revenat.temp;

/**
 * @author devonline
 * @link http://devonline.academy/javamm
 */
public final class HelloWorld {

    private HelloWorld() {
    }

    public static void main(final String[] args) {
        System.out.println("Hello world");

        final int a = 1;

       final int b =  100 + ( a > 0 ? 10 > 20 ? 10 : 20  : 1_000 );
       System.out.println(10 + ((10 > b) ? 10 : true ? 20 : 30));

       System.out.println(b);
    }
}