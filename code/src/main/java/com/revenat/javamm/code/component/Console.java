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

package com.revenat.javamm.code.component;

/**
 * @author Vitaliy Dragun
 */
public interface Console {

    Console DEFAULT = new Console() {
        @Override
        public void outPrintln(final Object value) {
            System.out.println(value);
        }

        @Override
        public void errPrintln(final String message) {
            System.err.println(message);
        }
    };

    void outPrintln(Object value);

    void errPrintln(String message);
}
