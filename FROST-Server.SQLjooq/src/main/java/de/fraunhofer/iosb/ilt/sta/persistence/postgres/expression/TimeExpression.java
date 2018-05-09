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
package de.fraunhofer.iosb.ilt.sta.persistence.postgres.expression;

import java.sql.Timestamp;
import org.jooq.Condition;
import org.jooq.Field;

/**
 *
 * @author scf
 */
public abstract class TimeExpression implements Field<Timestamp> {

    @Override
    public Condition eq(Field<Timestamp> other) {
        return simpleOpBool("=", other);
    }

    @Override
    public Condition ne(Field<Timestamp> other) {
        return eq(other).not();
    }

    @Override
    public Condition gt(Field<Timestamp> other) {
        return simpleOpBool(">", other);
    }

    @Override
    public Condition ge(Field<Timestamp> other) {
        return simpleOpBool(">=", other);
    }

    @Override
    public Condition lt(Field<Timestamp> other) {
        return simpleOpBool("<", other);
    }

    @Override
    public Condition le(Field<Timestamp> other) {
        return simpleOpBool("<=", other);
    }

    public Condition after(Field<Timestamp> other) {
        return simpleOpBool("a", other);
    }

    public Condition before(Field<Timestamp> other) {
        return simpleOpBool("b", other);
    }

    public Condition meets(Field<Timestamp> other) {
        return simpleOpBool("m", other);
    }

    @Override
    public Condition contains(Field<Timestamp> other) {
        return simpleOpBool("c", other);
    }

    public Condition overlaps(Field<Timestamp> other) {
        return simpleOpBool("o", other);
    }

    public Condition starts(Field<Timestamp> other) {
        return simpleOpBool("s", other);
    }

    public Condition finishes(Field<Timestamp> other) {
        return simpleOpBool("f", other);
    }

    @Override
    public Field<Timestamp> add(Field<?> other) {
        return simpleOp("+", other);
    }

    @Override
    public Field<Timestamp> sub(Field<?> other) {
        return simpleOp("-", other);
    }

    @Override
    public Field<Timestamp> mul(Field<? extends Number> other) {
        return simpleOp("*", other);
    }

    @Override
    public Field<Timestamp> div(Field<? extends Number> other) {
        return simpleOp("/", other);
    }

    public abstract Field<Timestamp> simpleOp(String op, Field<?> other);

    public abstract Condition simpleOpBool(String op, Field<?> other);
}
