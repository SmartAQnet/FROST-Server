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

import de.fraunhofer.iosb.ilt.sta.model.core.AbstractEntity;
import de.fraunhofer.iosb.ilt.sta.model.core.EntitySet;
import de.fraunhofer.iosb.ilt.sta.model.core.EntitySetImpl;
import de.fraunhofer.iosb.ilt.sta.model.core.Id;
import de.fraunhofer.iosb.ilt.sta.path.EntityType;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author jab
 */
public class Actuator extends AbstractEntity {

    private String name;
    private String description;
    private Map<String, Object> properties;
    private String encodingType;
    private Object metadata;

    private EntitySet<TaskingCapability> taskingCapabilities;

    private boolean setName;
    private boolean setDescription;
    private boolean setProperties;
    private boolean setEncodingType;
    private boolean setMetadata;

    public Actuator() {
        taskingCapabilities = new EntitySetImpl<>(EntityType.TaskingCapability);
    }

    public Actuator(Id id,
            String selfLink,
            String navigationLink,
            String name,
            String description,
            Map<String, Object> properties,
            String encodingType,
            Object metadata,
            EntitySet<TaskingCapability> taskingCapabilities) {
        super(id, selfLink, navigationLink);
        this.name = name;
        this.description = description;
        if (properties != null && !properties.isEmpty()) {
            this.properties = new HashMap<>(properties);
        }
        this.encodingType = encodingType;
        this.metadata = metadata;
        this.taskingCapabilities = taskingCapabilities;
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.Actuator;
    }

    @Override
    public void setEntityPropertiesSet() {
        setName = true;
        setDescription = true;
        setProperties = true;
        setEncodingType = true;
        setMetadata = true;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public String getEncodingType() {
        return encodingType;
    }

    public Object getMetadata() {
        return metadata;
    }

    public EntitySet<TaskingCapability> getTaskingCapabilities() {
        return taskingCapabilities;
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

    public boolean isSetEncodingType() {
        return setEncodingType;
    }

    public boolean isSetMetadata() {
        return setMetadata;
    }

    public void setName(String name) {
        this.name = name;
        setName = true;
    }

    public void setDescription(String description) {
        this.description = description;
        setDescription = true;
    }

    public void setProperties(Map<String, Object> properties) {
        if (properties != null && properties.isEmpty()) {
            properties = null;
        }
        this.properties = properties;
        setProperties = true;
    }

    public void setEncodingType(String encodingType) {
        this.encodingType = encodingType;
        setEncodingType = true;
    }

    public void setMetadata(Object metadata) {
        this.metadata = metadata;
        setMetadata = true;
    }

    public void setTaskingCapabilities(EntitySet<TaskingCapability> taskingCapabilities) {
        this.taskingCapabilities = taskingCapabilities;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.name);
        hash = 23 * hash + Objects.hashCode(this.description);
        hash = 23 * hash + Objects.hashCode(this.properties);
        hash = 23 * hash + Objects.hashCode(this.encodingType);
        hash = 23 * hash + Objects.hashCode(this.metadata);
        hash = 23 * hash + Objects.hashCode(this.taskingCapabilities);
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
        final Actuator other = (Actuator) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (!Objects.equals(this.encodingType, other.encodingType)) {
            return false;
        }
        if (!Objects.equals(this.properties, other.properties)) {
            return false;
        }
        if (!Objects.equals(this.metadata, other.metadata)) {
            return false;
        }
        if (!Objects.equals(this.taskingCapabilities, other.taskingCapabilities)) {
            return false;
        }
        return true;
    }
}
