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

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.jooq.BetweenAndStep;
import org.jooq.Binding;
import org.jooq.Comparator;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.Converter;
import org.jooq.DataType;
import org.jooq.DatePart;
import org.jooq.Field;
import org.jooq.LikeEscapeStep;
import org.jooq.Name;
import org.jooq.OrderField;
import org.jooq.QuantifiedSelect;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.Select;
import org.jooq.SortField;
import org.jooq.SortOrder;
import org.jooq.WindowIgnoreNullsStep;
import org.jooq.WindowPartitionByStep;

/**
 * Some paths, like Observation.result and the time-interval paths, return two
 * column references. This class is just to encapsulate these cases. If this
 * Expression is used as a normal Expression, the first of the two will be used.
 */
public class ListExpression implements Field<Object> {

    private final Map<String, Field<Object>> expressions;
    private final Map<String, OrderField<Object>> expressionsForOrder;

    public ListExpression(Map<String, Field<Object>> expressions) {
        this.expressions = expressions;
        this.expressionsForOrder = new HashMap<>(expressions);
    }

    public ListExpression(Map<String, Field<Object>> expressions, Map<String, OrderField<Object>> expressionsForOrder) {
        this.expressions = expressions;
        this.expressionsForOrder = expressionsForOrder;
    }

    public Map<String, Field<Object>> getExpressions() {
        return expressions;
    }

    public Map<String, OrderField<Object>> getExpressionsForOrder() {
        return expressionsForOrder;
    }

    public Field<?> getExpression(String name) {
        return expressions.get(name);
    }

    public Field<Object> getDefault() {
        return expressions.values().iterator().next();
    }

    @Override
    public Class getType() {
        return expressions.values().iterator().next().getType();
    }

    @Override
    public String getName() {
        return getDefault().getName();
    }

    @Override
    public Name getQualifiedName() {
        return getDefault().getQualifiedName();
    }

    @Override
    public Name getUnqualifiedName() {
        return getDefault().getUnqualifiedName();
    }

    @Override
    public String getComment() {
        return getDefault().getComment();
    }

    @Override
    public Converter<?, Object> getConverter() {
        return getDefault().getConverter();
    }

    @Override
    public Binding<?, Object> getBinding() {
        return getDefault().getBinding();
    }

    @Override
    public DataType<Object> getDataType() {
        return getDefault().getDataType();
    }

    @Override
    public DataType<Object> getDataType(Configuration configuration) {
        return getDefault().getDataType(configuration);
    }

    @Override
    public Field<Object> as(String alias) {
        return getDefault().as(alias);
    }

    @Override
    public Field<Object> as(Name alias) {
        return getDefault().as(alias);
    }

    @Override
    public Field<Object> as(Field<?> otherField) {
        return getDefault().as(otherField);
    }

    @Override
    public Field<Object> as(Function<? super Field<Object>, ? extends String> aliasFunction) {
        return getDefault().as(aliasFunction);
    }

    @Override
    public <Z> Field<Z> cast(Field<Z> field) {
        return getDefault().cast(field);
    }

    @Override
    public <Z> Field<Z> cast(DataType<Z> type) {
        return getDefault().cast(type);
    }

    @Override
    public <Z> Field<Z> cast(Class<Z> type) {
        return getDefault().cast(type);
    }

    @Override
    public <Z> Field<Z> coerce(Field<Z> field) {
        return getDefault().coerce(field);
    }

    @Override
    public <Z> Field<Z> coerce(DataType<Z> type) {
        return getDefault().coerce(type);
    }

    @Override
    public <Z> Field<Z> coerce(Class<Z> type) {
        return getDefault().coerce(type);
    }

    @Override
    public SortField<Object> asc() {
        return getDefault().asc();
    }

    @Override
    public SortField<Object> desc() {
        return getDefault().desc();
    }

    @Override
    public SortField<Object> sortDefault() {
        return getDefault().sortDefault();
    }

    @Override
    public SortField<Object> sort(SortOrder order) {
        return getDefault().sort(order);
    }

    @Override
    public SortField<Integer> sortAsc(Collection<Object> sortList) {
        return getDefault().sortAsc(sortList);
    }

    @Override
    public SortField<Integer> sortAsc(Object... sortList) {
        return getDefault().sortAsc(sortList);
    }

    @Override
    public SortField<Integer> sortDesc(Collection<Object> sortList) {
        return getDefault().sortDesc(sortList);
    }

    @Override
    public SortField<Integer> sortDesc(Object... sortList) {
        return getDefault().sortDesc(sortList);
    }

