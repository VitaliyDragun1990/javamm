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

import com.revenat.javamm.ide.ui.listener.ActionListener;
import com.revenat.javamm.ide.ui.listener.ActionStateManager;

import java.util.Map;

import static com.revenat.javamm.ide.ui.pane.action.state.ActionPaneState.StateName.EDITING;
import static com.revenat.javamm.ide.ui.pane.action.state.ActionPaneState.StateName.INITIAL;
import static com.revenat.javamm.ide.ui.pane.action.state.ActionPaneState.StateName.RUNNING;
import static java.util.Objects.requireNonNull;

/**
 * @author Vitaliy Dragun
 */
public class ActionPaneState implements ActionState {

    static ActionPaneState currentState;

    private static Map<StateName, ActionPaneState> supportedStates;

    final ActionStateManager actionStateManager;

    final ActionListener actionListener;

    ActionPaneState(final ActionStateManager actionStateManager, final ActionListener actionListener) {
        this.actionStateManager = requireNonNull(actionStateManager);
        this.actionListener = requireNonNull(actionListener);
    }

    public static ActionState create(final ActionStateManager actionStateManager, final ActionListener actionListener) {
        supportedStates = registerAllSupportedStates(actionStateManager, actionListener);
        currentState = getStateByName(INITIAL);
        currentState.initialize();
        return new ActionPaneState(actionStateManager, actionListener);
    }

    private static Map<StateName, ActionPaneState> registerAllSupportedStates(final ActionStateManager stateManager,
                                                                              final ActionListener actionListener) {
        return Map.of(INITIAL, new InitialState(stateManager, actionListener),
                      EDITING, new EditingState(stateManager, actionListener),
                      RUNNING, new RunningState(stateManager, actionListener)
            );
    }

    @Override
    public void onNew() {
        currentState.onNew();
    }

    @Override
    public void onOpen() {
        currentState.onOpen();
    }

    @Override
    public void onSave() {
        currentState.onSave();
    }

    @Override
    public void onExit() {
        currentState.onExit();
    }

    @Override
    public void onUndo() {
        currentState.onUndo();
    }

    @Override
    public void onRedo() {
        currentState.onRedo();
    }

    @Override
    public void onFormat() {
        currentState.onFormat();
    }

    @Override
    public void onRun() {
        currentState.onRun();
    }

    @Override
    public void onRunCompleted() {
        // do nothing for now
    }

    @Override
    public void onTerminate() {
        currentState.onTerminate();
    }

    static void setCurrentStateByName(StateName stateName) {
        currentState = getStateByName(stateName);
    }

    private static ActionPaneState getStateByName(StateName stateName) {
        return supportedStates.get(stateName);
    }

    protected void initialize() {
        // override in subclasses if certain initialization logic is required
    }

    enum StateName {
        INITIAL,
        EDITING,
        RUNNING;
    }
}
