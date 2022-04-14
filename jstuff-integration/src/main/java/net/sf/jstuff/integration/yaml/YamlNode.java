/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.yaml;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.RawValue;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

/**
 * {@link ObjectNode} based YamlNode with improved fluent API.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public final class YamlNode extends ObjectNode {

   private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper(new YAMLFactory() //
      .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES) //
      .enable(YAMLGenerator.Feature.INDENT_ARRAYS) //
      .disable(YAMLGenerator.Feature.SPLIT_LINES) //
      .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER) //
   );

   private static final long serialVersionUID = 1L;

   public static YamlNode create() {
      return new YamlNode(OBJECT_MAPPER.getNodeFactory());
   }

   private YamlNode(final JsonNodeFactory nc) {
      super(nc);
   }

   public YamlNode compute(final Consumer<YamlNode> consumer) {
      consumer.accept(this);
      return this;
   }

   @Override
   public YamlNode put(final String fieldName, final BigDecimal v) {
      super.put(fieldName, v);
      return this;
   }

   @Override
   public YamlNode put(final String fieldName, final BigInteger v) {
      super.put(fieldName, v);
      return this;
   }

   @Override
   public YamlNode put(final String fieldName, final boolean v) {
      super.put(fieldName, v);
      return this;
   }

   @Override
   public YamlNode put(final String fieldName, final Boolean v) {
      super.put(fieldName, v);
      return this;
   }

   @Override
   public YamlNode put(final String fieldName, final byte[] v) {
      super.put(fieldName, v);
      return this;
   }

   /**
    * Method that will set specified field, replacing old value, if any.
    *
    * @param value to set field to; if null, will be converted
    *           to a {@link NullNode} first (to remove field entry, call
    *           {@link #remove} instead)
    *
    * @return This node (to allow chaining)
    */
   @Override
   public YamlNode put(final String fieldName, final com.fasterxml.jackson.databind.JsonNode value) {
      set(fieldName, value);
      return this;
   }

   @Override
   public YamlNode put(final String fieldName, final double v) {
      super.put(fieldName, v);
      return this;
   }

   @Override
   public YamlNode put(final String fieldName, final Double v) {
      super.put(fieldName, v);
      return this;
   }

   @Override
   public YamlNode put(final String fieldName, final float v) {
      super.put(fieldName, v);
      return this;
   }

   @Override
   public YamlNode put(final String fieldName, final Float v) {
      super.put(fieldName, v);
      return this;
   }

   @Override
   public YamlNode put(final String fieldName, final int v) {
      super.put(fieldName, v);
      return this;
   }

   @Override
   public YamlNode put(final String fieldName, final Integer v) {
      super.put(fieldName, v);
      return this;
   }

   @Override
   public YamlNode put(final String fieldName, final long v) {
      super.put(fieldName, v);
      return this;
   }

   @Override
   public YamlNode put(final String fieldName, final Long v) {
      super.put(fieldName, v);
      return this;
   }

   public YamlNode put(final String fieldName, final Object obj) {
      super.set(fieldName, obj == null ? null : OBJECT_MAPPER.valueToTree(obj));
      return this;
   }

   @Override
   public YamlNode put(final String fieldName, final short v) {
      super.put(fieldName, v);
      return this;
   }

   @Override
   public YamlNode put(final String fieldName, final Short v) {
      super.put(fieldName, v);
      return this;
   }

   @Override
   public YamlNode put(final String fieldName, final String v) {
      super.put(fieldName, v);
      return this;
   }

   @Override
   public YamlNode putNull(final String fieldName) {
      super.putNull(fieldName);
      return this;
   }

   @Override
   public YamlNode putPOJO(final String fieldName, final Object pojo) {
      super.putPOJO(fieldName, pojo);
      return this;
   }

   @Override
   public YamlNode putRawValue(final String fieldName, final RawValue raw) {
      super.putRawValue(fieldName, raw);
      return this;
   }

   @Override
   public YamlNode remove(final Collection<String> fieldNames) {
      super.remove(fieldNames);
      return this;
   }

   /**
    * <b>IMPORTANT:</b> Does NOT return this node for chaining.
    * <p>
    * {@inheritDoc}
    */
   @Override
   public com.fasterxml.jackson.databind.JsonNode remove(final String fieldName) {
      final com.fasterxml.jackson.databind.JsonNode removedValue = super.remove(fieldName);
      return removedValue;
   }

   @Override
   public YamlNode removeAll() {
      super.removeAll();
      return this;
   }

   /**
    * <b>IMPORTANT:</b> Does NOT return this node for chaining.
    * <p>
    * {@inheritDoc}
    */
   @Override
   public com.fasterxml.jackson.databind.JsonNode replace(final String fieldName, final com.fasterxml.jackson.databind.JsonNode value) {
      final com.fasterxml.jackson.databind.JsonNode replacedValue = super.replace(fieldName, value);
      return replacedValue;
   }

   @Override
   public YamlNode retain(final Collection<String> fieldNames) {
      super.retain(fieldNames);
      return this;
   }

   @Override
   public YamlNode retain(final String... fieldNames) {
      super.retain(fieldNames);
      return this;
   }

   @Override
   @SuppressWarnings("unchecked")
   public YamlNode set(final String fieldName, final com.fasterxml.jackson.databind.JsonNode value) {
      super.set(fieldName, value);
      return this;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T extends com.fasterxml.jackson.databind.JsonNode> T setAll(
      final Map<String, ? extends com.fasterxml.jackson.databind.JsonNode> properties) {
      super.setAll(properties);
      return (T) this;
   }

   @Override
   @SuppressWarnings("unchecked")
   public YamlNode setAll(final ObjectNode other) {
      super.setAll(other);
      return this;
   }

   @Override
   public String toPrettyString() {
      try {
         return OBJECT_MAPPER.writeValueAsString(this);
      } catch (final JsonProcessingException ex) {
         return ex.getMessage();
      }
   }

   @Override
   public String toString() {
      return toPrettyString();
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T extends com.fasterxml.jackson.databind.JsonNode> T without(final Collection<String> fieldNames) {
      super.without(fieldNames);
      return (T) this;
   }

   @Override
   @SuppressWarnings("unchecked")
   public YamlNode without(final String fieldName) {
      super.without(fieldName);
      return this;
   }
}
