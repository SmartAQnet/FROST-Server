/*
 * Copyright (C) 2016 Fraunhofer Institut IOSB, Fraunhoferstr. 1, D 76131
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
package de.fraunhofer.iosb.ilt.sta.model.builder;

import de.fraunhofer.iosb.ilt.sta.model.Actuator;
import de.fraunhofer.iosb.ilt.sta.model.TaskingCapability;
import de.fraunhofer.iosb.ilt.sta.model.core.EntitySet;
import de.fraunhofer.iosb.ilt.sta.model.core.EntitySetImpl;
import de.fraunhofer.iosb.ilt.sta.path.EntityType;
import java.util.HashMap;
import java.util.Map;

/**
 * Builder class for Sensor objects.
 *
 * @author jab
 */
public class ActuatorBuilder extends AbstractEntityBuilder<Actuator, ActuatorBuilder> {

    private String name;
    private String description;
    private String encodingType;
    private Object metadata;
    private Map<String, Object> properties;
    private EntitySet<TaskingCapability> taskingCapabilities;

    public ActuatorBuilder() {
        properties = new HashMap<>();
        taskingCapabilities = new EntitySetImpl<>(EntityType.TaskingCapability);
    }

    public ActuatorBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ActuatorBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public ActuatorBuilder setEncodingType(String encodingType) {
        this.encodingType = encodingType;
        return this;
    }

    public ActuatorBuilder setMetadata(Object metadata) {
        this.metadata = metadata;
        return this;
    }

    public ActuatorBuilder setProperties(Map<String, Object> properties) {
        this.properties = properties;
        return this;
    }

    public ActuatorBuilder addProperty(String name, Object value) {
        this.properties.put(name, value);
        return this;
    }

    public ActuatorBuilder setTaskingCapabilities(EntitySet<TaskingCapability> taskingCapabilities) {
        this.taskingCapabilities = taskingCapabilities;
        return this;
    }

    public ActuatorBuilder addTaskingCapability(TaskingCapability taskingCapability) {
        this.taskingCapabilities.add(taskingCapability);
        return this;
    }

    @Override
    protected ActuatorBuilder getThis() {
        return this;
    }

    @Override
    public Actuator build() {
        Actuator actuator = new Actuator(
                id,
                selfLink,
                navigationLink,
                name,
                description,
                properties,
                encodingType,
                metadata,
                taskingCapabilities);
        actuator.setExportObject(isExportObject());
        return actuator;
    }

}
