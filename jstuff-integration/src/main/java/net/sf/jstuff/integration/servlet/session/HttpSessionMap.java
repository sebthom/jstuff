/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.servlet.session;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.asNonNullUnsafe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import net.sf.jstuff.core.collection.Enumerations;
import net.sf.jstuff.core.collection.Loops;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class HttpSessionMap implements SessionMap {

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
   public boolean containsKey(final @Nullable Object key) {
      final HttpSession sess = request.getSession(false);
      if (sess == null)
         return false;
      return Enumerations.contains(sess.getAttributeNames(), key);
   }

   @Override
   public boolean containsValue(final @Nullable Object value) {
      final HttpSession sess = request.getSession(false);
      if (sess == null)
         return false;
      for (final String key : Enumerations.toIterable(sess.getAttributeNames()))
         if (Objects.equals(sess.getAttribute(key), value))
            return true;
      return false;
   }

   @Override
   public Set<Map.Entry<String, Object>> entrySet() {
      final HttpSession sess = request.getSession(false);
      if (sess == null)
         return Collections.emptySet();

      final Map<String, Object> result = new HashMap<>();
      Loops.forEach(sess.getAttributeNames(), key -> result.put(key, asNonNullUnsafe(sess.getAttribute(key))));
      return result.entrySet();
   }

   @Override
   public boolean exists() {
      return request.getSession(false) != null;
   }

   @Override
   public @Nullable Object get(final @Nullable Object key) {
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
      final HttpSession sess = request.getSession();
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
      final var result = new HashSet<String>();
      Loops.forEach(sess.getAttributeNames(), result::add);
      return result;
   }

   @Override
   public @Nullable Object put(final @Nullable String key, final Object value) {
      final HttpSession sess = request.getSession();
      final Object oldValue = sess.getAttribute(key);
      sess.setAttribute(key, value);
      return oldValue;
   }

   @Override
   public void putAll(final Map<? extends String, @NonNull ?> map) {
      final HttpSession sess = request.getSession();
      for (final var entry : map.entrySet()) {
         sess.setAttribute(entry.getKey(), entry.getValue());
      }
   }

   @Override
   public @Nullable Object remove(final @Nullable Object key) {
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

      return Enumerations.size(sess.getAttributeNames());
   }

   @Override
   public Collection<Object> values() {
      final HttpSession sess = request.getSession(false);
      if (sess == null)
         return Collections.emptyList();

      final Collection<Object> result = new ArrayList<>();
      Loops.forEach(sess.getAttributeNames(), key -> result.add(asNonNullUnsafe(sess.getAttribute(key))));
      return result;
   }
}
