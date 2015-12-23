/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2015 Sebastian
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
package net.sf.jstuff.integration.spring.convert;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class StringToMapConverter extends AbstractConverter {
    private static final String ENTRY_SEPARATOR = ";";
    private static final String VALUE_ASSIGNMENT = "=>";

    public StringToMapConverter() {
        super();
    }

    public StringToMapConverter(final ConversionService conversionService) {
        super(conversionService);
    }

    @SuppressWarnings("unchecked")
    public Object convert(final Object source, final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        if (source == null)
            return null;
        final String sourceString = (String) source;
        if (StringUtils.isBlank(sourceString))
            return Collections.emptyMap();

        @SuppressWarnings("rawtypes")
        final Map result = new HashMap();

        for (final String entry : sourceString.split(ENTRY_SEPARATOR)) {
            final String[] entryArr = entry.split(VALUE_ASSIGNMENT);
            final String key = entryArr[0].trim();
            final String value = entryArr[1].trim();

            final Object targetKey = conversionService.convert(key, sourceType, targetType.getMapKeyTypeDescriptor(key));

            result.put(targetKey, conversionService.convert(value, sourceType, targetType.getMapValueTypeDescriptor()));
        }

        return result;
    }

    public Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new ConvertiblePair(String.class, Map.class));
    }

}