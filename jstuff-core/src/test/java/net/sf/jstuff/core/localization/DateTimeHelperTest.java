/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.localization;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class DateTimeHelperTest {

   @Test
   public void testIsValidDate() {
      DateTimeHelper c;
      DateFormat df;

      // testing Validator with german locale
      c = new DateTimeHelper(Locale.GERMANY);
      assertThat(c.isValidDate("26.12.02")).isTrue(); //SHORT
      assertThat(c.isValidDate("26.12.2002")).isTrue(); //MEDIUM
      assertThat(c.isValidDate("26. Dezember 2002")).isTrue(); //LONG
      assertThat(c.isValidDate("Donnerstag, 26. Dezember 2002")).isTrue(); //FULL

      df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY);
      assertThat(c.isValidDate(df.format(new Date()))).isTrue();
      df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMANY);
      assertThat(c.isValidDate(df.format(new Date()))).isTrue();
      df = DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
      assertThat(c.isValidDate(df.format(new Date()))).isTrue();
      df = DateFormat.getDateInstance(DateFormat.FULL, Locale.GERMANY);
      assertThat(c.isValidDate(df.format(new Date()))).isTrue();

      df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US);
      assertThat(c.isValidDate(df.format(new Date()))).isFalse();
      df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
      assertThat(c.isValidDate(df.format(new Date()))).isFalse();
      df = DateFormat.getDateInstance(DateFormat.LONG, Locale.US);
      assertThat(c.isValidDate(df.format(new Date()))).isFalse();
      df = DateFormat.getDateInstance(DateFormat.FULL, Locale.US);
      assertThat(c.isValidDate(df.format(new Date()))).isFalse();

      // testing Validator with us locale
      c = new DateTimeHelper(Locale.US);
      df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US);
      assertThat(c.isValidDate(df.format(new Date()))).isTrue();
      df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
      assertThat(c.isValidDate(df.format(new Date()))).isTrue();
      df = DateFormat.getDateInstance(DateFormat.LONG, Locale.US);
      assertThat(c.isValidDate(df.format(new Date()))).isTrue();
      df = DateFormat.getDateInstance(DateFormat.FULL, Locale.US);
      assertThat(c.isValidDate(df.format(new Date()))).isTrue();

      df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY);
      assertThat(c.isValidDate(df.format(new Date()))).isFalse();
      df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMANY);
      assertThat(c.isValidDate(df.format(new Date()))).isFalse();
      df = DateFormat.getDateInstance(DateFormat.LONG, Locale.GERMANY);
      assertThat(c.isValidDate(df.format(new Date()))).isFalse();
      df = DateFormat.getDateInstance(DateFormat.FULL, Locale.GERMANY);
      assertThat(c.isValidDate(df.format(new Date()))).isFalse();
   }

   public void testIsValidDateTime() {
      DateTimeHelper c;
      DateFormat df;

      // testing Validator with german locale
      c = new DateTimeHelper(Locale.GERMANY);
      /*assertThat(c.isValidDate("26.12.02")).isTrue();  //SHORT
      assertThat(c.isValidDate("26.12.2002")).isTrue();  //MEDIUM
      assertThat(c.isValidDate("26. Dezember 2002")).isTrue();  //LONG
      assertThat(c.isValidDate("Donnerstag, 26. Dezember 2002")).isTrue(); //FULL
      */
      df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.GERMANY);
      assertThat(c.isValidDateTime(df.format(new Date()))).isTrue();
      df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.GERMANY);
      assertThat(c.isValidDateTime(df.format(new Date()))).isTrue();
      df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, Locale.GERMANY);
      assertThat(c.isValidDateTime(df.format(new Date()))).isTrue();
      df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, Locale.GERMANY);
      assertThat(c.isValidDateTime(df.format(new Date()))).isTrue();

      df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.US);
      assertThat(c.isValidDateTime(df.format(new Date()))).isFalse();
      df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.US);
      assertThat(c.isValidDateTime(df.format(new Date()))).isFalse();
      df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, Locale.US);
      assertThat(c.isValidDateTime(df.format(new Date()))).isFalse();
      df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, Locale.US);
      assertThat(c.isValidDateTime(df.format(new Date()))).isFalse();

      // testing Validator with us locale
      c = new DateTimeHelper(Locale.US);
      df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.US);
      assertThat(c.isValidDateTime(df.format(new Date()))).isTrue();
      df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.US);
      assertThat(c.isValidDateTime(df.format(new Date()))).isTrue();
      df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, Locale.US);
      assertThat(c.isValidDateTime(df.format(new Date()))).isTrue();
      df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, Locale.US);
      assertThat(c.isValidDateTime(df.format(new Date()))).isTrue();

      df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.GERMANY);
      assertThat(c.isValidDateTime(df.format(new Date()))).isFalse();
      df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.GERMANY);
      assertThat(c.isValidDateTime(df.format(new Date()))).isFalse();
      df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, Locale.GERMANY);
      assertThat(c.isValidDateTime(df.format(new Date()))).isFalse();
      df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, Locale.GERMANY);
      assertThat(c.isValidDateTime(df.format(new Date()))).isFalse();
   }

   public void testIsValidTime() {
      DateTimeHelper c;
      DateFormat df;

      // testing Validator with german locale
      c = new DateTimeHelper(Locale.GERMANY);
      assertThat(c.isValidTime("21:45")).isTrue(); //SHORT
      assertThat(c.isValidTime("21:45:30")).isTrue(); //MEDIUM
      assertThat(c.isValidTime("21:45:43 CET")).isTrue(); //LONG
      assertThat(c.isValidTime("21:46 Uhr CET")).isTrue(); //FULL

      df = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.GERMANY);
      assertThat(c.isValidTime(df.format(new Date()))).isTrue();
      df = DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.GERMANY);
      assertThat(c.isValidTime(df.format(new Date()))).isTrue();
      df = DateFormat.getTimeInstance(DateFormat.LONG, Locale.GERMANY);
      assertThat(c.isValidTime(df.format(new Date()))).isTrue();
      /*
       * the next one fails if summer time CEST is enabled on the client
      df = DateFormat.getTimeInstance(DateFormat.FULL, Locale.GERMANY);
      assertThat(c.isValidTime(df.format(new Date()))).isTrue();
      */
      /*
      the next tests are problematic because 9:42 PM will be
      interpreted as 09:42 and no error will arrise:
      ----------------------------------------------
      df = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.US);
      assertThat(c.isValidTime(df.format(new Date()))).isFalse();
      df = DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.US);
      assertThat(c.isValidTime(df.format(new Date()))).isFalse();
      df = DateFormat.getTimeInstance(DateFormat.LONG, Locale.US);
      assertThat(c.isValidTime(df.format(new Date()))).isFalse();
      df = DateFormat.getTimeInstance(DateFormat.FULL, Locale.US);
      assertThat(c.isValidTime(df.format(new Date()))).isFalse();
      */

      // testing Validator with us locale
      c = new DateTimeHelper(Locale.US);
      df = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.US);
      assertThat(c.isValidTime(df.format(new Date()))).isTrue();
      df = DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.US);
      assertThat(c.isValidTime(df.format(new Date()))).isTrue();
      df = DateFormat.getTimeInstance(DateFormat.LONG, Locale.US);
      assertThat(c.isValidTime(df.format(new Date()))).isTrue();
      df = DateFormat.getTimeInstance(DateFormat.FULL, Locale.US);
      assertThat(c.isValidTime(df.format(new Date()))).isTrue();

      df = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.GERMANY);
      assertThat(c.isValidTime(df.format(new Date()))).isFalse();
      df = DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.GERMANY);
      assertThat(c.isValidTime(df.format(new Date()))).isFalse();
      df = DateFormat.getTimeInstance(DateFormat.LONG, Locale.GERMANY);
      assertThat(c.isValidTime(df.format(new Date()))).isFalse();
      df = DateFormat.getTimeInstance(DateFormat.FULL, Locale.GERMANY);
      assertThat(c.isValidTime(df.format(new Date()))).isFalse();
   }

}
