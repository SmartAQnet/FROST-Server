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

import de.fraunhofer.iosb.ilt.configurable.AbstractConfigurable;
import de.fraunhofer.iosb.ilt.configurable.annotations.ConfigurableClass;
import de.fraunhofer.iosb.ilt.configurable.annotations.ConfigurableField;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorClass;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorDouble;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorInt;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorList;
import de.fraunhofer.iosb.ilt.swe.common.util.RealPair;
import java.util.List;

/**
 *
 * @author Hylke van der Schaaf
 */
@ConfigurableClass(jsonName = "AllowedValues")
public class AllowedValues extends AbstractConfigurable<Void, Void> {

	@ConfigurableField(editor = EditorList.class, optional = true,
			label = "Values",
			description = "The values that the user can choose from.")
	@EditorList.EdOptsList(editor = EditorDouble.class)
	@EditorDouble.EdOptsDouble(min = Double.NEGATIVE_INFINITY, max = Double.POSITIVE_INFINITY, step = Double.MIN_VALUE, dflt = 0)
	private List<Double> value;

	@ConfigurableField(editor = EditorList.class, optional = true,
			label = "Intervals",
			description = "The intervals that the value must fall in.")
	@EditorList.EdOptsList(editor = EditorClass.class)
	@EditorClass.EdOptsClass(clazz = RealPair.class)
	private List<RealPair> interval;

	@ConfigurableField(editor = EditorInt.class, optional = true,
			label = "Figures",
			description = "The number of significant figures.")
	@EditorInt.EdOptsInt(min = 0, max = 100, step = 1, dflt = 1)
	private Integer significantFigures;

	public List<Double> getValue() {
		return value;
	}

	public List<RealPair> getInterval() {
		return interval;
	}

	public Integer getSignificantFigures() {
		return significantFigures;
	}

}
