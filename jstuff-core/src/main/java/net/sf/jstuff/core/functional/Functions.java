/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.core.functional;

import java.io.Serializable;

import net.sf.jstuff.core.validation.Args;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Functions {
    public static abstract class AbstractFunction<In, Out> implements ChainableFunction<In, Out>, Serializable {
        private static final long serialVersionUID = 1L;

        public <NextOut> ChainableFunction<In, NextOut> and(final Function<? super Out, NextOut> next) {
            return new And<In, Out, NextOut>(this, next);
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }
    }

    public static class And<In, Intermediate, Out> extends AbstractFunction<In, Out> {
        private static final long serialVersionUID = 1L;

        private final Function<In, Intermediate> first;
        private final Function<? super Intermediate, Out> second;

        public And(final Function<In, Intermediate> first, final Function<? super Intermediate, Out> second) {
            Args.notNull("first", first);
            Args.notNull("second", second);

            this.first = first;
            this.second = second;
        }

        public final Out apply(final In source) {
            return second.apply(first.apply(source));
        }
    }

    public static class CastTo<In, Out> extends AbstractFunction<In, Out> {
        private static final long serialVersionUID = 1L;

        @SuppressWarnings("unchecked")
        public Out apply(final In source) {
            return (Out) source;
        }
    }

    public static class ObjectToString<In> extends AbstractFunction<In, String> {
        private static final long serialVersionUID = 1L;

        public String apply(final In source) {
            return source == null ? null : source.toString();
        }
    }

    public static class Prefix<In> extends AbstractFunction<In, String> {
        private static final long serialVersionUID = 1L;

        public final String prefix;

        public Prefix(final String prefix) {
            Args.notNull("prefix", prefix);

            this.prefix = prefix;
        }

        public String apply(final In source) {
            return source == null ? null : prefix + source;
        }
    }

    public static class StringToInt extends AbstractFunction<String, Integer> {
        private static final long serialVersionUID = 1L;

        public Integer apply(final String source) {
            return source == null ? null : Integer.parseInt(source);
        }
    }

    public static class Suffix<In> extends AbstractFunction<In, String> {
        private static final long serialVersionUID = 1L;

        public final String suffix;

        public Suffix(final String suffix) {
            Args.notNull("suffix", suffix);

            this.suffix = suffix;
        }

        public String apply(final In source) {
            return source == null ? null : source + suffix;
        }
    }

    public static class Trim<In> extends AbstractFunction<In, String> {
        private static final long serialVersionUID = 1L;

        public String apply(final In source) {
            return source == null ? null : source.toString().trim();
        }
    }

    public static <In, Out> CastTo<In, Out> castTo(@SuppressWarnings("unused") final Class<Out> targetType) {
        return new CastTo<In, Out>();
    }

    public static <In> ObjectToString<In> objectToString() {
        return new ObjectToString<In>();
    }

    public static <In> Prefix<In> prefix(final String prefix) {
        return new Prefix<In>(prefix);
    }

    public static StringToInt stringToInt() {
        return new StringToInt();
    }

    public static <In> Suffix<In> suffix(final String suffix) {
        return new Suffix<In>(suffix);
    }

    public static <In> Trim<In> trim() {
        return new Trim<In>();
    }
}
