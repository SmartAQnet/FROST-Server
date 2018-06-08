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
import de.fraunhofer.iosb.ilt.swe.common.constraint.AllowedTokens;

import java.util.Map;

import de.fraunhofer.iosb.ilt.configurable.editor.EditorSubclass;

/**
 *
 * @author Hylke van der Schaaf
 */
public class Category extends AbstractSimpleComponent {

    private String value;
    private AllowedTokens constraint;
    //TODO
    private Map<String, String> codeSpace;

    private EditorString editorValue;
    private EditorSubclass<Void, Void, AllowedTokens> editorConstraint;

    @Override
    public EditorMap<?> getConfigEditor(Void context, Void edtCtx) {
        EditorMap<?> configEditor = super.getConfigEditor(context, edtCtx);

        if (editorValue == null) {
            editorValue = new EditorString(value, 1, "Value", "The value of this component.");
            configEditor.addOption("value", editorValue, true);

            editorConstraint = new EditorSubclass<>(context, edtCtx, AllowedTokens.class, "Constraint", "The constraints put on the value of this component.");
            editorConstraint.setNameField("type");
            editorConstraint.setMerge(true);
            configEditor.addOption("constraint", editorConstraint, true);
            if (constraint != null) {
                editorConstraint.setValue(constraint);
            }
        }

        return configEditor;
    }

}
