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
package net.sf.jstuff.integration.spring.convert;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class AbstractConverter implements ConditionalGenericConverter {
    @Autowired
    protected ConversionService conversionService;

    public AbstractConverter() {
        super();
    }

    public AbstractConverter(final ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    public ConversionService getConversionService() {
        return conversionService;
    }

    public boolean matches(final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        if (!conversionService.canConvert(sourceType, targetType.getMapKeyTypeDescriptor()))
            return false;

        final boolean matches = conversionService.canConvert(sourceType, targetType.getMapValueTypeDescriptor());
        return matches;
    }

    public void setConversionService(final ConversionService conversionService) {
        this.conversionService = conversionService;
    }

}