    @Override
    public <Z> SortField<Z> sort(Map<Object, Z> sortMap) {
        return getDefault().sort(sortMap);
    }

    @Override
    public Field<Object> neg() {
        return getDefault().neg();
    }

    @Override
    public Field<Object> unaryMinus() {
        return getDefault().unaryMinus();
    }

    @Override
    public Field<Object> unaryPlus() {
        return getDefault().unaryPlus();
    }

    @Override
    public Field<Object> add(Number value) {
        return getDefault().add(value);
    }

    @Override
    public Field<Object> add(Field<?> value) {
        return getDefault().add(value);
    }

    @Override
    public Field<Object> plus(Number value) {
        return getDefault().plus(value);
    }

    @Override
    public Field<Object> plus(Field<?> value) {
        return getDefault().plus(value);
    }

    @Override
    public Field<Object> sub(Number value) {
        return getDefault().sub(value);
    }

    @Override
    public Field<Object> sub(Field<?> value) {
        return getDefault().sub(value);
    }

    @Override
    public Field<Object> subtract(Number value) {
        return getDefault().subtract(value);
    }

    @Override
    public Field<Object> subtract(Field<?> value) {
        return getDefault().subtract(value);
    }

    @Override
    public Field<Object> minus(Number value) {
        return getDefault().minus(value);
    }

    @Override
    public Field<Object> minus(Field<?> value) {
        return getDefault().minus(value);
    }

    @Override
    public Field<Object> mul(Number value) {
        return getDefault().mul(value);
    }

    @Override
    public Field<Object> mul(Field<? extends Number> value) {
        return getDefault().mul(value);
    }

    @Override
    public Field<Object> multiply(Number value) {
        return getDefault().multiply(value);
    }

    @Override
    public Field<Object> multiply(Field<? extends Number> value) {
        return getDefault().multiply(value);
    }

    @Override
    public Field<Object> times(Number value) {
        return getDefault().times(value);
    }

    @Override
    public Field<Object> times(Field<? extends Number> value) {
        return getDefault().times(value);
    }

    @Override
    public Field<Object> div(Number value) {
        return getDefault().div(value);
    }

    @Override
    public Field<Object> div(Field<? extends Number> value) {
        return getDefault().div(value);
    }

    @Override
    public Field<Object> divide(Number value) {
        return getDefault().divide(value);
    }

    @Override
    public Field<Object> divide(Field<? extends Number> value) {
        return getDefault().divide(value);
    }

    @Override
    public Field<Object> mod(Number value) {
        return getDefault().mod(value);
    }

    @Override
    public Field<Object> mod(Field<? extends Number> value) {
        return getDefault().mod(value);
    }

    @Override
    public Field<Object> modulo(Number value) {
        return getDefault().modulo(value);
    }

    @Override
    public Field<Object> modulo(Field<? extends Number> value) {
        return getDefault().modulo(value);
    }

    @Override
    public Field<Object> rem(Number value) {
        return getDefault().rem(value);
    }

    @Override
    public Field<Object> rem(Field<? extends Number> value) {
        return getDefault().rem(value);
    }

    @Override
    public Field<BigDecimal> pow(Number exponent) {
        return getDefault().pow(exponent);
    }

    @Override
    public Field<BigDecimal> pow(Field<? extends Number> exponent) {
        return getDefault().pow(exponent);
    }

    @Override
    public Field<BigDecimal> power(Number exponent) {
        return getDefault().power(exponent);
    }

    @Override
    public Field<BigDecimal> power(Field<? extends Number> exponent) {
        return getDefault().power(exponent);
    }

    @Override
    public Field<Object> bitNot() {
        return getDefault().bitNot();
    }

    @Override
    public Field<Object> bitAnd(Object value) {
        return getDefault().bitAnd(value);
    }

    @Override
    public Field<Object> bitAnd(Field<Object> value) {
        return getDefault().bitAnd(value);
    }

    @Override
    public Field<Object> bitNand(Object value) {
        return getDefault().bitNand(value);
    }

    @Override
    public Field<Object> bitNand(Field<Object> value) {
        return getDefault().bitNand(value);
    }

    @Override
    public Field<Object> bitOr(Object value) {
        return getDefault().bitOr(value);
    }

    @Override
    public Field<Object> bitOr(Field<Object> value) {
        return getDefault().bitOr(value);
    }

    @Override
    public Field<Object> bitNor(Object value) {
        return getDefault().bitNor(value);
    }

    @Override
    public Field<Object> bitNor(Field<Object> value) {
        return getDefault().bitNor(value);
    }

    @Override
    public Field<Object> bitXor(Object value) {
        return getDefault().bitXor(value);
    }

