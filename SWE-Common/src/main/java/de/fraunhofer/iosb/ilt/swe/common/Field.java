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
package de.fraunhofer.iosb.ilt.swe.common;

import com.google.gson.JsonElement;

import de.fraunhofer.iosb.ilt.configurable.Configurable;
import de.fraunhofer.iosb.ilt.configurable.EditorFactory;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorList;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorMap;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorString;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorSubclass;

/**
 *
 * @author scf
 */
public class Field implements Configurable<Void, Void> {

    private EditorMap configEditor;
    private EditorString editorName;
    private EditorList<AbstractDataComponent, EditorSubclass<Void, Void, AbstractDataComponent>> editorField;

    @Override
    public void configure(JsonElement config, Void context, Void edtCtx) {
        getConfigEditor(context, edtCtx).setConfig(config);
    }

    @Override
    public EditorMap<?> getConfigEditor(Void context, Void edtCtx) {
        if (configEditor == null) {
            configEditor = new EditorMap();

            editorName = new EditorString("NameMe!", 1, "Name", "A unique name.");
            configEditor.addOption("name", editorName, false);

            EditorFactory<EditorSubclass<Void, Void, AbstractDataComponent>> factory
                    = () -> new EditorSubclass(null, null, AbstractDataComponent.class, true, "type");
            editorField = new EditorList<>(factory, "Type", "The type of this field");
            configEditor.addOption("type", editorField, false);
        }
        return configEditor;
    }

}
