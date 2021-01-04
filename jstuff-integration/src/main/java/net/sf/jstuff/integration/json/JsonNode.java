/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.json;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.RawValue;

/**
 * {@link ObjectNode} based JsonNode with improved fluent API.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public final class JsonNode extends ObjectNode {

   private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper(new JsonFactory());

   private static final long serialVersionUID = 1L;

   public static JsonNode create() {
      return new JsonNode(OBJECT_MAPPER.getNodeFactory());
   }

   private JsonNode(final JsonNodeFactory nc) {
      super(nc);
   }

   public JsonNode compute(final Consumer<JsonNode> consumer) {
      consumer.accept(this);
      return this;
   }

   @Override
   public JsonNode put(final String fieldName, final BigDecimal v) {
      return (JsonNode) super.put(fieldName, v);
   }

   @Override
   public JsonNode put(final String fieldName, final BigInteger v) {
      return (JsonNode) super.put(fieldName, v);
   }

   @Override
   public JsonNode put(final String fieldName, final boolean v) {
      return (JsonNode) super.put(fieldName, v);
   }

   @Override
   public JsonNode put(final String fieldName, final Boolean v) {
      return (JsonNode) super.put(fieldName, v);
   }

   @Override
   public JsonNode put(final String fieldName, final byte[] v) {
      return (JsonNode) super.put(fieldName, v);
   }

   @Override
   public JsonNode put(final String fieldName, final double v) {
      return (JsonNode) super.put(fieldName, v);
   }

   @Override
   public JsonNode put(final String fieldName, final Double v) {
      return (JsonNode) super.put(fieldName, v);
   }

   @Override
   public JsonNode put(final String fieldName, final float v) {
      return (JsonNode) super.put(fieldName, v);
   }

   @Override
   public JsonNode put(final String fieldName, final Float v) {
      return (JsonNode) super.put(fieldName, v);
   }

   @Override
   public JsonNode put(final String fieldName, final int v) {
      return (JsonNode) super.put(fieldName, v);
   }

   @Override
   public JsonNode put(final String fieldName, final Integer v) {
      return (JsonNode) super.put(fieldName, v);
   }

   @Override
   public JsonNode put(final String fieldName, final long v) {
      return (JsonNode) super.put(fieldName, v);
   }

   @Override
   public JsonNode put(final String fieldName, final Long v) {
      return (JsonNode) super.put(fieldName, v);
   }

   public JsonNode put(final String fieldName, final Object obj) {
      super.set(fieldName, obj == null ? null : OBJECT_MAPPER.valueToTree(obj));
      return this;
   }

   @Override
   public JsonNode put(final String fieldName, final short v) {
      return (JsonNode) super.put(fieldName, v);
   }

   @Override
   public JsonNode put(final String fieldName, final Short v) {
      return (JsonNode) super.put(fieldName, v);
   }

   @Override
   public JsonNode put(final String fieldName, final String v) {
      return (JsonNode) super.put(fieldName, v);
   }

   @Override
   public JsonNode putNull(final String fieldName) {
      return (JsonNode) super.putNull(fieldName);
   }

   @Override
   public JsonNode putPOJO(final String fieldName, final Object pojo) {
      return (JsonNode) super.putPOJO(fieldName, pojo);
   }

   @Override
   public JsonNode putRawValue(final String fieldName, final RawValue raw) {
      return (JsonNode) super.putRawValue(fieldName, raw);
   }

   @Override
   public JsonNode remove(final Collection<String> fieldNames) {
      return (JsonNode) super.remove(fieldNames);
   }

   @Override
   public JsonNode remove(final String fieldName) {
      return (JsonNode) super.remove(fieldName);
   }

   @Override
   public JsonNode removeAll() {
      return (JsonNode) super.removeAll();
   }

   @Override
   public JsonNode retain(final Collection<String> fieldNames) {
      return (JsonNode) super.retain(fieldNames);
   }

   @Override
   public JsonNode retain(final String... fieldNames) {
      return (JsonNode) super.retain(fieldNames);
   }

   @Override
   @SuppressWarnings("unchecked")
   public JsonNode set(final String fieldName, final com.fasterxml.jackson.databind.JsonNode value) {
      return (JsonNode) super.set(fieldName, value);
   }

   @Override
   public <T extends com.fasterxml.jackson.databind.JsonNode> T setAll(final Map<String, ? extends com.fasterxml.jackson.databind.JsonNode> properties) {
      return super.setAll(properties);
   }

   @Override
   @SuppressWarnings("unchecked")
   public JsonNode setAll(final ObjectNode other) {
      return (JsonNode) super.setAll(other);
   }

   @Override
   public <T extends com.fasterxml.jackson.databind.JsonNode> T without(final Collection<String> fieldNames) {
      return super.without(fieldNames);
   }

   @Override
   @SuppressWarnings("unchecked")
   public JsonNode without(final String fieldName) {
      return (JsonNode) super.without(fieldName);
   }
}