    @Override
    public Field<Object> bitXor(Field<Object> value) {
        return getDefault().bitXor(value);
    }

    @Override
    public Field<Object> bitXNor(Object value) {
        return getDefault().bitXNor(value);
    }

    @Override
    public Field<Object> bitXNor(Field<Object> value) {
        return getDefault().bitXNor(value);
    }

    @Override
    public Field<Object> shl(Number value) {
        return getDefault().shl(value);
    }

    @Override
    public Field<Object> shl(Field<? extends Number> value) {
        return getDefault().shl(value);
    }

    @Override
    public Field<Object> shr(Number value) {
        return getDefault().shr(value);
    }

    @Override
    public Field<Object> shr(Field<? extends Number> value) {
        return getDefault().shr(value);
    }

    @Override
    public Condition isNull() {
        return getDefault().isNull();
    }

    @Override
    public Condition isNotNull() {
        return getDefault().isNotNull();
    }

    @Override
    public Condition isDistinctFrom(Object value) {
        return getDefault().isDistinctFrom(value);
    }

    @Override
    public Condition isDistinctFrom(Field<Object> field) {
        return getDefault().isDistinctFrom(field);
    }

    @Override
    public Condition isNotDistinctFrom(Object value) {
        return getDefault().isNotDistinctFrom(value);
    }

    @Override
    public Condition isNotDistinctFrom(Field<Object> field) {
        return getDefault().isNotDistinctFrom(field);
    }

    @Override
    public Condition likeRegex(String pattern) {
        return getDefault().likeRegex(pattern);
    }

    @Override
    public Condition likeRegex(Field<String> pattern) {
        return getDefault().likeRegex(pattern);
    }

    @Override
    public Condition notLikeRegex(String pattern) {
        return getDefault().notLikeRegex(pattern);
    }

    @Override
    public Condition notLikeRegex(Field<String> pattern) {
        return getDefault().notLikeRegex(pattern);
    }

    @Override
    public LikeEscapeStep like(Field<String> value) {
        return getDefault().like(value);
    }

    @Override
    public Condition like(Field<String> value, char escape) {
        return getDefault().like(value, escape);
    }

    @Override
    public LikeEscapeStep like(String value) {
        return getDefault().like(value);
    }

    @Override
    public Condition like(String value, char escape) {
        return getDefault().like(value, escape);
    }

    @Override
    public LikeEscapeStep likeIgnoreCase(Field<String> field) {
        return getDefault().likeIgnoreCase(field);
    }

    @Override
    public Condition likeIgnoreCase(Field<String> field, char escape) {
        return getDefault().likeIgnoreCase(field, escape);
    }

    @Override
    public LikeEscapeStep likeIgnoreCase(String value) {
        return getDefault().likeIgnoreCase(value);
    }

    @Override
    public Condition likeIgnoreCase(String value, char escape) {
        return getDefault().likeIgnoreCase(value, escape);
    }

    @Override
    public LikeEscapeStep notLike(Field<String> field) {
        return getDefault().notLike(field);
    }

    @Override
    public Condition notLike(Field<String> field, char escape) {
        return getDefault().notLike(field, escape);
    }

    @Override
    public LikeEscapeStep notLike(String value) {
        return getDefault().notLike(value);
    }

    @Override
    public Condition notLike(String value, char escape) {
        return getDefault().notLike(value, escape);
    }

    @Override
    public LikeEscapeStep notLikeIgnoreCase(Field<String> field) {
        return getDefault().notLikeIgnoreCase(field);
    }

    @Override
    public Condition notLikeIgnoreCase(Field<String> field, char escape) {
        return getDefault().notLikeIgnoreCase(field, escape);
    }

    @Override
    public LikeEscapeStep notLikeIgnoreCase(String value) {
        return getDefault().notLikeIgnoreCase(value);
    }

    @Override
    public Condition notLikeIgnoreCase(String value, char escape) {
        return getDefault().notLikeIgnoreCase(value, escape);
    }

    @Override
    public Condition contains(Object value) {
        return getDefault().contains(value);
    }

    @Override
    public Condition contains(Field<Object> value) {
        return getDefault().contains(value);
    }

    @Override
    public Condition notContains(Object value) {
        return getDefault().notContains(value);
    }

    @Override
    public Condition notContains(Field<Object> value) {
        return getDefault().notContains(value);
    }

    @Override
    public Condition containsIgnoreCase(Object value) {
        return getDefault().containsIgnoreCase(value);
    }

    @Override
    public Condition containsIgnoreCase(Field<Object> value) {
        return getDefault().containsIgnoreCase(value);
    }

