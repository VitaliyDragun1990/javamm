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

import java.time.Duration;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.revenat.javamm.code.syntax.Keywords.ALL_KEYWORDS;
import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;


/**
 * Copied from https://github.com/FXMisc/RichTextFX/blob/master/richtextfx-demos/src/main/java/org/fxmisc/richtext/demo/JavaKeywordsAsyncDemo.java
 *
 * @author Vitaliy Dragun
 */
public class JavammAsyncSyntaxHighlighter implements SyntaxHighlighter {

    private static final String[] KEYWORDS = ALL_KEYWORDS.toArray(new String[0]);

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";

    private static final String PAREN_PATTERN = "[()]";

    private static final String BRACE_PATTERN = "[{}]";

    private static final String BRACKET_PATTERN = "[\\[\\]]";

    private static final String SEMICOLON_PATTERN = ";";

    private static final String DOUBLE_QUOTED_STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";

    private static final String SINGLE_QUOTED_STRING_PATTERN = "'([^'\\\\]|\\\\.)*'";

    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

    private static final Pattern PATTERN = Pattern.compile(
        "(?<KEYWORD>" + KEYWORD_PATTERN + ")" +
            "|(?<PAREN>" + PAREN_PATTERN + ")" +
            "|(?<BRACE>" + BRACE_PATTERN + ")" +
            "|(?<BRACKET>" + BRACKET_PATTERN + ")" +
            "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")" +
            "|(?<STRING>" + DOUBLE_QUOTED_STRING_PATTERN + "|" + SINGLE_QUOTED_STRING_PATTERN + ")" +
            "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );

    private static final String SAMPLE_CODE = buildSampleCode();

    private final CodeArea codeArea;

    private final ExecutorService executorService;

    private Subscription cleanupWhenDone;

    JavammAsyncSyntaxHighlighter(final CodeArea codeArea, final ExecutorService executorService) {
        this.codeArea = codeArea;
        this.executorService = executorService;
    }

    @Override
    public void enable() {
        cleanupWhenDone = codeArea.multiPlainChanges()
            .successionEnds(Duration.ofMillis(500))
            .supplyTask(this::computeHighlightingAsync)
            .awaitLatest(codeArea.multiPlainChanges())
            .filterMap(t -> {
                if (t.isSuccess()) {
                    return Optional.of(t.get());
                } else {
                    t.getFailure().printStackTrace();
                    return Optional.empty();
                }
            })
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
        return matchForGroup("KEYWORD", matcher)
            .or(() -> matchForGroup("PAREN", matcher))
            .or(() -> matchForGroup("BRACE", matcher))
            .or(() -> matchForGroup("BRACKET", matcher))
            .or(() -> matchForGroup("SEMICOLON", matcher))
            .or(() -> matchForGroup("STRING", matcher))
            .or(() -> matchForGroup("COMMENT", matcher))
            .orElseThrow();
    }

    private Optional<String> matchForGroup(final String groupName, final Matcher matcher) {
        return Optional.ofNullable(matcher.group(groupName) != null ? groupName.toLowerCase() : null);
    }

    private static String buildSampleCode() {
        return String.join("\n", "    /*",
            "     * multi-line comment",
            "     */",
            "    function main() {",
            "        var a = 1",
            "        final b = a + 4",
            "        a++",
            "        final text = 'Hello world'",
            "        // single-line comment",
            "        for(var i = 0; /* // test comment */ i < b; /* test comment */ i ++) {",
            "            if(i < 3) {",
            "                println('i < 3 -> ' + text)",
            "            }",
            "            else {",
            "                println(\"else -> \" + text)",
            "            }",
            "        }",
            "",
            "        println(a typeof void)",
            "        println(sum(a, b))",
            "    }",
            "",
            "   function sum(a, b) {",
            "       return a + b",
            "   }");
    }
}
