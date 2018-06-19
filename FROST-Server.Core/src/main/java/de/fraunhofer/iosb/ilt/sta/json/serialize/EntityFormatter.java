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
package de.fraunhofer.iosb.ilt.sta.json.serialize;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import de.fraunhofer.iosb.ilt.sta.formatter.DataArrayResult;
import de.fraunhofer.iosb.ilt.sta.formatter.DataArrayValue;
import de.fraunhofer.iosb.ilt.sta.json.deserialize.custom.GeoJsonDeserializier;
import de.fraunhofer.iosb.ilt.sta.json.mixin.ActuatorMixIn;
import de.fraunhofer.iosb.ilt.sta.json.mixin.DatastreamMixIn;
import de.fraunhofer.iosb.ilt.sta.json.mixin.EntitySetResultMixIn;
import de.fraunhofer.iosb.ilt.sta.json.mixin.FeatureOfInterestMixIn;
import de.fraunhofer.iosb.ilt.sta.json.mixin.HistoricalLocationMixIn;
import de.fraunhofer.iosb.ilt.sta.json.mixin.LocationMixIn;
import de.fraunhofer.iosb.ilt.sta.json.mixin.MultiDatastreamMixIn;
import de.fraunhofer.iosb.ilt.sta.json.mixin.ObservationMixIn;
import de.fraunhofer.iosb.ilt.sta.json.mixin.ObservedPropertyMixIn;
import de.fraunhofer.iosb.ilt.sta.json.mixin.SensorMixIn;
import de.fraunhofer.iosb.ilt.sta.json.mixin.TaskMixIn;
import de.fraunhofer.iosb.ilt.sta.json.mixin.TaskingCapabilityMixIn;
import de.fraunhofer.iosb.ilt.sta.json.mixin.ThingMixIn;
import de.fraunhofer.iosb.ilt.sta.json.mixin.UnitOfMeasurementMixIn;
import de.fraunhofer.iosb.ilt.sta.json.serialize.custom.CustomSerializationManager;
import de.fraunhofer.iosb.ilt.sta.model.Actuator;
import de.fraunhofer.iosb.ilt.sta.model.Datastream;
import de.fraunhofer.iosb.ilt.sta.model.FeatureOfInterest;
import de.fraunhofer.iosb.ilt.sta.model.HistoricalLocation;
import de.fraunhofer.iosb.ilt.sta.model.Location;
import de.fraunhofer.iosb.ilt.sta.model.MultiDatastream;
import de.fraunhofer.iosb.ilt.sta.model.Observation;
import de.fraunhofer.iosb.ilt.sta.model.ObservedProperty;
import de.fraunhofer.iosb.ilt.sta.model.Sensor;
import de.fraunhofer.iosb.ilt.sta.model.Task;
import de.fraunhofer.iosb.ilt.sta.model.TaskingCapability;
import de.fraunhofer.iosb.ilt.sta.model.Thing;
import de.fraunhofer.iosb.ilt.sta.model.core.Entity;
import de.fraunhofer.iosb.ilt.sta.model.core.EntitySet;
import de.fraunhofer.iosb.ilt.sta.model.ext.EntitySetResult;
import de.fraunhofer.iosb.ilt.sta.model.ext.UnitOfMeasurement;
import java.io.IOException;

/**
 * Enables serialization of entities as JSON.
 *
 * @author jab
 */
public class EntityFormatter {

    private static ObjectMapper objectMapperInstance;

    public static ObjectMapper getObjectMapper() {
        if (objectMapperInstance == null) {
            initObjectMapper();
        }
        return objectMapperInstance;
    }

    private static synchronized void initObjectMapper() {
        if (objectMapperInstance == null) {
            objectMapperInstance = createObjectMapper();
        }
    }

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.setPropertyNamingStrategy(new EntitySetCamelCaseNamingStrategy());
        mapper.addMixIn(Actuator.class, ActuatorMixIn.class);
        mapper.addMixIn(Datastream.class, DatastreamMixIn.class);
        mapper.addMixIn(MultiDatastream.class, MultiDatastreamMixIn.class);
        mapper.addMixIn(FeatureOfInterest.class, FeatureOfInterestMixIn.class);
        mapper.addMixIn(HistoricalLocation.class, HistoricalLocationMixIn.class);
        mapper.addMixIn(Location.class, LocationMixIn.class);
        mapper.addMixIn(Observation.class, ObservationMixIn.class);
        mapper.addMixIn(ObservedProperty.class, ObservedPropertyMixIn.class);
        mapper.addMixIn(Sensor.class, SensorMixIn.class);
        mapper.addMixIn(Task.class, TaskMixIn.class);
        mapper.addMixIn(TaskingCapability.class, TaskingCapabilityMixIn.class);
        mapper.addMixIn(Thing.class, ThingMixIn.class);
        mapper.addMixIn(UnitOfMeasurement.class, UnitOfMeasurementMixIn.class);
        mapper.addMixIn(EntitySetResult.class, EntitySetResultMixIn.class);

        SimpleModule module = new SimpleModule();
        GeoJsonSerializer geoJsonSerializer = new GeoJsonSerializer();
        for (String encodingType : GeoJsonDeserializier.ENCODINGS) {
            CustomSerializationManager.getInstance().registerSerializer(encodingType, geoJsonSerializer);
        }

        module.addSerializer(Entity.class, new EntitySerializer());
        module.addSerializer(EntitySetResult.class, new EntitySetResultSerializer());
        module.addSerializer(DataArrayValue.class, new DataArrayValueSerializer());
        module.addSerializer(DataArrayResult.class, new DataArrayResultSerializer());
        mapper.registerModule(module);
        return mapper;
    }

    private EntityFormatter() {
    }

    public static <T extends Entity> String writeEntity(T entity) throws IOException {
        return getObjectMapper().writeValueAsString(entity);
    }

    public static String writeEntityCollection(EntitySet entityCollection) throws IOException {
        return getObjectMapper().writeValueAsString(new EntitySetResult(entityCollection));
    }

    public static String writeObject(Object object) throws IOException {
        return getObjectMapper().writeValueAsString(object);
    }
}
