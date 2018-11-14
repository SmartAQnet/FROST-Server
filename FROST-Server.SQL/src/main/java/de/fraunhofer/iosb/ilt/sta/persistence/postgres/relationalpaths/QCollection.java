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
package de.fraunhofer.iosb.ilt.sta.persistence.postgres.relationalpaths;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.SimpleExpression;

/**
 * @author scf
 * @param <I> The type of path used for the ID fields.
 * @param <J> The type of the ID fields.
 */
public class QCollection<I extends SimpleExpression<J> & Path<J>, J> {

    public final AbstractQDatastreams<? extends AbstractQDatastreams, I, J> qDatastreams;
    public final AbstractQMultiDatastreams<? extends AbstractQMultiDatastreams, I, J> qMultiDatastreams;
    public final AbstractQThings<? extends AbstractQThings, I, J> qThings;
    public final AbstractQFeatures<? extends AbstractQFeatures, I, J> qFeatures;
    public final AbstractQHistLocations<? extends AbstractQHistLocations, I, J> qHistLocations;
    public final AbstractQLocations<? extends AbstractQLocations, I, J> qLocations;
    public final AbstractQSensors<? extends AbstractQSensors, I, J> qSensors;
    public final AbstractQObservations<? extends AbstractQObservations, I, J> qObservations;
    public final AbstractQObsProperties<? extends AbstractQObsProperties, I, J> qObsProperties;
    public final AbstractQLocationsHistLocations<? extends AbstractQLocationsHistLocations, I, J> qLocationsHistLocations;
    public final AbstractQMultiDatastreamsObsProperties<? extends AbstractQMultiDatastreamsObsProperties, I, J> qMultiDatastreamsObsProperties;
    public final AbstractQThingsLocations<? extends AbstractQThingsLocations, I, J> qThingsLocations;

    public QCollection(
            AbstractQDatastreams<?, I, J> qDs,
            AbstractQFeatures<?, I, J> qFeat,
            AbstractQHistLocations<?, I, J> qHistLoc,
            AbstractQLocations<?, I, J> qLoc,
            AbstractQMultiDatastreams<?, I, J> qMds,
            AbstractQObsProperties<?, I, J> qOps,
            AbstractQObservations<?, I, J> qObs,
            AbstractQSensors<?, I, J> qSnsr,
            AbstractQThings<?, I, J> qThng,
            AbstractQLocationsHistLocations<?, I, J> qLocHistLoc,
            AbstractQMultiDatastreamsObsProperties<?, I, J> qMdsObsProps,
            AbstractQThingsLocations<?, I, J> qThngLoc) {
        qDatastreams = qDs;
        qMultiDatastreams = qMds;
        qThings = qThng;
        qFeatures = qFeat;
        qHistLocations = qHistLoc;
        qLocations = qLoc;
        qSensors = qSnsr;
        qObservations = qObs;
        qObsProperties = qOps;
        qLocationsHistLocations = qLocHistLoc;
        qMultiDatastreamsObsProperties = qMdsObsProps;
        qThingsLocations = qThngLoc;
    }

}
