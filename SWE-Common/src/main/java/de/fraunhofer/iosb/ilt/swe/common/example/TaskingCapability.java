/*
 * Copyright (C) 2018 Fraunhofer Institut IOSB, Fraunhoferstr. 1, D 76131
 * Karlsruhe, Germany.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fraunhofer.iosb.ilt.swe.common.example;

import com.google.gson.JsonElement;

import de.fraunhofer.iosb.ilt.configurable.ConfigEditor;
import de.fraunhofer.iosb.ilt.configurable.Configurable;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorMap;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorSubclass;
import de.fraunhofer.iosb.ilt.swe.common.AbstractDataComponent;

/**
 * @author scf
 */
public class TaskingCapability implements Configurable<Void, Void> {

    private AbstractDataComponent component;

    private EditorSubclass<Void, Void, AbstractDataComponent> configEditor;

    @Override
    public void configure(JsonElement config, Void context, Void edtCtx) {
        getConfigEditor(context, edtCtx).setConfig(config);
        component = configEditor.getValue();
    }

    @Override
    public ConfigEditor<?> getConfigEditor(Void context, Void edtCtx) {
        if (configEditor == null) {
            configEditor = new EditorSubclass(context, edtCtx, AbstractDataComponent.class, "taskingParameters", "The parameters for this taskingCapability");
            configEditor.setMerge(true);
            configEditor.setNameField("type");
        }
        return configEditor;
    }

}
