/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.spring.convert;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class StringToMapConverter extends AbstractConverter {
   private static final char ENTRY_SEPARATOR = ';';
   private static final String VALUE_ASSIGNMENT = "=>";

   public StringToMapConverter() {
   }

   public StringToMapConverter(final ConversionService conversionService) {
      super(conversionService);
   }

   @Override
   @SuppressWarnings("unchecked")
   public Object convert(final Object source, final TypeDescriptor sourceType, final TypeDescriptor targetType) {
      Args.notNull("targetType", targetType);

      final String sourceString = (String) source;
      if (StringUtils.isBlank(sourceString))
         return Collections.emptyMap();

      @SuppressWarnings("rawtypes")
      final Map result = new HashMap();

      for (final String entry : Strings.split(sourceString, ENTRY_SEPARATOR)) {
         final String[] entryArr = Strings.splitByWholeSeparator(entry, VALUE_ASSIGNMENT);
         final String key = entryArr[0].trim();
         final String value = entryArr[1].trim();

         result.put( //
            conversionService.convert(key, sourceType, targetType.getMapKeyTypeDescriptor(key)), //
            conversionService.convert(value, sourceType, targetType.getMapValueTypeDescriptor()) //
         );
      }

      return result;
   }

   @Override
   public Set<ConvertiblePair> getConvertibleTypes() {
      return Collections.singleton(new ConvertiblePair(String.class, Map.class));
   }

}
