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

import de.fraunhofer.iosb.ilt.sta.path.CustomProperty;
import de.fraunhofer.iosb.ilt.sta.path.EntityProperty;
import de.fraunhofer.iosb.ilt.sta.path.NavigationProperty;
import de.fraunhofer.iosb.ilt.sta.path.Property;
import de.fraunhofer.iosb.ilt.sta.persistence.postgres.expression.ConstantDateExpression;
import de.fraunhofer.iosb.ilt.sta.persistence.postgres.expression.ConstantGeometryExpression;
import de.fraunhofer.iosb.ilt.sta.persistence.postgres.expression.ConstantNumberExpression;
import de.fraunhofer.iosb.ilt.sta.persistence.postgres.expression.ConstantStringExpression;
import de.fraunhofer.iosb.ilt.sta.persistence.postgres.expression.ConstantTimeExpression;
import de.fraunhofer.iosb.ilt.sta.persistence.postgres.expression.JsonExpressionFactory;
import de.fraunhofer.iosb.ilt.sta.persistence.postgres.expression.ListExpression;
import de.fraunhofer.iosb.ilt.sta.persistence.postgres.expression.StaDateTimeExpression;
import de.fraunhofer.iosb.ilt.sta.persistence.postgres.expression.StaDurationExpression;
import de.fraunhofer.iosb.ilt.sta.persistence.postgres.expression.StaTimeIntervalExpression;
import static de.fraunhofer.iosb.ilt.sta.persistence.postgres.expression.StaTimeIntervalExpression.KEY_TIME_INTERVAL_END;
import static de.fraunhofer.iosb.ilt.sta.persistence.postgres.expression.StaTimeIntervalExpression.KEY_TIME_INTERVAL_START;
import de.fraunhofer.iosb.ilt.sta.persistence.postgres.expression.StringCastExpressionFactory;
import de.fraunhofer.iosb.ilt.sta.persistence.postgres.expression.TimeExpression;
import de.fraunhofer.iosb.ilt.sta.query.OrderBy;
import de.fraunhofer.iosb.ilt.sta.query.expression.ExpressionVisitor;
import de.fraunhofer.iosb.ilt.sta.query.expression.Path;
import de.fraunhofer.iosb.ilt.sta.query.expression.constant.BooleanConstant;
import de.fraunhofer.iosb.ilt.sta.query.expression.constant.DateConstant;
import de.fraunhofer.iosb.ilt.sta.query.expression.constant.DateTimeConstant;
import de.fraunhofer.iosb.ilt.sta.query.expression.constant.DoubleConstant;
import de.fraunhofer.iosb.ilt.sta.query.expression.constant.DurationConstant;
import de.fraunhofer.iosb.ilt.sta.query.expression.constant.GeoJsonConstant;
import de.fraunhofer.iosb.ilt.sta.query.expression.constant.IntegerConstant;
import de.fraunhofer.iosb.ilt.sta.query.expression.constant.IntervalConstant;
import de.fraunhofer.iosb.ilt.sta.query.expression.constant.LineStringConstant;
import de.fraunhofer.iosb.ilt.sta.query.expression.constant.PointConstant;
import de.fraunhofer.iosb.ilt.sta.query.expression.constant.PolygonConstant;
import de.fraunhofer.iosb.ilt.sta.query.expression.constant.StringConstant;
import de.fraunhofer.iosb.ilt.sta.query.expression.constant.TimeConstant;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.arithmetic.Add;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.arithmetic.Divide;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.arithmetic.Modulo;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.arithmetic.Multiply;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.arithmetic.Subtract;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.comparison.Equal;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.comparison.GreaterEqual;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.comparison.GreaterThan;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.comparison.LessEqual;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.comparison.LessThan;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.comparison.NotEqual;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.date.Date;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.date.Day;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.date.FractionalSeconds;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.date.Hour;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.date.MaxDateTime;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.date.MinDateTime;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.date.Minute;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.date.Month;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.date.Now;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.date.Second;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.date.Time;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.date.TotalOffsetMinutes;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.date.Year;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.logical.And;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.logical.Not;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.logical.Or;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.math.Ceiling;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.math.Floor;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.math.Round;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.spatialrelation.GeoDistance;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.spatialrelation.GeoIntersects;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.spatialrelation.GeoLength;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.spatialrelation.STContains;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.spatialrelation.STCrosses;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.spatialrelation.STDisjoint;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.spatialrelation.STEquals;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.spatialrelation.STIntersects;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.spatialrelation.STOverlaps;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.spatialrelation.STRelate;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.spatialrelation.STTouches;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.spatialrelation.STWithin;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.string.Concat;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.string.EndsWith;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.string.IndexOf;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.string.Length;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.string.StartsWith;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.string.Substring;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.string.SubstringOf;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.string.ToLower;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.string.ToUpper;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.string.Trim;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.temporal.After;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.temporal.Before;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.temporal.During;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.temporal.Finishes;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.temporal.Meets;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.temporal.Overlaps;
import de.fraunhofer.iosb.ilt.sta.query.expression.function.temporal.Starts;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.codec.Wkt;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Hylke van der Schaaf
 */
