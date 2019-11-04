
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

import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.SourceLine;

import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * @author Vitaliy Dragun
 *
 */
public class SwitchOperation extends AbstractOperation {

    private final Expression condition;

    private final List<SwitchChildOperation> childOperations;

    public SwitchOperation(final SourceLine sourceLine,
                           final Expression condition,
                           final List<SwitchChildOperation> childOperations) {
        super(sourceLine);
        this.childOperations = List.copyOf(childOperations);
        this.condition = requireNonNull(condition);
    }

    public Expression getCondition() {
        return condition;
    }

    public List<SwitchChildOperation> getChildOperations() {
        return childOperations;
    }

//    public static void main(final String[] args) {
//        final String hello ="hello";
//
//        switch (hello) {
//        default : {
//            System.out.println("loozer");
//        }
//        case "hellos": {
//            System.out.println("Hello");
//        }
//        case "hi": {
//            System.out.println("hi");
//        }
//        }
//    }
}