    @Override
    public Condition notContainsIgnoreCase(Object value) {
        return getDefault().notContainsIgnoreCase(value);
    }

    @Override
    public Condition notContainsIgnoreCase(Field<Object> value) {
        return getDefault().notContainsIgnoreCase(value);
    }

    @Override
    public Condition startsWith(Object value) {
        return getDefault().startsWith(value);
    }

    @Override
    public Condition startsWith(Field<Object> value) {
        return getDefault().startsWith(value);
    }

    @Override
    public Condition endsWith(Object value) {
        return getDefault().endsWith(value);
    }

    @Override
    public Condition endsWith(Field<Object> value) {
        return getDefault().endsWith(value);
    }

    @Override
    public Condition in(Collection<?> values) {
        return getDefault().in(values);
    }

    @Override
    public Condition in(Result<? extends Record1<Object>> result) {
        return getDefault().in(result);
    }

    @Override
    public Condition in(Object... values) {
        return getDefault().in(values);
    }

    @Override
    public Condition in(Field<?>... values) {
        return getDefault().in(values);
    }

    @Override
    public Condition in(Select<? extends Record1<Object>> query) {
        return getDefault().in(query);
    }

    @Override
    public Condition notIn(Collection<?> values) {
        return getDefault().notIn(values);
    }

    @Override
    public Condition notIn(Result<? extends Record1<Object>> result) {
        return getDefault().notIn(result);
    }

    @Override
    public Condition notIn(Object... values) {
        return getDefault().notIn(values);
    }

    @Override
    public Condition notIn(Field<?>... values) {
        return getDefault().notIn(values);
    }

    @Override
    public Condition notIn(Select<? extends Record1<Object>> query) {
        return getDefault().notIn(query);
    }

    @Override
    public Condition between(Object minValue, Object maxValue) {
        return getDefault().between(minValue, maxValue);
    }

    @Override
    public Condition between(Field<Object> minValue, Field<Object> maxValue) {
        return getDefault().between(minValue, maxValue);
    }

    @Override
    public Condition betweenSymmetric(Object minValue, Object maxValue) {
        return getDefault().betweenSymmetric(minValue, maxValue);
    }

    @Override
    public Condition betweenSymmetric(Field<Object> minValue, Field<Object> maxValue) {
        return getDefault().betweenSymmetric(minValue, maxValue);
    }

    @Override
    public Condition notBetween(Object minValue, Object maxValue) {
        return getDefault().notBetween(minValue, maxValue);
    }

    @Override
    public Condition notBetween(Field<Object> minValue, Field<Object> maxValue) {
        return getDefault().notBetween(minValue, maxValue);
    }

    @Override
    public Condition notBetweenSymmetric(Object minValue, Object maxValue) {
        return getDefault().notBetweenSymmetric(minValue, maxValue);
    }

    @Override
    public Condition notBetweenSymmetric(Field<Object> minValue, Field<Object> maxValue) {
        return getDefault().notBetweenSymmetric(minValue, maxValue);
    }

    @Override
    public BetweenAndStep<Object> between(Object minValue) {
        return getDefault().between(minValue);
    }

    @Override
    public BetweenAndStep<Object> between(Field<Object> minValue) {
        return getDefault().between(minValue);
    }

    @Override
    public BetweenAndStep<Object> betweenSymmetric(Object minValue) {
        return getDefault().betweenSymmetric(minValue);
    }

    @Override
    public BetweenAndStep<Object> betweenSymmetric(Field<Object> minValue) {
        return getDefault().betweenSymmetric(minValue);
    }

    @Override
    public BetweenAndStep<Object> notBetween(Object minValue) {
        return getDefault().notBetween(minValue);
    }

    @Override
    public BetweenAndStep<Object> notBetween(Field<Object> minValue) {
        return getDefault().notBetween(minValue);
    }

    @Override
    public BetweenAndStep<Object> notBetweenSymmetric(Object minValue) {
        return getDefault().notBetweenSymmetric(minValue);
    }

    @Override
    public BetweenAndStep<Object> notBetweenSymmetric(Field<Object> minValue) {
        return getDefault().notBetweenSymmetric(minValue);
    }

    @Override
    public Condition compare(Comparator comparator, Object value) {
        return getDefault().compare(comparator, value);
    }

    @Override
    public Condition compare(Comparator comparator, Field<Object> field) {
        return getDefault().compare(comparator, field);
    }

    @Override
    public Condition compare(Comparator comparator, Select<? extends Record1<Object>> query) {
        return getDefault().compare(comparator, query);
    }

