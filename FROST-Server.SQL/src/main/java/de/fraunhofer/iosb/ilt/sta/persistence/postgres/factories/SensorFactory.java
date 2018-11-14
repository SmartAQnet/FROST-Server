/*
 * Copyright (C) 2018 Fraunhofer Institut IOSB, Fraunhoferstr. 1, D 76131
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
package de.fraunhofer.iosb.ilt.sta.persistence.postgres.factories;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.SQLInsertClause;
import com.querydsl.sql.dml.SQLUpdateClause;
import de.fraunhofer.iosb.ilt.sta.messagebus.EntityChangedMessage;
import de.fraunhofer.iosb.ilt.sta.model.Datastream;
import de.fraunhofer.iosb.ilt.sta.model.MultiDatastream;
import de.fraunhofer.iosb.ilt.sta.model.Sensor;
import de.fraunhofer.iosb.ilt.sta.path.EntityProperty;
import de.fraunhofer.iosb.ilt.sta.path.EntityType;
import de.fraunhofer.iosb.ilt.sta.path.Property;
import de.fraunhofer.iosb.ilt.sta.persistence.postgres.DataSize;
import de.fraunhofer.iosb.ilt.sta.persistence.postgres.EntityFactories;
import static de.fraunhofer.iosb.ilt.sta.persistence.postgres.EntityFactories.CAN_NOT_BE_NULL;
import static de.fraunhofer.iosb.ilt.sta.persistence.postgres.EntityFactories.CHANGED_MULTIPLE_ROWS;
import static de.fraunhofer.iosb.ilt.sta.persistence.postgres.EntityFactories.NO_ID_OR_NOT_FOUND;
import de.fraunhofer.iosb.ilt.sta.persistence.postgres.PostgresPersistenceManager;
import de.fraunhofer.iosb.ilt.sta.persistence.postgres.Utils;
import de.fraunhofer.iosb.ilt.sta.persistence.postgres.relationalpaths.AbstractQDatastreams;
import de.fraunhofer.iosb.ilt.sta.persistence.postgres.relationalpaths.AbstractQMultiDatastreams;
import de.fraunhofer.iosb.ilt.sta.persistence.postgres.relationalpaths.AbstractQSensors;
import de.fraunhofer.iosb.ilt.sta.persistence.postgres.relationalpaths.QCollection;
import de.fraunhofer.iosb.ilt.sta.query.Query;
import de.fraunhofer.iosb.ilt.sta.util.IncompleteEntityException;
import de.fraunhofer.iosb.ilt.sta.util.NoSuchEntityException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hylke van der Schaaf
 * @param <I> The type of path used for the ID fields.
 * @param <J> The type of the ID fields.
 */
