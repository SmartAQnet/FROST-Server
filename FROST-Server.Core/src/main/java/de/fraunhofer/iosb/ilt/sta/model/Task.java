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

import de.fraunhofer.iosb.ilt.sta.model.builder.TaskingCapabilityBuilder;
import de.fraunhofer.iosb.ilt.sta.model.core.AbstractEntity;
import de.fraunhofer.iosb.ilt.sta.model.core.Id;
import de.fraunhofer.iosb.ilt.sta.model.ext.TimeInstant;
import de.fraunhofer.iosb.ilt.sta.path.EntityPathElement;
import de.fraunhofer.iosb.ilt.sta.path.EntitySetPathElement;
import de.fraunhofer.iosb.ilt.sta.path.EntityType;
import de.fraunhofer.iosb.ilt.sta.path.ResourcePathElement;
import de.fraunhofer.iosb.ilt.sta.util.IncompleteEntityException;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jab
 */
public class Task extends AbstractEntity {

    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Task.class);
    private TimeInstant creationTime;
    private Map<String, Object> taskingParameters;
    private TaskingCapability taskingCapability;

    private boolean setCreationTime;
    private boolean setTaskingParameters;
    private boolean setTaskingCapability;

    public Task() {
    }

    public Task(Id id,
            String selfLink,
            String navigationLink,
            TimeInstant creationTime,
            Map<String, Object> taskingParameters,
            TaskingCapability taskingCapability) {
        super(id, selfLink, navigationLink);
        this.creationTime = creationTime;
        if (taskingParameters != null && !taskingParameters.isEmpty()) {
            this.taskingParameters = taskingParameters;
        }
        this.taskingCapability = taskingCapability;
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.Task;
    }

    @Override
    public void complete(EntitySetPathElement containingSet) throws IncompleteEntityException {
        ResourcePathElement parent = containingSet.getParent();
        if (parent != null && parent instanceof EntityPathElement) {
            EntityPathElement parentEntity = (EntityPathElement) parent;
            Id parentId = parentEntity.getId();
            if (parentId != null) {
                switch (parentEntity.getEntityType()) {
                    case TaskingCapability:
                        setTaskingCapability(new TaskingCapabilityBuilder().setId(parentId).build());
                        LOGGER.debug("Set taskingCapabilityId to {}.", parentId);
                        break;
                }
            }
        }

        super.complete(containingSet);
    }

    @Override
    public void setEntityPropertiesSet() {
        setCreationTime = true;
        setTaskingParameters = true;
    }

    public TimeInstant getCreationTime() {
        return creationTime;
    }

    public Map<String, Object> getTaskingParameters() {
        return taskingParameters;
    }

    public TaskingCapability getTaskingCapability() {
        return taskingCapability;
    }

    public boolean isSetCreationTime() {
        return setCreationTime;
    }

    public boolean isSetTaskingParameters() {
        return setTaskingParameters;
    }

    public boolean isSetTaskingCapability() {
        return setTaskingCapability;
    }

    public void setCreationTime(TimeInstant creationTime) {
        this.creationTime = creationTime;
        setCreationTime = true;
    }

    public void setTaskingParameters(Map<String, Object> taskingParameters) {
        this.taskingParameters = taskingParameters;
        setTaskingParameters = true;
    }

    public void setTaskingCapability(TaskingCapability taskingCapability) {
        this.taskingCapability = taskingCapability;
        setTaskingCapability = true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.creationTime);
        hash = 59 * hash + Objects.hashCode(this.taskingParameters);
        hash = 59 * hash + Objects.hashCode(this.taskingCapability);
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
        final Task other = (Task) obj;
        if (!Objects.equals(this.creationTime, other.creationTime)) {
            return false;
        }
        if (!Objects.equals(this.taskingParameters, other.taskingParameters)) {
            return false;
        }
        if (!Objects.equals(this.taskingCapability, other.taskingCapability)) {
            return false;
        }
        return true;
    }

}
