/**
 * Copyright (C) 2012 WRML.org <mark@wrml.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wrml.cli.gui;

import org.wrml.cli.gui.base.BaseAction;

public abstract class GuiAction extends BaseAction {

    private final Gui _Gui;

    public GuiAction(String title, final Gui gui) {
        super(title);
        _Gui = gui;
    }

    public Gui getGui() {
        return _Gui;
    }

}