/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.spring.convert;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class AbstractConverter implements ConditionalGenericConverter {
   @Autowired
   protected ConversionService conversionService;

   protected AbstractConverter() {
   }

   protected AbstractConverter(final ConversionService conversionService) {
      this.conversionService = conversionService;
   }

   public ConversionService getConversionService() {
      return conversionService;
   }

   @Override
   public boolean matches(final TypeDescriptor sourceType, final TypeDescriptor targetType) {
      Args.notNull("targetType", targetType);

      if (!conversionService.canConvert(sourceType, targetType.getMapKeyTypeDescriptor()))
         return false;

      final boolean matches = conversionService.canConvert(sourceType, targetType.getMapValueTypeDescriptor());
      return matches;
   }

   public void setConversionService(final ConversionService conversionService) {
      this.conversionService = conversionService;
   }

}