    @Override
    public Condition compare(Comparator comparator, QuantifiedSelect<? extends Record1<Object>> query) {
        return getDefault().compare(comparator, query);
    }

    @Override
    public Condition equal(Object value) {
        return getDefault().equal(value);
    }

    @Override
    public Condition equal(Field<Object> field) {
        return getDefault().equal(field);
    }

    @Override
    public Condition equal(Select<? extends Record1<Object>> query) {
        return getDefault().equal(query);
    }

    @Override
    public Condition equal(QuantifiedSelect<? extends Record1<Object>> query) {
        return getDefault().equal(query);
    }

    @Override
    public Condition eq(Object value) {
        return getDefault().eq(value);
    }

    @Override
    public Condition eq(Field<Object> field) {
        return getDefault().eq(field);
    }

    @Override
    public Condition eq(Select<? extends Record1<Object>> query) {
        return getDefault().eq(query);
    }

    @Override
    public Condition eq(QuantifiedSelect<? extends Record1<Object>> query) {
        return getDefault().eq(query);
    }

    @Override
    public Condition notEqual(Object value) {
        return getDefault().notEqual(value);
    }

    @Override
    public Condition notEqual(Field<Object> field) {
        return getDefault().notEqual(field);
    }

    @Override
    public Condition notEqual(Select<? extends Record1<Object>> query) {
        return getDefault().notEqual(query);
    }

    @Override
    public Condition notEqual(QuantifiedSelect<? extends Record1<Object>> query) {
        return getDefault().notEqual(query);
    }

    @Override
    public Condition ne(Object value) {
        return getDefault().ne(value);
    }

    @Override
    public Condition ne(Field<Object> field) {
        return getDefault().ne(field);
    }

    @Override
    public Condition ne(Select<? extends Record1<Object>> query) {
        return getDefault().ne(query);
    }

    @Override
    public Condition ne(QuantifiedSelect<? extends Record1<Object>> query) {
        return getDefault().ne(query);
    }

    @Override
    public Condition lessThan(Object value) {
        return getDefault().lessThan(value);
    }

    @Override
    public Condition lessThan(Field<Object> field) {
        return getDefault().lessThan(field);
    }

    @Override
    public Condition lessThan(Select<? extends Record1<Object>> query) {
        return getDefault().lessThan(query);
    }

    @Override
    public Condition lessThan(QuantifiedSelect<? extends Record1<Object>> query) {
        return getDefault().lessThan(query);
    }

    @Override
    public Condition lt(Object value) {
        return getDefault().lt(value);
    }

    @Override
    public Condition lt(Field<Object> field) {
        return getDefault().lt(field);
    }

    @Override
    public Condition lt(Select<? extends Record1<Object>> query) {
        return getDefault().lt(query);
    }

    @Override
    public Condition lt(QuantifiedSelect<? extends Record1<Object>> query) {
        return getDefault().lt(query);
    }

    @Override
    public Condition lessOrEqual(Object value) {
        return getDefault().lessOrEqual(value);
    }

    @Override
    public Condition lessOrEqual(Field<Object> field) {
        return getDefault().lessOrEqual(field);
    }

    @Override
    public Condition lessOrEqual(Select<? extends Record1<Object>> query) {
        return getDefault().lessOrEqual(query);
    }

    @Override
    public Condition lessOrEqual(QuantifiedSelect<? extends Record1<Object>> query) {
        return getDefault().lessOrEqual(query);
    }

    @Override
    public Condition le(Object value) {
        return getDefault().le(value);
    }

    @Override
    public Condition le(Field<Object> field) {
        return getDefault().le(field);
    }

    @Override
    public Condition le(Select<? extends Record1<Object>> query) {
        return getDefault().le(query);
    }

    @Override
    public Condition le(QuantifiedSelect<? extends Record1<Object>> query) {
        return getDefault().le(query);
    }

    @Override
    public Condition greaterThan(Object value) {
        return getDefault().greaterThan(value);
    }

    @Override
    public Condition greaterThan(Field<Object> field) {
        return getDefault().greaterThan(field);
    }

    @Override
    public Condition greaterThan(Select<? extends Record1<Object>> query) {
        return getDefault().greaterThan(query);
    }

    @Override
    public Condition greaterThan(QuantifiedSelect<? extends Record1<Object>> query) {
        return getDefault().greaterThan(query);
    }

    @Override
    public Condition gt(Object value) {
        return getDefault().gt(value);
    }

    @Override
    public Condition gt(Field<Object> field) {
        return getDefault().gt(field);
    }

