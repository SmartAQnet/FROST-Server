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

import de.fraunhofer.iosb.ilt.configurable.editor.EditorBoolean;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorMap;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorString;

/**
 *
 * @author Hylke van der Schaaf
 */
public abstract class AbstractDataComponent extends AbstractSWEIdentifiable {

    private String definition;
    private Boolean optional;
    private Boolean updatable;

    private EditorString editorDefinition;
    private EditorBoolean editorOptional;
    private EditorBoolean editorUpdatable;

    @Override
    public EditorMap<?> getConfigEditor(Void context, Void edtCtx) {
        EditorMap<?> configEditor = super.getConfigEditor(context, edtCtx);

        if (editorDefinition == null) {
            editorDefinition = new EditorString(definition, 1, "Definition", "A scoped namethat maps to a controlled term defined in a (web accessible) dictionary, registry or ontology.");
            configEditor.addOption("definition", editorDefinition, true);

            editorOptional = new EditorBoolean(updatable == null ? false : updatable, "Optional", "A flag indicating if the component value can be omitted.");
            configEditor.addOption("optional", editorOptional, true);

            editorUpdatable = new EditorBoolean(updatable == null ? false : updatable, "Updatable", "A flag indicating if the component value is fixed or can be updated.");
            configEditor.addOption("updatable", editorUpdatable, true);
        }

        return configEditor;
    }

}
