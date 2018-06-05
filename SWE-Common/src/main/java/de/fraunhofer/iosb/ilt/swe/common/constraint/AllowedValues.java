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
package de.fraunhofer.iosb.ilt.swe.common.constraint;

import com.google.gson.JsonElement;
import de.fraunhofer.iosb.ilt.configurable.ConfigEditor;
import de.fraunhofer.iosb.ilt.configurable.Configurable;
import de.fraunhofer.iosb.ilt.configurable.EditorFactory;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorList;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorMap;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorString;
import java.util.List;

/**
 *
 * @author Hylke van der Schaaf
 */
public class AllowedValues implements Configurable<Void, Void> {

	private List<Double> value;
	private List<RealPair> interval;
	private Integer significantFigures;

	private EditorMap configEditor;
	private EditorString editorPattern;
	private EditorList<String, EditorString> editorValue;

	@Override
	public void configure(JsonElement config, Void context, Void edtCtx) {
		getConfigEditor(context, edtCtx).setConfig(config);
	}

	@Override
	public ConfigEditor<?> getConfigEditor(Void context, Void edtCtx) {
		if (configEditor == null) {
			configEditor = new EditorMap();

			editorPattern = new EditorString(pattern, 1, "Pattern", "The regex(?) pattern that the value must match.");
			configEditor.addOption("pattern", editorPattern, true);

			EditorFactory<EditorString> factory = () -> new EditorString("", 1);
			editorValue = new EditorList(factory, "Value", "The values that the value can choose from.");
			configEditor.addOption("value", editorValue, true);
		}
		return configEditor;
	}

}
