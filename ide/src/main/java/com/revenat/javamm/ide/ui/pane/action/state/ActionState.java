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

package com.revenat.javamm.ide.ui.pane.action.state;

/**
 * @author Vitaliy Dragun
 */
public interface ActionState {

    void onEvent(ActionEvent actionEvent);

    enum ActionEvent {
        NEW,
        OPEN,
        SAVE,
        EXIT,
        UNDO,
        REDO,
        FORMAT,
        RUN,
        RUN_COMPLETED,
        TERMINATED,
        ALL_TABS_CLOSED,
        TAB_CONTENT_CHANGED_WITH_ONLY_UNDO_AVAILABLE,
        TAB_CONTENT_CHANGED_WITH_ONLY_REDO_AVAILABLE,
        TAB_CONTENT_CHANGED_WITH_UNDO_REDO_AVAILABLE,
        TAB_CONTENT_UNCHANGED,
        NEW_TAB_CREATED,
        TAB_CONTENT_SAVED;

        public static ActionEvent tabContentChangedEvent(final boolean undoAvailable,
                                                         final boolean redoAvailable) {
            if (undoAvailable && redoAvailable) {
                return TAB_CONTENT_CHANGED_WITH_UNDO_REDO_AVAILABLE;
            } else if (undoAvailable) {
                return TAB_CONTENT_CHANGED_WITH_ONLY_UNDO_AVAILABLE;
            } else if (redoAvailable) {
                return TAB_CONTENT_CHANGED_WITH_ONLY_REDO_AVAILABLE;
            } else {
                throw new IllegalStateException("Either undo or redo should be available on tabContentChanged event");
            }
        }
    }
}