public class SensorFactory<I extends SimpleExpression<J> & Path<J>, J> implements EntityFactory<Sensor, I, J> {

    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SensorFactory.class);

    private final EntityFactories<I, J> entityFactories;
    private final AbstractQSensors<?, I, J> qInstance;
    private final QCollection<I, J> qCollection;

    public SensorFactory(EntityFactories<I, J> factories, AbstractQSensors<?, I, J> qInstance) {
        this.entityFactories = factories;
        this.qInstance = qInstance;
        this.qCollection = factories.qCollection;
    }

    @Override
    public Sensor create(Tuple tuple, Query query, DataSize dataSize) {
        Set<Property> select = query == null ? Collections.emptySet() : query.getSelect();
        Sensor entity = new Sensor();
        entity.setName(tuple.get(qInstance.name));
        entity.setDescription(tuple.get(qInstance.description));
        entity.setEncodingType(tuple.get(qInstance.encodingType));
        J id = entityFactories.getIdFromTuple(tuple, qInstance.getId());
        if (id != null) {
            entity.setId(entityFactories.idFromObject(id));
        }
        if (select.isEmpty() || select.contains(EntityProperty.METADATA)) {
            String metaDataString = tuple.get(qInstance.metadata);
            dataSize.increase(metaDataString == null ? 0 : metaDataString.length());
            entity.setMetadata(metaDataString);
        }
        if (select.isEmpty() || select.contains(EntityProperty.PROPERTIES)) {
            String props = tuple.get(qInstance.properties);
            entity.setProperties(Utils.jsonToObject(props, Map.class));
        }
        return entity;
    }

    @Override
    public boolean insert(PostgresPersistenceManager<I, J> pm, Sensor s) throws NoSuchEntityException, IncompleteEntityException {
        SQLQueryFactory qFactory = pm.createQueryFactory();
        AbstractQSensors<? extends AbstractQSensors, I, J> qs = qCollection.qSensors;
        SQLInsertClause insert = qFactory.insert(qs);
        insert.set(qs.name, s.getName());
        insert.set(qs.description, s.getDescription());
        insert.set(qs.encodingType, s.getEncodingType());
        // TODO: Check metadata serialisation.
        insert.set(qs.metadata, s.getMetadata().toString());
        insert.set(qs.properties, EntityFactories.objectToJson(s.getProperties()));

        entityFactories.insertUserDefinedId(pm, insert, qs.getId(), s);

        J generatedId = insert.executeWithKey(qs.getId());
        LOGGER.debug("Inserted Sensor. Created id = {}.", generatedId);
        s.setId(entityFactories.idFromObject(generatedId));

        // Create new datastreams, if any.
        for (Datastream ds : s.getDatastreams()) {
            ds.setSensor(new Sensor(s.getId()));
            ds.complete();
            pm.insert(ds);
        }

        // Create new multiDatastreams, if any.
        for (MultiDatastream mds : s.getMultiDatastreams()) {
            mds.setSensor(new Sensor(s.getId()));
            mds.complete();
            pm.insert(mds);
        }

        return true;
    }

    @Override
    public EntityChangedMessage update(PostgresPersistenceManager<I, J> pm, Sensor s, J sensorId) throws NoSuchEntityException, IncompleteEntityException {
        SQLQueryFactory qFactory = pm.createQueryFactory();
        AbstractQSensors<? extends AbstractQSensors, I, J> qs = qCollection.qSensors;
        SQLUpdateClause update = qFactory.update(qs);
        EntityChangedMessage message = new EntityChangedMessage();

        if (s.isSetName()) {
            if (s.getName() == null) {
                throw new IncompleteEntityException("name" + CAN_NOT_BE_NULL);
            }
            update.set(qs.name, s.getName());
            message.addField(EntityProperty.NAME);
        }
        if (s.isSetDescription()) {
            if (s.getDescription() == null) {
                throw new IncompleteEntityException(EntityProperty.DESCRIPTION.jsonName + CAN_NOT_BE_NULL);
            }
            update.set(qs.description, s.getDescription());
            message.addField(EntityProperty.DESCRIPTION);
        }
        if (s.isSetEncodingType()) {
            if (s.getEncodingType() == null) {
                throw new IncompleteEntityException("encodingType" + CAN_NOT_BE_NULL);
            }
            update.set(qs.encodingType, s.getEncodingType());
            message.addField(EntityProperty.ENCODINGTYPE);
        }
        if (s.isSetMetadata()) {
            if (s.getMetadata() == null) {
                throw new IncompleteEntityException("metadata" + CAN_NOT_BE_NULL);
            }
            // TODO: Check metadata serialisation.
            update.set(qs.metadata, s.getMetadata().toString());
            message.addField(EntityProperty.METADATA);
        }
        if (s.isSetProperties()) {
            update.set(qs.properties, EntityFactories.objectToJson(s.getProperties()));
            message.addField(EntityProperty.PROPERTIES);
        }

        update.where(qs.getId().eq(sensorId));
        long count = 0;
        if (!update.isEmpty()) {
            count = update.execute();
        }
        if (count > 1) {
            LOGGER.error("Updating Sensor {} caused {} rows to change!", sensorId, count);
            throw new IllegalStateException(CHANGED_MULTIPLE_ROWS);
        }

        linkExistingDatastreams(s, pm, qFactory, sensorId);

        linkExistingMultiDatastreams(s, pm, qFactory, sensorId);

        LOGGER.debug("Updated Sensor {}", sensorId);
        return message;
    }

    private void linkExistingMultiDatastreams(Sensor s, PostgresPersistenceManager<I, J> pm, SQLQueryFactory qFactory, J sensorId) throws NoSuchEntityException {
        for (MultiDatastream mds : s.getMultiDatastreams()) {
            if (mds.getId() == null || !entityFactories.entityExists(pm, mds)) {
                throw new NoSuchEntityException("MultiDatastream" + NO_ID_OR_NOT_FOUND);
            }
            J mdsId = (J) mds.getId().getValue();
            AbstractQMultiDatastreams<? extends AbstractQMultiDatastreams, I, J> qmds = qCollection.qMultiDatastreams;
            long mdsCount = qFactory.update(qmds)
                    .set(qmds.getSensorId(), sensorId)
                    .where(qmds.getId().eq(mdsId))
                    .execute();
            if (mdsCount > 0) {
                LOGGER.debug("Assigned multiDatastream {} to sensor {}.", mdsId, sensorId);
            }
        }
    }

    private void linkExistingDatastreams(Sensor s, PostgresPersistenceManager<I, J> pm, SQLQueryFactory qFactory, J sensorId) throws NoSuchEntityException {
        for (Datastream ds : s.getDatastreams()) {
            if (ds.getId() == null || !entityFactories.entityExists(pm, ds)) {
                throw new NoSuchEntityException("Datastream" + NO_ID_OR_NOT_FOUND);
            }
            J dsId = (J) ds.getId().getValue();
            AbstractQDatastreams<? extends AbstractQDatastreams, I, J> qds = qCollection.qDatastreams;
            long dsCount = qFactory.update(qds)
                    .set(qds.getSensorId(), sensorId)
                    .where(qds.getId().eq(dsId))
                    .execute();
            if (dsCount > 0) {
                LOGGER.debug("Assigned datastream {} to sensor {}.", dsId, sensorId);
            }
        }
    }

    @Override
    public void delete(PostgresPersistenceManager<I, J> pm, J entityId) throws NoSuchEntityException {
        long count = pm.createQueryFactory()
                .delete(qInstance)
                .where(qInstance.getId().eq(entityId))
                .execute();
        if (count == 0) {
            throw new NoSuchEntityException("Sensor " + entityId + " not found.");
        }
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.SENSOR;
    }

    @Override
    public I getPrimaryKey() {
        return qInstance.getId();
    }

}
