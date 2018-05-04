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
import de.fraunhofer.iosb.ilt.sta.model.Task;
import de.fraunhofer.iosb.ilt.sta.model.TaskingCapability;
import de.fraunhofer.iosb.ilt.sta.model.Thing;
import de.fraunhofer.iosb.ilt.sta.model.core.EntitySet;
import de.fraunhofer.iosb.ilt.sta.model.core.EntitySetImpl;
import de.fraunhofer.iosb.ilt.sta.path.EntityType;
import java.util.HashMap;
import java.util.Map;

/**
 * Builder class for Datastream objects.
 *
 * @author jab
 */
public class TaskingCapabilityBuilder extends AbstractEntityBuilder<TaskingCapability, TaskingCapabilityBuilder> {

    private String description;
    private String name;
    private Map<String, Object> properties;
    private Map<String, Object> taskingParameters;
    private Actuator actuator;
    private EntitySet<Task> tasks;
    private Thing thing;

    public TaskingCapabilityBuilder() {
        properties = new HashMap<>();
        taskingParameters = new HashMap<>();
        tasks = new EntitySetImpl<>(EntityType.Task);
    }

    public TaskingCapabilityBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public TaskingCapabilityBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public TaskingCapabilityBuilder setProperties(Map<String, Object> properties) {
        this.properties = properties;
        return this;
    }

    public TaskingCapabilityBuilder addProperty(String name, Object value) {
        this.properties.put(name, value);
        return this;
    }

    public TaskingCapabilityBuilder setTaskingParameters(Map<String, Object> taskingParameters) {
        this.taskingParameters = taskingParameters;
        return this;
    }

    public TaskingCapabilityBuilder setActuator(Actuator actuator) {
        this.actuator = actuator;
        return this;
    }

    public TaskingCapabilityBuilder setTasks(EntitySet<Task> tasks) {
        this.tasks = tasks;
        return this;
    }

    public TaskingCapabilityBuilder addTask(Task task) {
        this.tasks.add(task);
        return this;
    }

    public TaskingCapabilityBuilder setThing(Thing thing) {
        this.thing = thing;
        return this;
    }

    @Override
    protected TaskingCapabilityBuilder getThis() {
        return this;
    }

    @Override
    public TaskingCapability build() {
        TaskingCapability taskingCapability = new TaskingCapability(
                id,
                selfLink,
                navigationLink,
                name,
                description,
                properties,
                taskingParameters,
                actuator,
                thing,
                tasks);
        taskingCapability.setExportObject(isExportObject());
        return taskingCapability;
    }

}
