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
package de.fraunhofer.iosb.ilt.sta.mqtt.subscription;

import de.fraunhofer.iosb.ilt.sta.json.serialize.EntityFormatter;
import de.fraunhofer.iosb.ilt.sta.model.core.Entity;
import de.fraunhofer.iosb.ilt.sta.model.core.Id;
import de.fraunhofer.iosb.ilt.sta.path.EntityPathElement;
import de.fraunhofer.iosb.ilt.sta.path.EntityProperty;
import de.fraunhofer.iosb.ilt.sta.path.Property;
import de.fraunhofer.iosb.ilt.sta.path.PropertyPathElement;
import de.fraunhofer.iosb.ilt.sta.path.ResourcePath;
import de.fraunhofer.iosb.ilt.sta.persistence.PersistenceManager;
import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

/**
 *
 * @author jab
 */
public class PropertySubscription extends AbstractSubscription {

    private Property property;
    private Predicate<? super Entity> matcher;

    public PropertySubscription(String topic, ResourcePath path, String serviceRootUrl) {
        super(topic, path, serviceRootUrl);
        init();
    }

    private void init() {
        if (!SubscriptionFactory.getQueryFromTopic(topic).isEmpty()) {
            throw new IllegalArgumentException("Invalid subscription to: '" + topic + "': query options not allowed for subscription on a property.");
        }
        final int size = path.size();
        entityType = ((EntityPathElement) path.get(size - 2)).getEntityType();
        property = ((PropertyPathElement) path.get(size - 1)).getProperty();
        if (path.getIdentifiedElement() != null) {
            Id id = path.getIdentifiedElement().getId();
            matcher = x -> x.getProperty(EntityProperty.ID).equals(id);
        }
        generateFilter(2);
    }

    @Override
    public boolean matches(PersistenceManager persistenceManager, Entity newEntity, Set<Property> fields) {
        if (matcher != null && !matcher.test(newEntity)) {
            return false;
        }
        if (fields == null || !fields.contains(property)) {
            return false;
        }

        return super.matches(persistenceManager, newEntity, fields);
    }

    @Override
    public String doFormatMessage(Entity entity) throws IOException {
        HashSet<String> propNames = new HashSet<>(1);
        propNames.add(property.getJsonName());
        entity.setSelectedPropertyNames(propNames);
        return EntityFormatter.writeEntity(entity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), property);
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
        final PropertySubscription other = (PropertySubscription) obj;
        return super.equals(obj)
                && Objects.equals(this.property, other.property);
    }

}
