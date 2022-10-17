/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.spring.convert;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

import net.sf.jstuff.core.Strings;

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
   public @Nullable Object convert(final @Nullable Object source, final TypeDescriptor sourceType, final TypeDescriptor targetType) {
      final String sourceString = (String) source;
      if (sourceString == null || Strings.isBlank(sourceString))
         return Collections.emptyMap();

      final var result = new HashMap<>();

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
   public @Nullable Set<ConvertiblePair> getConvertibleTypes() {
      return Collections.singleton(new ConvertiblePair(String.class, Map.class));
   }

}
