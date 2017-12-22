/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.integration.servlet.session;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.ObjectUtils;

import net.sf.jstuff.core.collection.ArrayUtils;
import net.sf.jstuff.core.collection.CollectionUtils;
import net.sf.jstuff.core.collection.Enumerations;
import net.sf.jstuff.core.collection.Maps;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class HttpSessionMap implements SessionMap {

    @SuppressWarnings("deprecation")
    private static String[] getValueNames(final HttpSession sess) {
        if (sess == null)
            return ArrayUtils.EMPTY_STRING_ARRAY;

        final String[] valueNames = sess.getValueNames();
        return valueNames == null ? ArrayUtils.EMPTY_STRING_ARRAY : valueNames;
    }

    private final HttpServletRequest request;

    public HttpSessionMap(final HttpServletRequest request) {
        Args.notNull("request", request);
        this.request = request;
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsKey(final Object key) {
        final HttpSession sess = request.getSession(false);
        if (sess == null)
            return false;
        return Enumerations.contains(sess.getAttributeNames(), key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean containsValue(final Object value) {
        final HttpSession sess = request.getSession(false);
        if (sess == null)
            return false;
        for (final String key : Enumerations.toIterable((Enumeration<String>) sess.getAttributeNames()))
            if (ObjectUtils.equals(sess.getAttribute(key), value))
                return true;
        return false;
    }

    @Override
    public Set<java.util.Map.Entry<String, Object>> entrySet() {
        final HttpSession sess = request.getSession(false);
        if (sess == null)
            return Collections.emptySet();

        final Map<String, Object> result = Maps.newHashMap();
        for (final String key : getValueNames(sess)) {
            result.put(key, sess.getAttribute(key));
        }
        return result.entrySet();
    }

    @Override
    public boolean exists() {
        return request.getSession(false) != null;
    }

    @Override
    public Object get(final Object key) {
        final HttpSession sess = request.getSession(false);
        if (sess == null)
            return null;
        return sess.getAttribute(key == null ? null : key.toString());
    }

    @Override
    public Object get(final String key, final Object defaultValueIfNull) {
        final Object val = get(key);
        if (val == null)
            return defaultValueIfNull;
        return val;
    }

    @Override
    public Object getId() {
        final HttpSession sess = request.getSession(true);
        return sess.getId();
    }

    @Override
    public void invalidate() {
        final HttpSession sess = request.getSession(false);
        if (sess == null)
            return;
        sess.invalidate();
    }

    @Override
    public boolean isEmpty() {
        final HttpSession sess = request.getSession(false);
        if (sess == null)
            return true;
        return !sess.getAttributeNames().hasMoreElements();
    }

    @Override
    public Set<String> keySet() {
        final HttpSession sess = request.getSession(false);
        if (sess == null)
            return Collections.emptySet();
        final Set<String> result = CollectionUtils.newHashSet();
        CollectionUtils.addAll(result, getValueNames(sess));
        return result;
    }

    @Override
    public Object put(final String key, final Object value) {
        final HttpSession sess = request.getSession(true);
        final Object oldValue = sess.getAttribute(key);
        sess.setAttribute(key, value);
        return oldValue;
    }

    @Override
    public void putAll(final Map<? extends String, ? extends Object> map) {
        final HttpSession sess = request.getSession(true);
        for (final Entry<? extends String, ? extends Object> e : map.entrySet()) {
            sess.setAttribute(e.getKey(), e.getValue());
        }
    }

    @Override
    public Object remove(final Object key) {
        final HttpSession sess = request.getSession(false);
        if (sess == null)
            return null;

        final Object oldValue = sess.getAttribute(key == null ? null : key.toString());
        sess.removeAttribute(key == null ? null : key.toString());
        return oldValue;
    }

    @Override
    public int size() {
        final HttpSession sess = request.getSession(false);
        if (sess == null)
            return 0;

        return getValueNames(sess).length;
    }

    @Override
    public Collection<Object> values() {
        final HttpSession sess = request.getSession(false);
        if (sess == null)
            return Collections.emptyList();

        final Collection<Object> result = CollectionUtils.newArrayList();
        for (final String key : getValueNames(sess)) {
            result.add(sess.getAttribute(key));
        }
        return result;
    }
}
