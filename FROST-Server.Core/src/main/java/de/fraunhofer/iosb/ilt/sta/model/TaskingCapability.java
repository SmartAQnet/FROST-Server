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
package de.fraunhofer.iosb.ilt.sta.model;

import de.fraunhofer.iosb.ilt.sta.model.builder.ActuatorBuilder;
import de.fraunhofer.iosb.ilt.sta.model.builder.ThingBuilder;
import de.fraunhofer.iosb.ilt.sta.model.core.AbstractEntity;
import de.fraunhofer.iosb.ilt.sta.model.core.EntitySet;
import de.fraunhofer.iosb.ilt.sta.model.core.EntitySetImpl;
import de.fraunhofer.iosb.ilt.sta.model.core.Id;
import de.fraunhofer.iosb.ilt.sta.path.EntityPathElement;
import de.fraunhofer.iosb.ilt.sta.path.EntitySetPathElement;
import de.fraunhofer.iosb.ilt.sta.path.EntityType;
import de.fraunhofer.iosb.ilt.sta.path.ResourcePathElement;
import de.fraunhofer.iosb.ilt.sta.util.IncompleteEntityException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jab
 */
public class TaskingCapability extends AbstractEntity {

    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskingCapability.class);
    private String name;
    private String description;
    private Map<String, Object> properties;
    private Map<String, Object> taskingParameters;

    private Actuator actuator;
    private Thing thing;
    private EntitySet<Task> tasks;

    private boolean setName;
    private boolean setDescription;
    private boolean setProperties;
    private boolean setTaskingParameters;
    private boolean setActuator;
    private boolean setThing;

    public TaskingCapability() {
        this.tasks = new EntitySetImpl<>(EntityType.Task);
    }

    public TaskingCapability(Id id,
            String selfLink,
            String navigationLink,
            String name,
            String description,
            Map<String, Object> properties,
            Map<String, Object> taskingParameters,
            Actuator actuator,
            Thing thing,
            EntitySet<Task> tasks) {
        super(id, selfLink, navigationLink);
        this.name = name;
        this.description = description;
        if (properties != null && !properties.isEmpty()) {
            this.properties = new HashMap<>(properties);
        }
        if (taskingParameters != null && !taskingParameters.isEmpty()) {
            this.taskingParameters = taskingParameters;
        }
        this.actuator = actuator;
        this.thing = thing;
        this.tasks = tasks;
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.TaskingCapability;
    }

    @Override
    public void complete(EntitySetPathElement containingSet) throws IncompleteEntityException {
        ResourcePathElement parent = containingSet.getParent();
        if (parent != null && parent instanceof EntityPathElement) {
            EntityPathElement parentEntity = (EntityPathElement) parent;
            Id parentId = parentEntity.getId();
            if (parentId != null) {
                switch (parentEntity.getEntityType()) {
                    case Actuator:
                        setActuator(new ActuatorBuilder().setId(parentId).build());
                        LOGGER.debug("Set actuatorId to {}.", parentId);
                        break;

                    case Thing:
                        setThing(new ThingBuilder().setId(parentId).build());
                        LOGGER.debug("Set thingId to {}.", parentId);
                        break;
                }
            }
        }

        super.complete(containingSet);
    }

    @Override
    public void setEntityPropertiesSet() {
        setDescription = true;
        setName = true;
        setProperties = true;
        setTaskingParameters = true;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public Map<String, Object> getTaskingParameters() {
        return taskingParameters;
    }

    public Actuator getActuator() {
        return actuator;
    }

    public Thing getThing() {
        return thing;
    }

    public EntitySet<Task> getTasks() {
        return tasks;
    }

    public void setDescription(String description) {
        this.description = description;
        setDescription = true;
    }

    public void setName(String name) {
        this.name = name;
        setName = true;
    }

    public void setProperties(Map<String, Object> properties) {
        if (properties != null && properties.isEmpty()) {
            properties = null;
        }
        this.properties = properties;
        setProperties = true;
    }

    public void setTaskingParameters(Map<String, Object> taskingParameters) {
        this.taskingParameters = taskingParameters;
        setTaskingParameters = true;
    }

    public void setActuator(Actuator actuator) {
        this.actuator = actuator;
        setActuator = true;
    }

    /**
     * @param thing the thing to set
     */
    public void setThing(Thing thing) {
        this.thing = thing;
        setThing = true;
    }

    public void setTasks(EntitySet<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.name);
        hash = 59 * hash + Objects.hashCode(this.description);
        hash = 59 * hash + Objects.hashCode(this.properties);
        hash = 59 * hash + Objects.hashCode(this.taskingParameters);
        hash = 59 * hash + Objects.hashCode(this.actuator);
        hash = 59 * hash + Objects.hashCode(this.thing);
        hash = 59 * hash + Objects.hashCode(this.tasks);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TaskingCapability other = (TaskingCapability) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (!Objects.equals(this.properties, other.properties)) {
            return false;
        }
        if (!Objects.equals(this.taskingParameters, other.taskingParameters)) {
            return false;
        }
        if (!Objects.equals(this.actuator, other.actuator)) {
            return false;
        }
        if (!Objects.equals(this.thing, other.thing)) {
            return false;
        }
        if (!Objects.equals(this.tasks, other.tasks)) {
            return false;
        }
        return true;
    }

    public boolean isSetName() {
        return setName;
    }

    public boolean isSetDescription() {
        return setDescription;
    }

    public boolean isSetProperties() {
        return setProperties;
    }

    public boolean isSetTaskingParameters() {
        return setTaskingParameters;
    }

    public boolean isSetActuator() {
        return setActuator;
    }

    public boolean isSetThing() {
        return setThing;
    }

}