    @Override
    public Condition gt(Select<? extends Record1<Object>> query) {
        return getDefault().gt(query);
    }

    @Override
    public Condition gt(QuantifiedSelect<? extends Record1<Object>> query) {
        return getDefault().gt(query);
    }

    @Override
    public Condition greaterOrEqual(Object value) {
        return getDefault().greaterOrEqual(value);
    }

    @Override
    public Condition greaterOrEqual(Field<Object> field) {
        return getDefault().greaterOrEqual(field);
    }

    @Override
    public Condition greaterOrEqual(Select<? extends Record1<Object>> query) {
        return getDefault().greaterOrEqual(query);
    }

    @Override
    public Condition greaterOrEqual(QuantifiedSelect<? extends Record1<Object>> query) {
        return getDefault().greaterOrEqual(query);
    }

    @Override
    public Condition ge(Object value) {
        return getDefault().ge(value);
    }

    @Override
    public Condition ge(Field<Object> field) {
        return getDefault().ge(field);
    }

    @Override
    public Condition ge(Select<? extends Record1<Object>> query) {
        return getDefault().ge(query);
    }

    @Override
    public Condition ge(QuantifiedSelect<? extends Record1<Object>> query) {
        return getDefault().ge(query);
    }

    @Override
    public Condition isTrue() {
        return getDefault().isTrue();
    }

    @Override
    public Condition isFalse() {
        return getDefault().isFalse();
    }

    @Override
    public Condition equalIgnoreCase(String value) {
        return getDefault().equalIgnoreCase(value);
    }

    @Override
    public Condition equalIgnoreCase(Field<String> value) {
        return getDefault().equalIgnoreCase(value);
    }

    @Override
    public Condition notEqualIgnoreCase(String value) {
        return getDefault().notEqualIgnoreCase(value);
    }

    @Override
    public Condition notEqualIgnoreCase(Field<String> value) {
        return getDefault().notEqualIgnoreCase(value);
    }

    @Override
    public Field<Integer> sign() {
        return getDefault().sign();
    }

    @Override
    public Field<Object> abs() {
        return getDefault().abs();
    }

    @Override
    public Field<Object> round() {
        return getDefault().round();
    }

    @Override
    public Field<Object> round(int decimals) {
        return getDefault().round(decimals);
    }

    @Override
    public Field<Object> floor() {
        return getDefault().floor();
    }

    @Override
    public Field<Object> ceil() {
        return getDefault().ceil();
    }

    @Override
    public Field<BigDecimal> sqrt() {
        return getDefault().sqrt();
    }

    @Override
    public Field<BigDecimal> exp() {
        return getDefault().exp();
    }

    @Override
    public Field<BigDecimal> ln() {
        return getDefault().ln();
    }

    @Override
    public Field<BigDecimal> log(int base) {
        return getDefault().log(base);
    }

    @Override
    public Field<BigDecimal> acos() {
        return getDefault().acos();
    }

    @Override
    public Field<BigDecimal> asin() {
        return getDefault().asin();
    }

    @Override
    public Field<BigDecimal> atan() {
        return getDefault().atan();
    }

    @Override
    public Field<BigDecimal> atan2(Number y) {
        return getDefault().atan2(y);
    }

    @Override
    public Field<BigDecimal> atan2(Field<? extends Number> y) {
        return getDefault().atan2(y);
    }

    @Override
    public Field<BigDecimal> cos() {
        return getDefault().cos();
    }

    @Override
    public Field<BigDecimal> sin() {
        return getDefault().sin();
    }

    @Override
    public Field<BigDecimal> tan() {
        return getDefault().tan();
    }

    @Override
    public Field<BigDecimal> cot() {
        return getDefault().cot();
    }

    @Override
    public Field<BigDecimal> sinh() {
        return getDefault().sinh();
    }

    @Override
    public Field<BigDecimal> cosh() {
        return getDefault().cosh();
    }

    @Override
    public Field<BigDecimal> tanh() {
        return getDefault().tanh();
    }

    @Override
    public Field<BigDecimal> coth() {
        return getDefault().coth();
    }

    @Override
    public Field<BigDecimal> deg() {
        return getDefault().deg();
    }

    @Override
    public Field<BigDecimal> rad() {
        return getDefault().rad();
    }

    @Override
    public Field<Integer> count() {
        return getDefault().count();
    }

    @Override
    public Field<Integer> countDistinct() {
        return getDefault().countDistinct();
    }

    @Override
    public Field<Object> max() {
        return getDefault().max();
    }

    @Override
    public Field<Object> min() {
        return getDefault().min();
    }

    @Override
    public Field<BigDecimal> sum() {
        return getDefault().sum();
    }

