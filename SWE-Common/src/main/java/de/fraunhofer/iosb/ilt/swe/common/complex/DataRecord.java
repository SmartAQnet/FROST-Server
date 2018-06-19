/*
 * Copyright (C) 2018 Fraunhofer IOSB.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library. If not, see <http://www.gnu.org/licenses/>.
 */
package de.fraunhofer.iosb.ilt.swe.common.complex;

import de.fraunhofer.iosb.ilt.configurable.annotations.ConfigurableClass;
import de.fraunhofer.iosb.ilt.configurable.annotations.ConfigurableField;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorClass;
import de.fraunhofer.iosb.ilt.configurable.editor.EditorList;
import de.fraunhofer.iosb.ilt.swe.common.AbstractDataComponent;
import java.util.List;

/**
 *
 * @author Hylke van der Schaaf
 */
@ConfigurableClass(jsonName = "DataRecord")
public class DataRecord extends AbstractDataComponent {

	@ConfigurableField(editor = EditorList.class,
			jsonField = "field",
			label = "Fields",
			description = "The fields in this DataRecord.")
	@EditorList.EdOptsList(editor = EditorClass.class)
	@EditorClass.EdOptsClass(clazz = Field.class)
	private List<Field> fields;

	public List<Field> getFields() {
		return fields;
	}

}
