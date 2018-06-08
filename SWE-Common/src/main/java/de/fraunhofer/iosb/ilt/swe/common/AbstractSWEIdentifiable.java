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

import de.fraunhofer.iosb.ilt.configurable.editor.EditorMap;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorString;

/**
 *
 * @author Hylke van der Schaaf
 */
public abstract class AbstractSWEIdentifiable extends AbstractSWE {

    private String identifier;
    private String label;
    private String description;

    private EditorMap configEditor;
    private EditorString editorIdentifier;
    private EditorString editorLabel;
    private EditorString editorDescription;

    @Override
    public EditorMap<?> getConfigEditor(Void context, Void edtCtx) {
        if (configEditor == null) {
            configEditor = new EditorMap();

            editorIdentifier = new EditorString(identifier, 1, "Identifier", "A unique identifier.");
            configEditor.addOption("identifier", editorIdentifier, true);

            editorLabel = new EditorString(label, 1, "Label", "A short descriptive name.");
            configEditor.addOption("label", editorLabel, true);

            editorDescription = new EditorString(description, 3, "Description", "A longer description.");
            configEditor.addOption("description", editorDescription, true);
        }
        return configEditor;
    }

}
