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
package de.fraunhofer.iosb.ilt.sta.persistence.postgres;

import de.fraunhofer.iosb.ilt.sta.model.core.Id;
import de.fraunhofer.iosb.ilt.sta.path.EntityProperty;
import de.fraunhofer.iosb.ilt.sta.path.EntityType;
import de.fraunhofer.iosb.ilt.sta.path.ResourcePath;
import de.fraunhofer.iosb.ilt.sta.path.ResourcePathVisitor;
import de.fraunhofer.iosb.ilt.sta.query.Query;
import de.fraunhofer.iosb.ilt.sta.settings.PersistenceSettings;
import java.util.Map;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.jooq.impl.TableImpl;

/**
 *
 * @author scf
 */
public interface PathSqlBuilder extends ResourcePathVisitor {

    /**
     * A class that keeps track of the latest table that was joined.
     */
    public interface TableRef {

        public EntityType getType();

        public TableImpl<?> getqPath();

        public void clear();

        public TableRef copy();

        public boolean isEmpty();
    }

    public SelectJoinStep<Record> buildFor(EntityType entityType, Id id, DSLContext dslContext, PersistenceSettings settings);

    public SelectJoinStep<Record> buildFor(ResourcePath path, Query query, DSLContext dslContext, PersistenceSettings settings);

    public void queryEntityType(EntityType type, Id id, TableRef last);

    public Map<String, Field<?>> expressionsForProperty(EntityProperty property, TableImpl<?> qPath, Map<String, Field<?>> target);
}
