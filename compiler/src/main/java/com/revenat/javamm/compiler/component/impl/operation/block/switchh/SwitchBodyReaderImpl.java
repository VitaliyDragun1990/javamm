
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

package com.revenat.javamm.compiler.component.impl.operation.block.switchh;

import com.revenat.javamm.code.fragment.SourceLine;
import com.revenat.javamm.code.fragment.operation.SwitchBodyEntry;
import com.revenat.javamm.code.fragment.operation.SwitchCaseEntry;
import com.revenat.javamm.compiler.component.BlockOperationReader;
import com.revenat.javamm.compiler.component.error.JavammLineSyntaxError;
import com.revenat.javamm.compiler.component.error.JavammStructSyntaxError;
import com.revenat.javamm.compiler.component.impl.operation.SwitchBodyEntryReader;
import com.revenat.javamm.compiler.component.impl.operation.SwitchBodyEntryValidator;
import com.revenat.javamm.compiler.component.impl.operation.SwitchBodyReader;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import static com.revenat.javamm.code.syntax.Keywords.SWITCH;
import static com.revenat.javamm.compiler.component.impl.util.SyntaxParseUtils.isClosingBlockOperation;
import static com.revenat.javamm.compiler.component.impl.util.SyntaxValidationUtils.validateThatLineContainsClosingCurlyBraceOnly;
import static java.lang.String.format;

/**
 * @author Vitaliy Dragun
 */
public class SwitchBodyReaderImpl implements SwitchBodyReader {
    private final List<SwitchBodyEntryReader<?>> entryReaders;

    private final SwitchBodyEntryValidator entryValidator;

    public SwitchBodyReaderImpl(final List<SwitchBodyEntryReader<?>> entryReaders,
                                final SwitchBodyEntryValidator entryValidator) {
        this.entryReaders = List.copyOf(entryReaders);
        this.entryValidator = entryValidator;
    }

    @Override
    public List<SwitchBodyEntry> read(final String moduleName,
                                      final ListIterator<SourceLine> sourceCode,
                                      final BlockOperationReader blockOperationReader) {
        final Set<SwitchBodyEntry> entries = new LinkedHashSet<>();

        while (sourceCode.hasNext()) {
            final SourceLine current = sourceCode.next();

            if (isEndOfSwitchBody(current)) {
                return List.copyOf(entries);
            } else {
                readCurrentEntry(current, sourceCode, blockOperationReader, entries);
            }
        }

        throw switchBodyLacksClosingBraceError(moduleName);
    }

    private void readCurrentEntry(final SourceLine currentLine,
                                  final ListIterator<SourceLine> sourceCode,
                                  final BlockOperationReader blockOperationReader,
                                  final Set<SwitchBodyEntry> entries) {
        final SwitchBodyEntryReader<?> entryReader = getEntryReader(currentLine);
        final SwitchBodyEntry entry = entryReader.read(currentLine, sourceCode, blockOperationReader);
        validate(entry, entries, currentLine);
        entries.add(entry);
    }

    private JavammStructSyntaxError switchBodyLacksClosingBraceError(final String moduleName) {
        return new JavammStructSyntaxError(
            format("'}' expected to close '%s' block statement at the end of file", SWITCH),
            moduleName);
    }

    private boolean isEndOfSwitchBody(final SourceLine sourceLine) {
        if (isClosingBlockOperation(sourceLine)) {
            validateThatLineContainsClosingCurlyBraceOnly(sourceLine);
            return true;
        }
        return false;
    }

    private void validate(final SwitchBodyEntry entry,
                          final Set<SwitchBodyEntry> entries,
                          final SourceLine sourceLine) {
        validateNotDuplicate(entries, entry, sourceLine);
        entryValidator.validate(entry);
    }

    private void validateNotDuplicate(final Set<SwitchBodyEntry> entries,
                                      final SwitchBodyEntry entry,
                                      final SourceLine sourceLine) {
        if (entries.contains(entry)) {
            throw duplicateEntryException(entry, sourceLine);
        }
    }


    private JavammLineSyntaxError duplicateEntryException(final SwitchBodyEntry violator, final SourceLine sourceLine) {
        if (violator.isDefault()) {
            return new JavammLineSyntaxError(sourceLine, "Duplicate default label");
        } else {
            return new JavammLineSyntaxError(sourceLine,
                "Duplicate case label '%s'", ((SwitchCaseEntry) violator).getExpression());
        }
    }

    private SwitchBodyEntryReader<?> getEntryReader(final SourceLine sourceLine) {
        return entryReaders.stream()
            .filter(r -> r.canRead(sourceLine))
            .findFirst()
            .orElseThrow(() -> new JavammLineSyntaxError(sourceLine, "Unsupported '%s' child statement", SWITCH));
    }
}
