
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

package com.revenat.javamm.interpreter.component.impl;

import com.revenat.javamm.code.component.ExpressionContext;
import com.revenat.javamm.code.exception.ConfigException;
import com.revenat.javamm.code.fragment.Expression;
import com.revenat.javamm.code.fragment.UpdatableExpression;
import com.revenat.javamm.interpreter.component.ExpressionContextAware;
import com.revenat.javamm.interpreter.component.ExpressionEvaluator;
import com.revenat.javamm.interpreter.component.ExpressionUpdater;

import java.util.Map;
import java.util.Set;
import java.util.function.BinaryOperator;

import static java.lang.String.format;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toUnmodifiableMap;

/**
 * @author Vitaliy Dragun
 *
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ExpressionContextImpl implements ExpressionContext {
    private final Map<Class<? extends Expression>, ExpressionEvaluator> expressionEvaluatorMap;

    private final Map<Class<? extends UpdatableExpression>, ExpressionUpdater> expressionUpdaterMap;

    public ExpressionContextImpl(final Set<ExpressionEvaluator<?>> expressionEvaluators,
                                 final Set<ExpressionUpdater<?>> expressionUpdaters) {
        this.expressionEvaluatorMap = getExpressionEvaluators(expressionEvaluators);
        this.expressionUpdaterMap = getExpressionUpdaters(expressionUpdaters);
    }

    @Override
    public Object getValue(final Expression expression) {
        final ExpressionEvaluator evaluator = getExpressionEvaluatorFor(expression);
        return evaluator.evaluate(expression);
    }

    @Override
    public void setValue(final UpdatableExpression updatableExpression, final Object updatedValue) {
        final ExpressionUpdater updater = getExpressionUpdaterFor(updatableExpression);
        updater.update(updatableExpression, updatedValue);
    }

    private ExpressionUpdater getExpressionUpdaterFor(final UpdatableExpression updatableExpression) {
        final ExpressionUpdater updater = expressionUpdaterMap.get(updatableExpression.getClass());
        if (updater == null) {
            throw configException("ExpressionUpdater not defined for %s", updatableExpression.getClass());
        }
        return updater;
    }

    private ExpressionEvaluator getExpressionEvaluatorFor(final Expression expression) {
        final ExpressionEvaluator evaluator = expressionEvaluatorMap.get(expression.getClass());
        if (evaluator == null) {
            throw configException("ExpressionEvaluator not defined for %s", expression.getClass());
        }
        return evaluator;
    }

    private Map<Class<? extends Expression>, ExpressionEvaluator> getExpressionEvaluators(
                                    final Set<ExpressionEvaluator<?>> expressionEvaluators) {
        return expressionEvaluators.stream()
                .peek(this::setExpressionContextIfAware)
                .collect(
                        toUnmodifiableMap(
                                ExpressionEvaluator::getExpressionClass, identity(), checkForEvaluatorDuplicates()));
    }

    private Map<Class<? extends UpdatableExpression>, ExpressionUpdater> getExpressionUpdaters(
            final Set<ExpressionUpdater<?>> expressionUpdaters) {
        return expressionUpdaters.stream()
                .peek(this::setExpressionContextIfAware)
                .collect(
                        toUnmodifiableMap(
                                ExpressionUpdater::getExpressionClass, identity(), checkForUpdaterDuplicates()));
    }

    private void setExpressionContextIfAware(final Object instance) {
        if (instance instanceof ExpressionContextAware) {
            ((ExpressionContextAware) instance).setExpressionContext(this);
        }
    }

    private BinaryOperator<ExpressionEvaluator> checkForEvaluatorDuplicates() {
        return (e1, e2) -> {
            throw configException("Duplicate of ExpressionEvaluator found: expression=%s, evaluator1=%s, evaluator2=%s",
                    e1.getExpressionClass().getName(), e1, e2);
        };
    }

    private BinaryOperator<ExpressionUpdater> checkForUpdaterDuplicates() {
        return (e1, e2) -> {
            throw configException("Duplicate of ExpressionUpdater found: expression=%s, updater1=%s, updater2=%s",
                    e1.getExpressionClass().getName(), e1, e2);
        };
    }

    private ConfigException configException(final String msg, final Object... args) {
        return new ConfigException(format(msg, args));
    }
}
