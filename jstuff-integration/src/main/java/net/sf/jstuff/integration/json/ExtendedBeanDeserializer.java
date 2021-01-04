/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.json;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang3.reflect.ConstructorUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.BeanDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBase;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.module.SimpleModule;

import net.sf.jstuff.core.logging.Logger;

/**
 * https://stackoverflow.com/questions/62840486/deserializing-json-object-bi-directional-one-to-many-with-jackson-objectmapper/62873518#62873518
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ExtendedBeanDeserializer extends BeanDeserializer {
   private static final long serialVersionUID = 1L;

   private static final Logger LOG = Logger.create();

   /**
    * Replaces the existing BeanDeserializer instance in the given mapper
    */
   public static final void registerWith(final ObjectMapper mapper) {
      final SimpleModule module = new SimpleModule();
      module.setDeserializerModifier(new BeanDeserializerModifier() {
         @Override
         public JsonDeserializer<?> modifyDeserializer(final DeserializationConfig cfg, final BeanDescription beanDescr,
            final JsonDeserializer<?> deserializer) {
            if (deserializer instanceof BeanDeserializer)
               return new ExtendedBeanDeserializer((BeanDeserializer) deserializer);
            return deserializer;
         }
      });
      mapper.registerModule(module);
   }

   public ExtendedBeanDeserializer(final BeanDeserializerBase src) {
      super(src);
   }

   private Object getParentObject(final JsonParser p) {
      JsonStreamContext parentCtx = p.getParsingContext().getParent();
      if (parentCtx == null)
         return null;

      Object parentObject = parentCtx.getCurrentValue();
      if (parentObject == null)
         return null;

      if (parentObject instanceof Collection || parentObject instanceof Map || parentObject.getClass().isArray()) {
         parentCtx = parentCtx.getParent();
         if (parentCtx != null) {
            parentObject = parentCtx.getCurrentValue();
         }
      }
      return parentObject;
   }

   @Override
   protected Object deserializeFromObjectUsingNonDefault(final JsonParser p, final DeserializationContext ctxt) throws IOException {
      final Object parentObject = getParentObject(p);
      if (parentObject != null) {
         // determine constructor that takes parent object
         final Constructor<?> ctor = ConstructorUtils.getMatchingAccessibleConstructor(_beanType.getRawClass(), parentObject.getClass());
         if (ctor != null) {
            try {
               // instantiate object
               final Object bean = ctor.newInstance(parentObject);
               p.setCurrentValue(bean);
               // deserialize fields
               if (p.hasTokenId(JsonTokenId.ID_FIELD_NAME)) {
                  String propName = p.getCurrentName();
                  do {
                     p.nextToken();
                     final SettableBeanProperty prop = _beanProperties.find(propName);
                     if (prop == null) {
                        handleUnknownVanilla(p, ctxt, bean, propName);
                        continue;
                     }
                     try {
                        prop.deserializeAndSet(p, ctxt, bean);
                     } catch (final Exception e) {
                        wrapAndThrow(e, bean, propName, ctxt);
                     }
                  }
                  // CHECKSTYLE:IGNORE .* FOR NEXT LINE
                  while ((propName = p.nextFieldName()) != null);
               }
               return bean;
            } catch (final ReflectiveOperationException ex) {
               LOG.warn(ex);
            }
         }
      }

      return super.deserializeFromObjectUsingNonDefault(p, ctxt);
   }
}