    @Override
    public Field<BigDecimal> avg() {
        return getDefault().avg();
    }

    @Override
    public Field<BigDecimal> median() {
        return getDefault().median();
    }

    @Override
    public Field<BigDecimal> stddevPop() {
        return getDefault().stddevPop();
    }

    @Override
    public Field<BigDecimal> stddevSamp() {
        return getDefault().stddevSamp();
    }

    @Override
    public Field<BigDecimal> varPop() {
        return getDefault().varPop();
    }

    @Override
    public Field<BigDecimal> varSamp() {
        return getDefault().varSamp();
    }

    @Override
    public WindowPartitionByStep<Integer> countOver() {
        return getDefault().countOver();
    }

    @Override
    public WindowPartitionByStep<Object> maxOver() {
        return getDefault().maxOver();
    }

    @Override
    public WindowPartitionByStep<Object> minOver() {
        return getDefault().minOver();
    }

    @Override
    public WindowPartitionByStep<BigDecimal> sumOver() {
        return getDefault().sumOver();
    }

    @Override
    public WindowPartitionByStep<BigDecimal> avgOver() {
        return getDefault().avgOver();
    }

    @Override
    public WindowIgnoreNullsStep<Object> firstValue() {
        return getDefault().firstValue();
    }

    @Override
    public WindowIgnoreNullsStep<Object> lastValue() {
        return getDefault().lastValue();
    }

    @Override
    public WindowIgnoreNullsStep<Object> lead() {
        return getDefault().lead();
    }

    @Override
    public WindowIgnoreNullsStep<Object> lead(int offset) {
        return getDefault().lead(offset);
    }

    @Override
    public WindowIgnoreNullsStep<Object> lead(int offset, Object defaultValue) {
        return getDefault().lead(offset, defaultValue);
    }

    @Override
    public WindowIgnoreNullsStep<Object> lead(int offset, Field<Object> defaultValue) {
        return getDefault().lead(offset, defaultValue);
    }

    @Override
    public WindowIgnoreNullsStep<Object> lag() {
        return getDefault().lag();
    }

    @Override
    public WindowIgnoreNullsStep<Object> lag(int offset) {
        return getDefault().lag(offset);
    }

    @Override
    public WindowIgnoreNullsStep<Object> lag(int offset, Object defaultValue) {
        return getDefault().lag(offset, defaultValue);
    }

    @Override
    public WindowIgnoreNullsStep<Object> lag(int offset, Field<Object> defaultValue) {
        return getDefault().lag(offset, defaultValue);
    }

    @Override
    public WindowPartitionByStep<BigDecimal> stddevPopOver() {
        return getDefault().stddevPopOver();
    }

    @Override
    public WindowPartitionByStep<BigDecimal> stddevSampOver() {
        return getDefault().stddevSampOver();
    }

    @Override
    public WindowPartitionByStep<BigDecimal> varPopOver() {
        return getDefault().varPopOver();
    }

    @Override
    public WindowPartitionByStep<BigDecimal> varSampOver() {
        return getDefault().varSampOver();
    }

    @Override
    public Field<String> upper() {
        return getDefault().upper();
    }

    @Override
    public Field<String> lower() {
        return getDefault().lower();
    }

    @Override
    public Field<String> trim() {
        return getDefault().trim();
    }

    @Override
    public Field<String> rtrim() {
        return getDefault().rtrim();
    }

    @Override
    public Field<String> ltrim() {
        return getDefault().ltrim();
    }

    @Override
    public Field<String> rpad(Field<? extends Number> length) {
        return getDefault().rpad(length);
    }

    @Override
    public Field<String> rpad(int length) {
        return getDefault().rpad(length);
    }

    @Override
    public Field<String> rpad(Field<? extends Number> length, Field<String> character) {
        return getDefault().rpad(length, character);
    }

    @Override
    public Field<String> rpad(int length, char character) {
        return getDefault().rpad(character);
    }

    @Override
    public Field<String> lpad(Field<? extends Number> length) {
        return getDefault().lpad(length);
    }

    @Override
    public Field<String> lpad(int length) {
        return getDefault().lpad(length);
    }

    @Override
    public Field<String> lpad(Field<? extends Number> length, Field<String> character) {
        return getDefault().lpad(length, character);
    }

    @Override
    public Field<String> lpad(int length, char character) {
        return getDefault().lpad(character);
    }

    @Override
    public Field<String> repeat(Number count) {
        return getDefault().repeat(count);
    }

    @Override
    public Field<String> repeat(Field<? extends Number> count) {
        return getDefault().repeat(count);
    }

