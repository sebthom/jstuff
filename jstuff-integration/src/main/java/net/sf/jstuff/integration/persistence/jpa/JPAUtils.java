/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.persistence.jpa;

import java.util.function.Supplier;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class JPAUtils {

   /**
    * Changes question marks in a query string to positional question marks.
    *
    * E.g. "?, ?, ?" becomes "?1, ?2, ?3"
    */
   public static String enumerateQuestionMarks(final String queryString) {
      if (queryString.isEmpty())
         return "";

      final var out = new StringBuilder();
      int startSearchAt = 0, foundAt = 0;

      for (int i = 1; (foundAt = queryString.indexOf('?', startSearchAt)) >= 0; i++) { // CHECKSTYLE:IGNORE InnerAssignment
         final String replaceValue = "?" + i;
         out.append(queryString, startSearchAt, foundAt).append(replaceValue);
         startSearchAt = foundAt + 1;
      }
      return out.append(queryString, startSearchAt, queryString.length()).toString();
   }

   public static final void executeTransactional(final EntityManager em, final Runnable code) {
      final EntityTransaction tx = em.getTransaction();
      tx.begin();
      try {
         code.run();
         tx.commit();
      } catch (final RuntimeException ex) {
         tx.rollback();
         throw ex;
      }
   }

   public static final <T> T executeTransactional(final EntityManager em, final Supplier<T> code) {
      final EntityTransaction tx = em.getTransaction();
      tx.begin();
      try {
         final T result = code.get();
         tx.commit();
         return result;
      } catch (final RuntimeException ex) {
         tx.rollback();
         throw ex;
      }
   }

   public static final <T> T mergeTransactional(final EntityManager em, final T entity) {
      final EntityTransaction tx = em.getTransaction();
      tx.begin();
      try {
         final T result = em.merge(entity);
         tx.commit();
         return result;
      } catch (final RuntimeException ex) {
         tx.rollback();
         throw ex;
      }
   }

   public static void setParameters(final Query query, final Object @Nullable... parameters) {
      if (parameters != null) {
         for (int i = 0; i < parameters.length; i++) {
            query.setParameter(i + 1, parameters[i]);
         }
      }
   }
}