public class PgExpressionHandler implements ExpressionVisitor<Field<?>> {

    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PgExpressionHandler.class);
    private final PathSqlBuilder psb;
    /**
     * The table reference for the main table of the request.
     */
    private final PathSqlBuilder.TableRef tableRef;

    public PgExpressionHandler(PathSqlBuilder psb, PathSqlBuilder.TableRef tableRef) {
        this.psb = psb;
        this.tableRef = tableRef;
    }

    public void addFilterToQuery(de.fraunhofer.iosb.ilt.sta.query.expression.Expression filter, SelectJoinStep<Record> sqlQuery) {
        Field<?> filterExpression = filter.accept(this);
        if (filterExpression instanceof Condition) {
            Condition predicate = (Condition) filterExpression;
            sqlQuery.where(predicate);
            return;
        } else if (filterExpression instanceof ListExpression) {
            ListExpression listExpression = (ListExpression) filterExpression;
            for (Field<?> expression : listExpression.getExpressions().values()) {
                if (expression instanceof Condition) {
                    Condition predicate = (Condition) expression;
                    sqlQuery.where(predicate);
                    return;
                }
            }
        }
        LOGGER.error("Filter is not a predicate but a {}.", filterExpression.getClass().getName());
        throw new IllegalArgumentException("Filter is not a predicate but a " + filterExpression.getClass().getName());
    }

    public void addOrderbyToQuery(OrderBy orderBy, SelectJoinStep<Record> sqlQuery) {
        Field<?> resultExpression = orderBy.getExpression().accept(this);
        if (resultExpression instanceof StaTimeIntervalExpression) {
            StaTimeIntervalExpression ti = (StaTimeIntervalExpression) resultExpression;
            addToQuery(orderBy, ti.getStart(), sqlQuery);
            addToQuery(orderBy, ti.getEnd(), sqlQuery);
        }
        if (resultExpression instanceof StaDurationExpression) {
            StaDurationExpression duration = (StaDurationExpression) resultExpression;
            addToQuery(orderBy, duration.getDuration(), sqlQuery);
        }
        if (resultExpression instanceof StaDateTimeExpression) {
            StaDateTimeExpression dateTime = (StaDateTimeExpression) resultExpression;
            addToQuery(orderBy, dateTime.getExpression(), sqlQuery);
        }
        if (resultExpression instanceof ListExpression) {
            for (Field<?> sqlExpression : ((ListExpression) resultExpression).getExpressionsForOrder().values()) {
                addToQuery(orderBy, sqlExpression, sqlQuery);
            }
        } else {
            addToQuery(orderBy, resultExpression, sqlQuery);
        }
    }

    public void addToQuery(OrderBy orderBy, Field<?> sqlExpression, SelectJoinStep<Record> sqlQuery) {
        if (sqlExpression instanceof ComparableExpressionBase) {
            ComparableExpressionBase comparable = (ComparableExpressionBase) sqlExpression;
            Field<?> projection = sqlQuery.getMetadata().getProjection();
            if (projection instanceof QTuple) {
                QTuple qTuple = (QTuple) projection;
                List<Field<?>> args = new ArrayList<>(qTuple.getArgs());
                args.add(comparable);
                sqlQuery.select(args.toArray(new Expression[args.size()]));
            }

            if (orderBy.getType() == OrderBy.OrderType.Ascending) {
                sqlQuery.orderBy(comparable.asc());
            } else {
                sqlQuery.orderBy(comparable.desc());
            }
        }
    }

    public static <T extends Field<?>> T checkType(Class<T> expectedClazz, Field<?> input, boolean canCast) {
        if (expectedClazz.isAssignableFrom(input.getClass())) {
            LOGGER.debug("Is {}: {} ({} -- {})", expectedClazz.getName(), input, input.getClass().getName(), input.getType().getName());
            return expectedClazz.cast(input);
        } else {
            if (canCast && StringExpression.class.equals(expectedClazz) && input instanceof NumberPath) {
                NumberPath numberPath = (NumberPath) input;
                return (T) numberPath.stringValue();
            }
            LOGGER.debug("Not a {}: {} ({} -- {})", expectedClazz.getName(), input, input.getClass().getName(), input.getType().getName());
            throw new IllegalArgumentException("Could not convert parameter of type " + input.getClass().getName() + " to type " + expectedClazz.getName());
        }
    }

    public static <T extends Field<?>> T getSingleOfType(Class<T> expectedClazz, Field<?> input) {
        if (input instanceof ListExpression) {
            ListExpression listExpression = (ListExpression) input;
            Map<String, Field<?>> expressions = listExpression.getExpressions();
            Collection<Field<?>> values = expressions.values();
            // Two passes, first do an exact check (no casting allowed)
            for (Field<?> subResult : values) {
                try {
                    return checkType(expectedClazz, subResult, false);
                } catch (IllegalArgumentException e) {
                    LOGGER.trace("Parameter not of type {}.", expectedClazz.getName());
                    LOGGER.trace("", e);
                }
            }
            // No exact check. Now check again, but allow casting.
            for (Field<?> subResult : values) {
                try {
                    return checkType(expectedClazz, subResult, true);
                } catch (IllegalArgumentException e) {
                    LOGGER.trace("Parameter not of type {}.", expectedClazz.getName());
                    LOGGER.trace("", e);
                }
            }
            throw new IllegalArgumentException("Non of the entries could be converted to type " + expectedClazz.getName());
        } else {
            return checkType(expectedClazz, input, true);
        }
    }

    @Override
    public Field<?> visit(Path path) {
        PathSqlBuilder.TableRef pathTableRef = tableRef.copy();
        List<Property> elements = path.getElements();
        Field<?> finalExpression = null;
        for (int i = 0; i < elements.size(); i++) {
            Property element = elements.get(i);
            if (element instanceof CustomProperty) {
                if (finalExpression == null) {
                    throw new IllegalArgumentException("CustomProperty must follow an EntityProperty: " + path);
                }
                // generate finalExpression::jsonb#>>'{x,y,z}'
                JsonExpressionFactory jsonFactory = new JsonExpressionFactory(finalExpression);
                for (; i < elements.size(); i++) {
                    jsonFactory.addToPath(elements.get(i).getName());
                }
                return jsonFactory.build();
            } else if (element instanceof EntityProperty) {
                if (finalExpression != null) {
                    throw new IllegalArgumentException("EntityProperty can not follow an other EntityProperty: " + path);
                }
                EntityProperty entityProperty = (EntityProperty) element;
                Map<String, Field<?>> pathExpressions = psb.expressionsForProperty(entityProperty, pathTableRef.getqPath(), new LinkedHashMap<>());
                if (pathExpressions.size() == 1) {
                    finalExpression = pathExpressions.values().iterator().next();
                } else {
                    finalExpression = getSubExpression(elements, i, pathExpressions);
                }
            } else if (element instanceof NavigationProperty) {
                if (finalExpression != null) {
                    throw new IllegalArgumentException("NavigationProperty can not follow an EntityProperty: " + path);
                }
                NavigationProperty navigationProperty = (NavigationProperty) element;
                psb.queryEntityType(navigationProperty.getType(), null, pathTableRef);
            }
        }
        if (finalExpression == null) {
            throw new IllegalArgumentException("Path does not end in an EntityProperty: " + path);
        }
        if (finalExpression instanceof DateTimePath) {
            DateTimePath dateTimePath = (DateTimePath) finalExpression;
            finalExpression = new StaDateTimeExpression(dateTimePath);
        }
        return finalExpression;
    }

    private Field<?> getSubExpression(List<Property> elements, int curIdx, Map<String, Field<?>> pathExpressions) {
        int nextIdx = curIdx + 1;
        if (elements.size() > nextIdx) {
            Property subProperty = elements.get(nextIdx);
            // If the subProperty is unknown, and the expression can be of type JSON,
            // then we assume JSON.
            if (!pathExpressions.containsKey(subProperty.getName()) && pathExpressions.containsKey("j")) {
                return pathExpressions.get("j");
            }
            // We can not accept json, so the subProperty must a known name.
            // We consume the subProperty. If the pathExpressions contains the property, there is no need to look further.
            elements.remove(nextIdx);
            return pathExpressions.get(subProperty.getName());
        } else {
            if (pathExpressions.containsKey(KEY_TIME_INTERVAL_START)
                    && pathExpressions.containsKey(KEY_TIME_INTERVAL_END)) {
                return new StaTimeIntervalExpression(pathExpressions);
            }
            return new ListExpression(pathExpressions);
        }
    }

    public Field<?>[] findPair(Field<?> p1, Field<?> p2) {
        Field<?>[] result = new Field<?>[2];
        try {
            result[0] = getSingleOfType(NumberExpression.class, p1);
            result[1] = getSingleOfType(NumberExpression.class, p2);
            return result;
        } catch (IllegalArgumentException e) {
        }
        try {
            result[0] = getSingleOfType(BooleanExpression.class, p1);
            result[1] = getSingleOfType(BooleanExpression.class, p2);
            return result;
        } catch (IllegalArgumentException e) {
        }
        // If both are strings, use strings.
        boolean firstIsString = false;
        try {
            result[0] = getSingleOfType(StringExpression.class, p1);
            firstIsString = true;
            result[1] = getSingleOfType(StringExpression.class, p2);
            return result;
        } catch (IllegalArgumentException e) {
        }
        // If one of the two is a string, cast the other
        if (firstIsString) {
            result[1] = StringCastExpressionFactory.build(p2);
            return result;
        } else {
            try {
                result[1] = getSingleOfType(StringExpression.class, p2);
                result[0] = StringCastExpressionFactory.build(p1);
                return result;
            } catch (IllegalArgumentException e) {
            }
        }

        result[0] = getSingleOfType(ComparableExpression.class, p1);
        result[1] = getSingleOfType(ComparableExpression.class, p2);
        return result;
    }

    @Override
    public Field<?> visit(BooleanConstant node) {
        return node.getValue() ? Expressions.TRUE : Expressions.FALSE;
    }

    @Override
    public Field<?> visit(DateConstant node) {
        LocalDate date = node.getValue();
        Calendar instance = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        instance.set(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth());
        ConstantDateExpression constant = new ConstantDateExpression(new java.sql.Date(instance.getTimeInMillis()));
        return constant;
    }

    @Override
    public Field<?> visit(DateTimeConstant node) {
        DateTime value = node.getValue();
        DateTimeZone zone = value.getZone();
        return new StaDateTimeExpression(new Timestamp(value.getMillis()), zone == DateTimeZone.UTC);
    }

    @Override
    public Field<?> visit(DoubleConstant node) {
        return new ConstantNumberExpression(node.getValue());
    }

    @Override
    public Field<?> visit(DurationConstant node) {
        return new StaDurationExpression(node);
    }

    @Override
    public Field<?> visit(IntervalConstant node) {
        Interval value = node.getValue();
        return new StaTimeIntervalExpression(
                new Timestamp(value.getStartMillis()),
                new Timestamp(value.getEndMillis())
        );
    }

    @Override
    public Field<?> visit(IntegerConstant node) {
        ConstantNumberExpression constant = new ConstantNumberExpression(node.getValue());
        return constant;
    }

    @Override
    public Field<?> visit(LineStringConstant node) {
        Geometry geom = fromGeoJsonConstant(node);
        return new ConstantGeometryExpression(geom);
    }

    @Override
    public Field<?> visit(PointConstant node) {
        Geometry geom = fromGeoJsonConstant(node);
        return new ConstantGeometryExpression(geom);
    }

    @Override
    public Field<?> visit(PolygonConstant node) {
        Geometry geom = fromGeoJsonConstant(node);
        return new ConstantGeometryExpression(geom);
    }

    private Geometry fromGeoJsonConstant(GeoJsonConstant node) {
        if (node.getValue().getCrs() == null) {
            return Wkt.fromWkt("SRID=4326;" + node.getSource());
        }
        return Wkt.fromWkt(node.getSource());
    }

    @Override
    public Field<?> visit(StringConstant node) {
        ConstantStringExpression constant = new ConstantStringExpression(node.getValue());
        return constant;
    }

    @Override
    public Field<?> visit(TimeConstant node) {
        LocalTime time = node.getValue();
        Calendar instance = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        instance.set(1970, 1, 1, time.getHourOfDay(), time.getMinuteOfHour(), time.getSecondOfMinute());
        ConstantTimeExpression constant = new ConstantTimeExpression(new java.sql.Time(instance.getTimeInMillis()));
        return constant;
    }

    @Override
    public Field<?> visit(Before node) {
        List<de.fraunhofer.iosb.ilt.sta.query.expression.Expression> params = node.getParameters();
        Field<?> p1 = params.get(0).accept(this);
        Field<?> p2 = params.get(1).accept(this);
        TimeExpression d1 = getSingleOfType(TimeExpression.class, p1);
        TimeExpression d2 = getSingleOfType(TimeExpression.class, p2);
        return d1.before(d2);
    }

    @Override
    public Field<?> visit(After node) {
        List<de.fraunhofer.iosb.ilt.sta.query.expression.Expression> params = node.getParameters();
        Field<?> p1 = params.get(0).accept(this);
        Field<?> p2 = params.get(1).accept(this);
        TimeExpression d1 = getSingleOfType(TimeExpression.class, p1);
        TimeExpression d2 = getSingleOfType(TimeExpression.class, p2);
        return d1.after(d2);
    }

    @Override
    public Field<?> visit(Meets node) {
        List<de.fraunhofer.iosb.ilt.sta.query.expression.Expression> params = node.getParameters();
        Field<?> p1 = params.get(0).accept(this);
        Field<?> p2 = params.get(1).accept(this);
        TimeExpression d1 = getSingleOfType(TimeExpression.class, p1);
        TimeExpression d2 = getSingleOfType(TimeExpression.class, p2);
        return d1.meets(d2);
    }

    @Override
    public Field<?> visit(During node) {
        List<de.fraunhofer.iosb.ilt.sta.query.expression.Expression> params = node.getParameters();
        Field<?> p1 = params.get(0).accept(this);
        Field<?> p2 = params.get(1).accept(this);
        if (p2 instanceof StaTimeIntervalExpression) {
            StaTimeIntervalExpression ti2 = (StaTimeIntervalExpression) p2;
            return ti2.contains(p1);
        } else {
            throw new IllegalArgumentException("Second parameter of 'during' has to be an interval.");
        }
    }

    @Override
    public Field<?> visit(Overlaps node) {
        List<de.fraunhofer.iosb.ilt.sta.query.expression.Expression> params = node.getParameters();
        Field<?> p1 = params.get(0).accept(this);
        Field<?> p2 = params.get(1).accept(this);
        TimeExpression d1 = getSingleOfType(TimeExpression.class, p1);
        TimeExpression d2 = getSingleOfType(TimeExpression.class, p2);
        return d1.overlaps(d2);
    }

    @Override
    public Field<?> visit(Starts node) {
        List<de.fraunhofer.iosb.ilt.sta.query.expression.Expression> params = node.getParameters();
        Field<?> p1 = params.get(0).accept(this);
        Field<?> p2 = params.get(1).accept(this);
        TimeExpression d1 = getSingleOfType(TimeExpression.class, p1);
        TimeExpression d2 = getSingleOfType(TimeExpression.class, p2);
        return d1.starts(d2);
    }

    @Override
    public Field<?> visit(Finishes node) {
        List<de.fraunhofer.iosb.ilt.sta.query.expression.Expression> params = node.getParameters();
        Field<?> p1 = params.get(0).accept(this);
        Field<?> p2 = params.get(1).accept(this);
        TimeExpression d1 = getSingleOfType(TimeExpression.class, p1);
        TimeExpression d2 = getSingleOfType(TimeExpression.class, p2);
        return d1.finishes(d2);
    }

    @Override
    public Field<?> visit(Add node) {
        List<de.fraunhofer.iosb.ilt.sta.query.expression.Expression> params = node.getParameters();
        Field<?> p1 = params.get(0).accept(this);
        Field<?> p2 = params.get(1).accept(this);
        if (p1 instanceof TimeExpression) {
            TimeExpression ti1 = (TimeExpression) p1;
            return ti1.add(p2);
        }
        if (p2 instanceof TimeExpression) {
            TimeExpression ti2 = (TimeExpression) p2;
            return ti2.add(p1);
        }
        NumberExpression n1 = getSingleOfType(NumberExpression.class, p1);
        NumberExpression n2 = getSingleOfType(NumberExpression.class, p2);
        return n1.add(n2);
    }

    @Override
    public Field<?> visit(Divide node) {
        List<de.fraunhofer.iosb.ilt.sta.query.expression.Expression> params = node.getParameters();
        Field<?> p1 = params.get(0).accept(this);
        Field<?> p2 = params.get(1).accept(this);
        if (p1 instanceof TimeExpression) {
            TimeExpression ti1 = (TimeExpression) p1;
            return ti1.div(p2);
        }
        if (p2 instanceof TimeExpression) {
            throw new UnsupportedOperationException("Can not devide by a TimeExpression.");
        }
        NumberExpression n1 = getSingleOfType(NumberExpression.class, p1);
        NumberExpression n2 = getSingleOfType(NumberExpression.class, p2);
        return n1.divide(n2);
    }

    @Override
    public Field<?> visit(Modulo node) {
        List<de.fraunhofer.iosb.ilt.sta.query.expression.Expression> params = node.getParameters();
        Field<?> p1 = params.get(0).accept(this);
        Field<?> p2 = params.get(1).accept(this);
        NumberExpression n1 = getSingleOfType(NumberExpression.class, p1);
        NumberExpression n2 = getSingleOfType(NumberExpression.class, p2);
        return n1.castToNum(BigDecimal.class).mod(n2.castToNum(BigDecimal.class));
    }

    @Override
    public Field<?> visit(Multiply node) {
        List<de.fraunhofer.iosb.ilt.sta.query.expression.Expression> params = node.getParameters();
        Field<?> p1 = params.get(0).accept(this);
        Field<?> p2 = params.get(1).accept(this);
        if (p1 instanceof TimeExpression) {
            TimeExpression ti1 = (TimeExpression) p1;
            return ti1.mul(p2);
        }
        if (p2 instanceof TimeExpression) {
            TimeExpression ti2 = (TimeExpression) p2;
            return ti2.mul(p1);
        }
        NumberExpression n1 = getSingleOfType(NumberExpression.class, p1);
        NumberExpression n2 = getSingleOfType(NumberExpression.class, p2);
        return n1.multiply(n2);
    }

    @Override
    public Field<?> visit(Subtract node) {
        List<de.fraunhofer.iosb.ilt.sta.query.expression.Expression> params = node.getParameters();
        Field<?> p1 = params.get(0).accept(this);
        Field<?> p2 = params.get(1).accept(this);
        if (p1 instanceof TimeExpression) {
            TimeExpression ti1 = (TimeExpression) p1;
            return ti1.sub(p2);
        }
        if (p2 instanceof TimeExpression) {
            throw new UnsupportedOperationException("Can not sub a time expression from a " + p1.getClass().getName());
        }
        NumberExpression n1 = getSingleOfType(NumberExpression.class, p1);
        NumberExpression n2 = getSingleOfType(NumberExpression.class, p2);
        return n1.subtract(n2);
    }

    @Override
    public Field<?> visit(Equal node) {
        List<de.fraunhofer.iosb.ilt.sta.query.expression.Expression> params = node.getParameters();
        Field<?> p1 = params.get(0).accept(this);
        Field<?> p2 = params.get(1).accept(this);
        if (p1 instanceof TimeExpression) {
            TimeExpression ti1 = (TimeExpression) p1;
            return ti1.eq(p2);
        }
        if (p2 instanceof TimeExpression) {
            TimeExpression ti2 = (TimeExpression) p2;
            return ti2.eq(p1);
        }
        if (p1 instanceof JsonExpressionFactory.ListExpressionJson) {
            JsonExpressionFactory.ListExpressionJson l1 = (JsonExpressionFactory.ListExpressionJson) p1;
            return l1.eq(p2);
        }
        if (p2 instanceof JsonExpressionFactory.ListExpressionJson) {
            JsonExpressionFactory.ListExpressionJson l2 = (JsonExpressionFactory.ListExpressionJson) p2;
            return l2.eq(p1);
        }
        Field<?>[] pair = findPair(p1, p2);
        if (pair[0] instanceof NumberExpression) {
            return ((NumberExpression) pair[0]).eq(pair[1]);
        }
        return ((ComparableExpression) pair[0]).eq(pair[1]);
    }

    @Override
    public Field<?> visit(GreaterEqual node) {
        List<de.fraunhofer.iosb.ilt.sta.query.expression.Expression> params = node.getParameters();
        Field<?> p1 = params.get(0).accept(this);
        Field<?> p2 = params.get(1).accept(this);
        if (p1 instanceof TimeExpression) {
            TimeExpression ti1 = (TimeExpression) p1;
            return ti1.goe(p2);
        }
        if (p2 instanceof TimeExpression) {
            TimeExpression ti2 = (TimeExpression) p2;
            return ti2.loe(p1);
        }
        if (p1 instanceof JsonExpressionFactory.ListExpressionJson) {
            JsonExpressionFactory.ListExpressionJson l1 = (JsonExpressionFactory.ListExpressionJson) p1;
            return l1.goe(p2);
        }
        if (p2 instanceof JsonExpressionFactory.ListExpressionJson) {
            JsonExpressionFactory.ListExpressionJson l2 = (JsonExpressionFactory.ListExpressionJson) p2;
            return l2.loe(p1);
        }
        Field<?>[] pair = findPair(p1, p2);
        if (pair[0] instanceof NumberExpression) {
            return ((NumberExpression) pair[0]).goe(pair[1]);
        }
        return ((ComparableExpression) pair[0]).goe(pair[1]);
    }

    @Override
    public Field<?> visit(GreaterThan node) {
        List<de.fraunhofer.iosb.ilt.sta.query.expression.Expression> params = node.getParameters();
        Field<?> p1 = params.get(0).accept(this);
        Field<?> p2 = params.get(1).accept(this);
        if (p1 instanceof TimeExpression) {
            TimeExpression ti1 = (TimeExpression) p1;
            return ti1.gt(p2);
        }
        if (p2 instanceof TimeExpression) {
            TimeExpression ti2 = (TimeExpression) p2;
            return ti2.lt(p1);
        }
        if (p1 instanceof JsonExpressionFactory.ListExpressionJson) {
            JsonExpressionFactory.ListExpressionJson l1 = (JsonExpressionFactory.ListExpressionJson) p1;
            return l1.gt(p2);
        }
        if (p2 instanceof JsonExpressionFactory.ListExpressionJson) {
            JsonExpressionFactory.ListExpressionJson l2 = (JsonExpressionFactory.ListExpressionJson) p2;
            return l2.lt(p1);
        }
        Field<?>[] pair = findPair(p1, p2);
        if (pair[0] instanceof NumberExpression) {
            return ((NumberExpression) pair[0]).gt(pair[1]);
        }
        return ((ComparableExpression) pair[0]).gt(pair[1]);
    }

    @Override
    public Field<?> visit(LessEqual node) {
        List<de.fraunhofer.iosb.ilt.sta.query.expression.Expression> params = node.getParameters();
        Field<?> p1 = params.get(0).accept(this);
        Field<?> p2 = params.get(1).accept(this);
        if (p1 instanceof TimeExpression) {
            TimeExpression ti1 = (TimeExpression) p1;
            return ti1.loe(p2);
        }
        if (p2 instanceof TimeExpression) {
            TimeExpression ti2 = (TimeExpression) p2;
            return ti2.goe(p1);
        }
        if (p1 instanceof JsonExpressionFactory.ListExpressionJson) {
            JsonExpressionFactory.ListExpressionJson l1 = (JsonExpressionFactory.ListExpressionJson) p1;
            return l1.loe(p2);
        }
        if (p2 instanceof JsonExpressionFactory.ListExpressionJson) {
            JsonExpressionFactory.ListExpressionJson l2 = (JsonExpressionFactory.ListExpressionJson) p2;
            return l2.goe(p1);
        }
        Field<?>[] pair = findPair(p1, p2);
        if (pair[0] instanceof NumberExpression) {
            return ((NumberExpression) pair[0]).loe(pair[1]);
        }
        return ((ComparableExpression) pair[0]).loe(pair[1]);

    }

    @Override
    public Field<?> visit(LessThan node) {
        List<de.fraunhofer.iosb.ilt.sta.query.expression.Expression> params = node.getParameters();
        Field<?> p1 = params.get(0).accept(this);
        Field<?> p2 = params.get(1).accept(this);
        if (p1 instanceof TimeExpression) {
            TimeExpression ti1 = (TimeExpression) p1;
            return ti1.lt(p2);
        }
        if (p2 instanceof TimeExpression) {
            TimeExpression ti2 = (TimeExpression) p2;
            return ti2.gt(p1);
        }
        if (p1 instanceof JsonExpressionFactory.ListExpressionJson) {
            JsonExpressionFactory.ListExpressionJson l1 = (JsonExpressionFactory.ListExpressionJson) p1;
            return l1.lt(p2);
        }
        if (p2 instanceof JsonExpressionFactory.ListExpressionJson) {
            JsonExpressionFactory.ListExpressionJson l2 = (JsonExpressionFactory.ListExpressionJson) p2;
            return l2.gt(p1);
        }
        Field<?>[] pair = findPair(p1, p2);
        if (pair[0] instanceof NumberExpression) {
            return ((NumberExpression) pair[0]).lt(pair[1]);
        }
        return ((ComparableExpression) pair[0]).lt(pair[1]);
    }

    @Override
    public Field<?> visit(NotEqual node) {
        List<de.fraunhofer.iosb.ilt.sta.query.expression.Expression> params = node.getParameters();
        Field<?> p1 = params.get(0).accept(this);
        Field<?> p2 = params.get(1).accept(this);
        if (p1 instanceof TimeExpression) {
            TimeExpression ti1 = (TimeExpression) p1;
            return ti1.neq(p2);
        }
        if (p2 instanceof TimeExpression) {
            TimeExpression ti2 = (TimeExpression) p2;
            return ti2.neq(p1);
        }
        if (p1 instanceof JsonExpressionFactory.ListExpressionJson) {
            JsonExpressionFactory.ListExpressionJson l1 = (JsonExpressionFactory.ListExpressionJson) p1;
            return l1.ne(p2);
        }
        if (p2 instanceof JsonExpressionFactory.ListExpressionJson) {
            JsonExpressionFactory.ListExpressionJson l2 = (JsonExpressionFactory.ListExpressionJson) p2;
            return l2.ne(p1);
        }
        Field<?>[] pair = findPair(p1, p2);
        if (pair[0] instanceof NumberExpression) {
            return ((NumberExpression) pair[0]).ne(pair[1]);
        }
        return ((ComparableExpression) pair[0]).ne(pair[1]);
    }

    @Override
    public Field<?> visit(Date node) {
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression param = node.getParameters().get(0);
        Field<?> input = param.accept(this);
        StaDateTimeExpression inExp = getSingleOfType(StaDateTimeExpression.class, input);
        DateExpression date = SQLExpressions.date(inExp.getExpression());
        return date;
    }

    @Override
    public Field<?> visit(Day node) {
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression param = node.getParameters().get(0);
        Field<?> input = param.accept(this);
        StaDateTimeExpression inExp = getSingleOfType(StaDateTimeExpression.class, input);
        return inExp.getExpression().dayOfMonth();
    }

    @Override
    public Field<?> visit(FractionalSeconds node) {
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression param = node.getParameters().get(0);
        Field<?> input = param.accept(this);
        StaDateTimeExpression inExp = getSingleOfType(StaDateTimeExpression.class, input);
        return inExp.getExpression().milliSecond();
    }

    @Override
    public Field<?> visit(Hour node) {
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression param = node.getParameters().get(0);
        Field<?> input = param.accept(this);
        StaDateTimeExpression inExp = getSingleOfType(StaDateTimeExpression.class, input);
        return inExp.getExpression().hour();
    }

    @Override
    public Field<?> visit(MaxDateTime node) {
        return new StaDateTimeExpression(new Timestamp(PostgresPersistenceManager.DATETIME_MAX.getMillis()), true);
    }

    @Override
    public Field<?> visit(MinDateTime node) {
        return new StaDateTimeExpression(new Timestamp(PostgresPersistenceManager.DATETIME_MIN.getMillis()), true);
    }

    @Override
    public Field<?> visit(Minute node) {
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression param = node.getParameters().get(0);
        Field<?> input = param.accept(this);
        StaDateTimeExpression inExp = getSingleOfType(StaDateTimeExpression.class, input);
        return inExp.getExpression().minute();
    }

    @Override
    public Field<?> visit(Month node) {
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression param = node.getParameters().get(0);
        Field<?> input = param.accept(this);
        StaDateTimeExpression inExp = getSingleOfType(StaDateTimeExpression.class, input);
        return inExp.getExpression().month();
    }

    @Override
    public Field<?> visit(Now node) {
        return new StaDateTimeExpression(DateTimeExpression.currentTimestamp());
    }

    @Override
    public Field<?> visit(Second node) {
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression param = node.getParameters().get(0);
        Field<?> input = param.accept(this);
        StaDateTimeExpression inExp = getSingleOfType(StaDateTimeExpression.class, input);
        return inExp.getExpression().second();
    }

    @Override
    public Field<?> visit(Time node) {
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression param = node.getParameters().get(0);
        Field<?> input = param.accept(this);
        StaDateTimeExpression inExp = getSingleOfType(StaDateTimeExpression.class, input);
        if (!inExp.isUtc()) {
            throw new IllegalArgumentException("Constants passed to the time() function have to be in UTC.");
        }
        TimeTemplate<java.sql.Time> time = Expressions.timeTemplate(java.sql.Time.class, "pg_catalog.time({0})", inExp.getExpression());
        return time;
    }

    @Override
    public Field<?> visit(TotalOffsetMinutes node) {
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression param = node.getParameters().get(0);
        Field<?> input = param.accept(this);
        StaDateTimeExpression inExp = getSingleOfType(StaDateTimeExpression.class, input);
        NumberExpression<Integer> offset = Expressions.numberTemplate(Integer.class, "timezone({0})", inExp.getExpression()).divide(60);
        return offset;
    }

    @Override
    public Field<?> visit(Year node) {
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression param = node.getParameters().get(0);
        Field<?> input = param.accept(this);
        StaDateTimeExpression inExp = getSingleOfType(StaDateTimeExpression.class, input);
        return inExp.getExpression().year();
    }

    @Override
    public Field<?> visit(GeoDistance node) {
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression p1 = node.getParameters().get(0);
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression p2 = node.getParameters().get(1);
        Field<?> e1 = p1.accept(this);
        Field<?> e2 = p2.accept(this);
        GeometryExpression g1 = getSingleOfType(GeometryExpression.class, e1);
        GeometryExpression g2 = getSingleOfType(GeometryExpression.class, e2);
        return g1.distance(g2);
    }

    @Override
    public Field<?> visit(GeoIntersects node) {
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression p1 = node.getParameters().get(0);
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression p2 = node.getParameters().get(1);
        Field<?> e1 = p1.accept(this);
        Field<?> e2 = p2.accept(this);
        GeometryExpression g1 = getSingleOfType(GeometryExpression.class, e1);
        GeometryExpression g2 = getSingleOfType(GeometryExpression.class, e2);
        return g1.intersects(g2);
    }

    @Override
    public Field<?> visit(GeoLength node) {
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression p1 = node.getParameters().get(0);
        Field<?> e1 = p1.accept(this);
        GeometryExpression g1 = getSingleOfType(GeometryExpression.class, e1);
        return Expressions.numberTemplate(Double.class, "ST_Length({0})", g1);
    }

    @Override
    public Field<?> visit(And node) {
        List<de.fraunhofer.iosb.ilt.sta.query.expression.Expression> params = node.getParameters();
        Field<?> p1 = params.get(0).accept(this);
        Field<?> p2 = params.get(1).accept(this);
        BooleanExpression b1 = getSingleOfType(BooleanExpression.class, p1);
        BooleanExpression b2 = getSingleOfType(BooleanExpression.class, p2);
        return b1.and(b2);
    }

    @Override
    public Field<?> visit(Not node) {
        List<de.fraunhofer.iosb.ilt.sta.query.expression.Expression> params = node.getParameters();
        Field<?> p1 = params.get(0).accept(this);
        BooleanExpression b1 = getSingleOfType(BooleanExpression.class, p1);
        return b1.not();
    }

    @Override
    public Field<?> visit(Or node) {
        List<de.fraunhofer.iosb.ilt.sta.query.expression.Expression> params = node.getParameters();
        Field<?> p1 = params.get(0).accept(this);
        Field<?> p2 = params.get(1).accept(this);
        BooleanExpression b1 = getSingleOfType(BooleanExpression.class, p1);
        BooleanExpression b2 = getSingleOfType(BooleanExpression.class, p2);
        return b1.or(b2);
    }

    @Override
    public Field<?> visit(Ceiling node) {
        List<de.fraunhofer.iosb.ilt.sta.query.expression.Expression> params = node.getParameters();
        Field<?> p1 = params.get(0).accept(this);
        NumberExpression n1 = getSingleOfType(NumberExpression.class, p1);
        return n1.ceil();
    }

    @Override
    public Field<?> visit(Floor node) {
        List<de.fraunhofer.iosb.ilt.sta.query.expression.Expression> params = node.getParameters();
        Field<?> p1 = params.get(0).accept(this);
        NumberExpression n1 = getSingleOfType(NumberExpression.class, p1);
        return n1.floor();
    }

    @Override
    public Field<?> visit(Round node) {
        List<de.fraunhofer.iosb.ilt.sta.query.expression.Expression> params = node.getParameters();
        Field<?> p1 = params.get(0).accept(this);
        NumberExpression n1 = getSingleOfType(NumberExpression.class, p1);
        return n1.round();
    }

    @Override
    public Field<?> visit(STContains node) {
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression p1 = node.getParameters().get(0);
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression p2 = node.getParameters().get(1);
        Field<?> e1 = p1.accept(this);
        Field<?> e2 = p2.accept(this);
        GeometryExpression g1 = getSingleOfType(GeometryExpression.class, e1);
        GeometryExpression g2 = getSingleOfType(GeometryExpression.class, e2);
        return g1.contains(g2);
    }

    @Override
    public Field<?> visit(STCrosses node) {
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression p1 = node.getParameters().get(0);
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression p2 = node.getParameters().get(1);
        Field<?> e1 = p1.accept(this);
        Field<?> e2 = p2.accept(this);
        GeometryExpression g1 = getSingleOfType(GeometryExpression.class, e1);
        GeometryExpression g2 = getSingleOfType(GeometryExpression.class, e2);
        return g1.crosses(g2);
    }

    @Override
    public Field<?> visit(STDisjoint node) {
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression p1 = node.getParameters().get(0);
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression p2 = node.getParameters().get(1);
        Field<?> e1 = p1.accept(this);
        Field<?> e2 = p2.accept(this);
        GeometryExpression g1 = getSingleOfType(GeometryExpression.class, e1);
        GeometryExpression g2 = getSingleOfType(GeometryExpression.class, e2);
        return g1.disjoint(g2);
    }

    @Override
    public Field<?> visit(STEquals node) {
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression p1 = node.getParameters().get(0);
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression p2 = node.getParameters().get(1);
        Field<?> e1 = p1.accept(this);
        Field<?> e2 = p2.accept(this);
        GeometryExpression g1 = getSingleOfType(GeometryExpression.class, e1);
        GeometryExpression g2 = getSingleOfType(GeometryExpression.class, e2);
        return g1.eq(g2);
    }

    @Override
    public Field<?> visit(STIntersects node) {
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression p1 = node.getParameters().get(0);
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression p2 = node.getParameters().get(1);
        Field<?> e1 = p1.accept(this);
        Field<?> e2 = p2.accept(this);
        GeometryExpression g1 = getSingleOfType(GeometryExpression.class, e1);
        GeometryExpression g2 = getSingleOfType(GeometryExpression.class, e2);
        return g1.intersects(g2);
    }

    @Override
    public Field<?> visit(STOverlaps node) {
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression p1 = node.getParameters().get(0);
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression p2 = node.getParameters().get(1);
        Field<?> e1 = p1.accept(this);
        Field<?> e2 = p2.accept(this);
        GeometryExpression g1 = getSingleOfType(GeometryExpression.class, e1);
        GeometryExpression g2 = getSingleOfType(GeometryExpression.class, e2);
        return g1.overlaps(g2);
    }

    @Override
    public Field<?> visit(STRelate node) {
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression p1 = node.getParameters().get(0);
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression p2 = node.getParameters().get(1);
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression p3 = node.getParameters().get(2);
        Field<?> e1 = p1.accept(this);
        Field<?> e2 = p2.accept(this);
        GeometryExpression g1 = getSingleOfType(GeometryExpression.class, e1);
        GeometryExpression g2 = getSingleOfType(GeometryExpression.class, e2);
        if (p3 instanceof StringConstant) {
            StringConstant e3 = (StringConstant) p3;
            String s3 = e3.getValue();
            return g1.relate(g2, s3);
        }
        throw new IllegalArgumentException("ST_RELATE can only be used with a string constant as third parameter.");
    }

    @Override
    public Field<?> visit(STTouches node) {
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression p1 = node.getParameters().get(0);
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression p2 = node.getParameters().get(1);
        Field<?> e1 = p1.accept(this);
        Field<?> e2 = p2.accept(this);
        GeometryExpression g1 = getSingleOfType(GeometryExpression.class, e1);
        GeometryExpression g2 = getSingleOfType(GeometryExpression.class, e2);
        return g1.touches(g2);
    }

    @Override
    public Field<?> visit(STWithin node) {
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression p1 = node.getParameters().get(0);
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression p2 = node.getParameters().get(1);
        Field<?> e1 = p1.accept(this);
        Field<?> e2 = p2.accept(this);
        GeometryExpression g1 = getSingleOfType(GeometryExpression.class, e1);
        GeometryExpression g2 = getSingleOfType(GeometryExpression.class, e2);
        return g1.within(g2);
    }

    @Override
    public Field<?> visit(Concat node) {
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression p1 = node.getParameters().get(0);
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression p2 = node.getParameters().get(1);
        Field<?> e1 = p1.accept(this);
        Field<?> e2 = p2.accept(this);
        StringExpression s1 = getSingleOfType(StringExpression.class, e1);
        StringExpression s2 = getSingleOfType(StringExpression.class, e2);
        return s1.concat(s2);
    }

    @Override
    public Field<?> visit(EndsWith node) {
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression p1 = node.getParameters().get(0);
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression p2 = node.getParameters().get(1);
        Field<?> e1 = p1.accept(this);
        Field<?> e2 = p2.accept(this);
        StringExpression s1 = getSingleOfType(StringExpression.class, e1);
        StringExpression s2 = getSingleOfType(StringExpression.class, e2);
        return s1.endsWith(s2);
    }

    @Override
    public Field<?> visit(IndexOf node) {
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression p1 = node.getParameters().get(0);
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression p2 = node.getParameters().get(1);
        Field<?> e1 = p1.accept(this);
        Field<?> e2 = p2.accept(this);
        StringExpression s1 = getSingleOfType(StringExpression.class, e1);
        StringExpression s2 = getSingleOfType(StringExpression.class, e2);
        return s1.indexOf(s2);
    }

    @Override
    public Field<?> visit(Length node) {
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression param = node.getParameters().get(0);
        Field<?> input = param.accept(this);
        StringExpression inExp = getSingleOfType(StringExpression.class, input);
        return inExp.length();
    }

    @Override
    public Field<?> visit(StartsWith node) {
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression p1 = node.getParameters().get(0);
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression p2 = node.getParameters().get(1);
        Field<?> e1 = p1.accept(this);
        Field<?> e2 = p2.accept(this);
        StringExpression s1 = getSingleOfType(StringExpression.class, e1);
        StringExpression s2 = getSingleOfType(StringExpression.class, e2);
        return s1.startsWith(s2);
    }

    @Override
    public Field<?> visit(Substring node) {
        List<de.fraunhofer.iosb.ilt.sta.query.expression.Expression> params = node.getParameters();
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression p1 = node.getParameters().get(0);
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression p2 = node.getParameters().get(1);
        Field<?> e1 = p1.accept(this);
        Field<?> e2 = p2.accept(this);
        StringExpression s1 = getSingleOfType(StringExpression.class, e1);
        NumberExpression n2 = getSingleOfType(NumberExpression.class, e2);
        if (params.size() > 2) {
            de.fraunhofer.iosb.ilt.sta.query.expression.Expression p3 = node.getParameters().get(2);
            Field<?> e3 = p3.accept(this);
            NumberExpression n3 = getSingleOfType(NumberExpression.class, e3);
            return s1.substring(n2, n3);
        }
        return s1.substring(n2);
    }

    @Override
    public Field<?> visit(SubstringOf node) {
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression p1 = node.getParameters().get(0);
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression p2 = node.getParameters().get(1);
        Field<?> e1 = p1.accept(this);
        Field<?> e2 = p2.accept(this);
        StringExpression s1 = getSingleOfType(StringExpression.class, e1);
        StringExpression s2 = getSingleOfType(StringExpression.class, e2);
        return s2.contains(s1);
    }

    @Override
    public Field<?> visit(ToLower node) {
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression param = node.getParameters().get(0);
        Field<?> input = param.accept(this);
        StringExpression inExp = getSingleOfType(StringExpression.class, input);
        return inExp.toLowerCase();
    }

    @Override
    public Field<?> visit(ToUpper node) {
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression param = node.getParameters().get(0);
        Field<?> input = param.accept(this);
        StringExpression inExp = getSingleOfType(StringExpression.class, input);
        return inExp.toUpperCase();
    }

    @Override
    public Field<?> visit(Trim node) {
        de.fraunhofer.iosb.ilt.sta.query.expression.Expression param = node.getParameters().get(0);
        Field<?> input = param.accept(this);
        StringExpression inExp = getSingleOfType(StringExpression.class, input);
        return inExp.trim();
    }
}
