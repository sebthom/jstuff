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
package net.sf.jstuff.core.collection;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class PagedList<E> extends ArrayList<E> {
   private static final long serialVersionUID = 1L;

   private Class<E> elementType;
   private int start;
   private int totalCount;

   public PagedList() {
      super();
   }

   public PagedList(final Class<E> elementType) {
      super();
      setElementType(elementType);
   }

   public PagedList(final Class<E> elementType, final List<E> elements, final int start, final int totalCount) {
      super(elements);
      setElementType(elementType);
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