    @Override
    public Field<String> replace(Field<String> search) {
        return getDefault().replace(search);
    }

    @Override
    public Field<String> replace(String search) {
        return getDefault().replace(search);
    }

    @Override
    public Field<String> replace(Field<String> search, Field<String> replace) {
        return getDefault().replace(replace);
    }

    @Override
    public Field<String> replace(String search, String replace) {
        return getDefault().replace(replace);
    }

    @Override
    public Field<Integer> position(String search) {
        return getDefault().position(search);
    }

    @Override
    public Field<Integer> position(Field<String> search) {
        return getDefault().position(search);
    }

    @Override
    public Field<Integer> ascii() {
        return getDefault().ascii();
    }

    @Override
    public Field<String> concat(Field<?>... fields) {
        return getDefault().concat(fields);
    }

    @Override
    public Field<String> concat(String... values) {
        return getDefault().concat(values);
    }

    @Override
    public Field<String> substring(int startingPosition) {
        return getDefault().substring(startingPosition);
    }

    @Override
    public Field<String> substring(Field<? extends Number> startingPosition) {
        return getDefault().substring(startingPosition);
    }

    @Override
    public Field<String> substring(int startingPosition, int length) {
        return getDefault().substring(length);
    }

    @Override
    public Field<String> substring(Field<? extends Number> startingPosition, Field<? extends Number> length) {
        return getDefault().substring(length);
    }

    @Override
    public Field<Integer> length() {
        return getDefault().length();
    }

    @Override
    public Field<Integer> charLength() {
        return getDefault().charLength();
    }

    @Override
    public Field<Integer> bitLength() {
        return getDefault().bitLength();
    }

    @Override
    public Field<Integer> octetLength() {
        return getDefault().octetLength();
    }

    @Override
    public Field<Integer> extract(DatePart datePart) {
        return getDefault().extract(datePart);
    }

    @Override
    public Field<Object> greatest(Object... others) {
        return getDefault().greatest(others);
    }

    @Override
    public Field<Object> greatest(Field<?>... others) {
        return getDefault().greatest(others);
    }

    @Override
    public Field<Object> least(Object... others) {
        return getDefault().least(others);
    }

    @Override
    public Field<Object> least(Field<?>... others) {
        return getDefault().least(others);
    }

    @Override
    public Field<Object> nvl(Object defaultValue) {
        return getDefault().nvl(defaultValue);
    }

    @Override
    public Field<Object> nvl(Field<Object> defaultValue) {
        return getDefault().nvl(defaultValue);
    }

    @Override
    public <Z> Field<Z> nvl2(Z valueIfNotNull, Z valueIfNull) {
        return getDefault().nvl2(valueIfNotNull, valueIfNull);
    }

    @Override
    public <Z> Field<Z> nvl2(Field<Z> valueIfNotNull, Field<Z> valueIfNull) {
        return getDefault().nvl2(valueIfNotNull, valueIfNull);
    }

    @Override
    public Field<Object> nullif(Object other) {
        return getDefault().nullif(other);
    }

    @Override
    public Field<Object> nullif(Field<Object> other) {
        return getDefault().nullif(other);
    }

    @Override
    public <Z> Field<Z> decode(Object search, Z result) {
        return getDefault().decode(search, result);
    }

    @Override
    public <Z> Field<Z> decode(Object search, Z result, Object... more) {
        return getDefault().decode(search, result, more);
    }

    @Override
    public <Z> Field<Z> decode(Field<Object> search, Field<Z> result) {
        return getDefault().decode(search, result);
    }

    @Override
    public <Z> Field<Z> decode(Field<Object> search, Field<Z> result, Field<?>... more) {
        return getDefault().decode(search, result, more);
    }

    @Override
    public Field<Object> coalesce(Object option, Object... options) {
        return getDefault().coalesce(options);
    }

    @Override
    public Field<Object> coalesce(Field<Object> option, Field<?>... options) {
        return getDefault().coalesce(options);
    }

    @Override
    public Field<Object> field(Record record) {
        return getDefault().field(record);
    }

    @Override
    public Object get(Record record) {
        return getDefault().get(record);
    }

    @Override
    public Object getValue(Record record) {
        return getDefault().getValue(record);
    }

    @Override
    public Object original(Record record) {
        return getDefault().original(record);
    }

    @Override
    public boolean changed(Record record) {
        return getDefault().changed(record);
    }

    @Override
    public void reset(Record record) {
        getDefault().reset(record);
    }

    @Override
    public Record1<Object> from(Record record) {
        return getDefault().from(record);
    }

}
