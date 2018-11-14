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

import de.fraunhofer.iosb.ilt.sta.model.core.EntitySet;
import de.fraunhofer.iosb.ilt.sta.model.core.EntitySetImpl;
import de.fraunhofer.iosb.ilt.sta.model.core.Id;
import de.fraunhofer.iosb.ilt.sta.model.core.NamedEntity;
import de.fraunhofer.iosb.ilt.sta.path.EntityPathElement;
import de.fraunhofer.iosb.ilt.sta.path.EntitySetPathElement;
import de.fraunhofer.iosb.ilt.sta.path.EntityType;
import de.fraunhofer.iosb.ilt.sta.path.ResourcePathElement;
import de.fraunhofer.iosb.ilt.sta.util.IncompleteEntityException;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jab, scf
 */
public class Location extends NamedEntity {

    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Location.class);
    private String encodingType;
    private Object location;
    private EntitySet<HistoricalLocation> historicalLocations; // 0..*
    private EntitySet<Thing> things;

    private boolean setEncodingType;
    private boolean setLocation;

    public Location() {
        this(null);
    }

    public Location(Id id) {
        super(id);
        this.things = new EntitySetImpl<>(EntityType.THING);
        this.historicalLocations = new EntitySetImpl<>(EntityType.HISTORICALLOCATION);
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.LOCATION;
    }

    @Override
    public void complete(EntitySetPathElement containingSet) throws IncompleteEntityException {
        ResourcePathElement parent = containingSet.getParent();
        if (parent instanceof EntityPathElement) {
            EntityPathElement parentEntity = (EntityPathElement) parent;
            Id parentId = parentEntity.getId();
            if (parentId != null) {
                if (parentEntity.getEntityType() == EntityType.THING) {
                    getThings().add(new Thing(parentId));
                    LOGGER.debug("Added thingId to {}.", parentId);
                } else {
                    LOGGER.error("Incorrect 'parent' entity type for {}: {}", getEntityType(), parentEntity.getEntityType());
                }
            }
        }
        super.complete(containingSet);
    }

    @Override
    public void setEntityPropertiesSet() {
        super.setEntityPropertiesSet();
        setEncodingType = true;
        setLocation = true;
    }

    public String getEncodingType() {
        return encodingType;
    }

    public void setEncodingType(String encodingType) {
        this.encodingType = encodingType;
        setEncodingType = encodingType != null;
    }

    public boolean isSetEncodingType() {
        return setEncodingType;
    }

    public Object getLocation() {
        return location;
    }

    public void setLocation(Object location) {
        this.location = location;
        setLocation = location != null;
    }

    public boolean isSetLocation() {
        return setLocation;
    }

    public EntitySet<HistoricalLocation> getHistoricalLocations() {
        return historicalLocations;
    }

    public void setHistoricalLocations(EntitySet<HistoricalLocation> historicalLocations) {
        this.historicalLocations = historicalLocations;
    }

    public EntitySet<Thing> getThings() {
        return things;
    }

    public void setThings(EntitySet<Thing> things) {
        this.things = things;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), encodingType, location, historicalLocations, things);
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
        final Location other = (Location) obj;
        return super.equals(other)
                && Objects.equals(encodingType, other.encodingType)
                && Objects.equals(location, other.location)
                && Objects.equals(historicalLocations, other.historicalLocations)
                && Objects.equals(things, other.things);
    }

}
