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

package com.revenat.javamm.ide.component.impl;

import com.revenat.javamm.ide.component.SyntaxHighlighter;
import javafx.concurrent.Task;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;
import org.reactfx.util.Try;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.revenat.javamm.code.syntax.Keywords.ALL_KEYWORDS;
import static java.lang.String.format;
import static java.lang.String.join;
import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static java.util.Map.entry;
import static java.util.stream.Collectors.joining;


/**
 * Copied from https://github.com/FXMisc/RichTextFX/blob/master/richtextfx-demos/src/main/java/org/fxmisc/richtext/demo/JavaKeywordsAsyncDemo.java
 *
 * @author Vitaliy Dragun
 */
public class JavammAsyncSyntaxHighlighter implements SyntaxHighlighter {

    private static final Map<String, String> GROUP_PATTERN_MAP = Map.ofEntries(
        // Keywords set
        entry("KEYWORD", format("\\b(%s)\\b", join("|", ALL_KEYWORDS))),
        // String literals: "..." or "...\n or '...' or '...\n
        entry("STRING", format("%s|%s", "\".*?[\"\\n]", "'.*?['\\n]")),
        // Comments: //... \n or /* ... */ or /* ... end file
        entry("COMMENT", format("%s|%s", "\\[^\n]*", "/\\*(.|\\R)*?(\\*/|\\z)"))
    // () - parentheses set
    //    entry("PAREN", "[()]"),
    // {} -  curly braces set
    //    entry("BRACE", "[{}]"),
    // [] - square brackets set
    //    entry("BRACKET", "[\\[\\]]")
    );

    private static final Pattern PATTERN = mergeAllGroups();

    private static final String SAMPLE_CODE = buildSampleCode();

    private static final int FIFTY_MILLIS = 50;

    private final CodeArea codeArea;

    private final ExecutorService executorService;

    private Subscription cleanupWhenDone;

    JavammAsyncSyntaxHighlighter(final CodeArea codeArea, final ExecutorService executorService) {
        this.codeArea = codeArea;
        this.executorService = executorService;
    }

    private static Pattern mergeAllGroups() {
        return Pattern.compile(
            GROUP_PATTERN_MAP.entrySet()
                .stream().map(e -> format("(?<%s>%s)", e.getKey(), e.getValue()))
                .collect(joining("|"))
        );
    }

    @Override
    public void enable() {
        cleanupWhenDone = codeArea.multiPlainChanges()
            .successionEnds(Duration.ofMillis(FIFTY_MILLIS))
            .supplyTask(this::computeHighlightingAsync)
            .awaitLatest(codeArea.multiPlainChanges())
            .filterMap(Try::toOptional)
            .subscribe(this::applyHighlighting);
        // FIXME remove
        codeArea.replaceText(0, 0, SAMPLE_CODE);
    }

    @Override
    public void disable() {
        cleanupWhenDone.unsubscribe();
    }

    @Override
    public void highlightNow() {
        codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText()));
    }

    private Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
        final String text = codeArea.getText();
        final Task<StyleSpans<Collection<String>>> task = new Task<>() {
            @Override
            protected StyleSpans<Collection<String>> call() {
                return computeHighlighting(text);
            }
        };
        executorService.execute(task);
        return task;
    }

    private void applyHighlighting(final StyleSpans<Collection<String>> highlighting) {
        codeArea.setStyleSpans(0, highlighting);
    }

    private StyleSpans<Collection<String>> computeHighlighting(final String text) {
        final Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        final StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while (matcher.find()) {
            final String styleClass = determineStyleClass(matcher);
            spansBuilder.add(emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    private String determineStyleClass(final Matcher matcher) {
        for (final String group : GROUP_PATTERN_MAP.keySet()) {
            if (matcher.group(group) != null) {
                return group.toLowerCase();
            }
        }
        throw new IllegalStateException("Impossible exception: at least one group should be found," +
            " because matcher.find() returns true");
    }

    private static String buildSampleCode() {
        return
            "/*\n" +
                "* multi-line comment\n" +
                "*/\n" +
                "function main () {\n" +
                "    var a = 1\n" +
                "    final b = a + 4\n" +
                "    a ++\n" +
                "    final text = 'Hello world'\n" +
                "    // single-line comment\n" +
                "    for (var i = 0; /* // test comment */ i < b; /* test comment */ i ++) /* test comment */ {\n" +
                "        if (i < 3) {\n" +
                "            println ('i < 3 -> ' + text)\n" +
                "        }\n" +
                "        else {\n" +
                "            println (\"else -> \" + text)\n" +
                "        }\n" +
                "    }\n" +
                "    \n" +
                "    println (a typeof void)\n" +
                "}\n" +
                "\n" +
                "function sum (a, b) {\n" +
                "    return a + b\n" +
                "}";
    }
}
