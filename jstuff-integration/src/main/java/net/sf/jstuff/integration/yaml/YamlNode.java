/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.yaml;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
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
      return (YamlNode) super.put(fieldName, v);
   }

   @Override
   public YamlNode put(final String fieldName, final BigInteger v) {
      return (YamlNode) super.put(fieldName, v);
   }

   @Override
   public YamlNode put(final String fieldName, final boolean v) {
      return (YamlNode) super.put(fieldName, v);
   }

   @Override
   public YamlNode put(final String fieldName, final Boolean v) {
      return (YamlNode) super.put(fieldName, v);
   }

   @Override
   public YamlNode put(final String fieldName, final byte[] v) {
      return (YamlNode) super.put(fieldName, v);
   }

   @Override
   public YamlNode put(final String fieldName, final double v) {
      return (YamlNode) super.put(fieldName, v);
   }

   @Override
   public YamlNode put(final String fieldName, final Double v) {
      return (YamlNode) super.put(fieldName, v);
   }

   @Override
   public YamlNode put(final String fieldName, final float v) {
      return (YamlNode) super.put(fieldName, v);
   }

   @Override
   public YamlNode put(final String fieldName, final Float v) {
      return (YamlNode) super.put(fieldName, v);
   }

   @Override
   public YamlNode put(final String fieldName, final int v) {
      return (YamlNode) super.put(fieldName, v);
   }

   @Override
   public YamlNode put(final String fieldName, final Integer v) {
      return (YamlNode) super.put(fieldName, v);
   }

   @Override
   public YamlNode put(final String fieldName, final long v) {
      return (YamlNode) super.put(fieldName, v);
   }

   @Override
   public YamlNode put(final String fieldName, final Long v) {
      return (YamlNode) super.put(fieldName, v);
   }

   public YamlNode put(final String fieldName, final Object obj) {
      super.set(fieldName, obj == null ? null : OBJECT_MAPPER.valueToTree(obj));
      return this;
   }

   @Override
   public YamlNode put(final String fieldName, final short v) {
      return (YamlNode) super.put(fieldName, v);
   }

   @Override
   public YamlNode put(final String fieldName, final Short v) {
      return (YamlNode) super.put(fieldName, v);
   }

   @Override
   public YamlNode put(final String fieldName, final String v) {
      return (YamlNode) super.put(fieldName, v);
   }

   @Override
   public YamlNode putNull(final String fieldName) {
      return (YamlNode) super.putNull(fieldName);
   }

   @Override
   public YamlNode putPOJO(final String fieldName, final Object pojo) {
      return (YamlNode) super.putPOJO(fieldName, pojo);
   }

   @Override
   public YamlNode putRawValue(final String fieldName, final RawValue raw) {
      return (YamlNode) super.putRawValue(fieldName, raw);
   }

   @Override
   public YamlNode remove(final Collection<String> fieldNames) {
      return (YamlNode) super.remove(fieldNames);
   }

   @Override
   public YamlNode remove(final String fieldName) {
      return (YamlNode) super.remove(fieldName);
   }

   @Override
   public YamlNode removeAll() {
      return (YamlNode) super.removeAll();
   }

   @Override
   public YamlNode retain(final Collection<String> fieldNames) {
      return (YamlNode) super.retain(fieldNames);
   }

   @Override
   public YamlNode retain(final String... fieldNames) {
      return (YamlNode) super.retain(fieldNames);
   }

   @Override
   @SuppressWarnings("unchecked")
   public YamlNode set(final String fieldName, final JsonNode value) {
      return (YamlNode) super.set(fieldName, value);
   }

   @Override
   public <T extends JsonNode> T setAll(final Map<String, ? extends JsonNode> properties) {
      return super.setAll(properties);
   }

   @Override
   @SuppressWarnings("unchecked")
   public YamlNode setAll(final ObjectNode other) {
      return (YamlNode) super.setAll(other);
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
   public <T extends JsonNode> T without(final Collection<String> fieldNames) {
      return super.without(fieldNames);
   }

   @Override
   @SuppressWarnings("unchecked")
   public YamlNode without(final String fieldName) {
      return (YamlNode) super.without(fieldName);
   }
}
