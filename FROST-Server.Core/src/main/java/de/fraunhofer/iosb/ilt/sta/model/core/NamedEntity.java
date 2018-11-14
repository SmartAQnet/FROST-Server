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
package de.fraunhofer.iosb.ilt.sta.model.core;

import java.util.Map;
import java.util.Objects;

/**
 * An abstract entity with: name, description and properties.
 *
 * @author jab, scf
 */
public abstract class NamedEntity extends AbstractEntity {

    private String name;
    private String description;
    private Map<String, Object> properties;

    private boolean setName;
    private boolean setDescription;
    private boolean setProperties;

    public NamedEntity(Id id) {
        super(id);
    }

    @Override
    public void setEntityPropertiesSet() {
        setName = true;
        setDescription = true;
        setProperties = true;
    }

    /**
     * @return the Name of the entity.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the Name of the entity.
     *
     * @param name The Name to set.
     */
    public void setName(String name) {
        this.name = name;
        setName = name != null;
    }

    /**
     * @return Flag indicating the Name was set by the user.
     */
    public boolean isSetName() {
        return setName;
    }

    /**
     * @return the Description of the entity.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the Description of the entity.
     *
     * @param description The Description to set.
     */
    public void setDescription(String description) {
        this.description = description;
        setDescription = description != null;
    }

    /**
     * @return Flag indicating the Description was set by the user.
     */
    public boolean isSetDescription() {
        return setDescription;
    }

    /**
     * @return the Properties map of the entity.
     */
    public Map<String, Object> getProperties() {
        return properties;
    }

    /**
     * Set the Properties map of the entity.
     *
     * @param properties The Properties to set. Setting this to an empty map
     * will set the properties to null.
     */
    public void setProperties(Map<String, Object> properties) {
        if (properties != null && properties.isEmpty()) {
            properties = null;
        }
        this.properties = properties;
        setProperties = true;
    }

    /**
     * @return Flag indicating Properties was set by the user.
     */
    public boolean isSetProperties() {
        return setProperties;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, properties);
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
        final NamedEntity other = (NamedEntity) obj;
        return super.equals(other)
                && Objects.equals(this.name, other.name)
                && Objects.equals(this.description, other.description)
                && Objects.equals(this.properties, other.properties);
    }
}
