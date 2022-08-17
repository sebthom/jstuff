/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class PagedList<E> extends ArrayList<E> {
   private static final long serialVersionUID = 1L;

   private Class<E> elementType;
   private int start;
   private int totalCount;

   public PagedList() {
   }

   public PagedList(final Class<E> elementType) {
      this.elementType = elementType;
   }

   public PagedList(final Class<E> elementType, final List<E> elements, final int start, final int totalCount) {
      super(elements);
      this.elementType = elementType;
      this.start = start;
      this.totalCount = totalCount;
   }

   public Class<E> getElementType() {
      return elementType;
   }

   public int getStart() {
      return start;
   }

   public int getTotalCount() {
      return totalCount;
   }

   /**
    * @return true if more elements are available for retrieval
    */
   public boolean isMoreElementsAvailable() {
      return totalCount > start + size();
   }

   public void setElementType(final Class<E> elementType) {
      this.elementType = elementType;
   }

   public void setStart(final int start) {
      this.start = start;
   }

   public void setTotalCount(final int total) {
      this.totalCount = total;
   }

   @Override
   public String toString() {
      return ToStringBuilder.reflectionToString(this);
   }
}
