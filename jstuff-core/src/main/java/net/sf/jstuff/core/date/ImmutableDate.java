/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2010-2018 Sebastian
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
package net.sf.jstuff.core.date;

import java.sql.Date;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ImmutableDate extends Date {
    private static final long serialVersionUID = 1L;

    public static ImmutableDate now() {
        return new ImmutableDate();
    }

    public static ImmutableDate of(final java.util.Date date) {
        return new ImmutableDate(date);
    }

    public ImmutableDate() {
        super(System.currentTimeMillis());
    }

    public ImmutableDate(final java.util.Date date) {
        super(date.getTime());
    }

    @Override
    public ImmutableDate clone() {
        return new ImmutableDate(this);
    }

    @Override
    public void setDate(final int date) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setHours(final int i) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setMinutes(final int i) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setMonth(final int month) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSeconds(final int i) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setTime(final long date) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setYear(final int year) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}
