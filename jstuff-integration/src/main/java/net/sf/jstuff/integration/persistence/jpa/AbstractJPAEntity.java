/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.persistence.jpa;

import static net.sf.jstuff.core.Strings.*;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.date.ImmutableDate;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.reflection.Fields;
import net.sf.jstuff.core.types.Identifiable;
import net.sf.jstuff.integration.persistence.HashCodeManager;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@MappedSuperclass
public abstract class AbstractJPAEntity<KeyType extends Serializable> implements Serializable, Identifiable<KeyType> {
   private static final long serialVersionUID = 1L;

   private static final Logger LOG = Logger.create();

   /* ******************************************************************************
    * Consistent HashCode Management
    * ******************************************************************************/
   @javax.persistence.Transient
   private final String _hashCodeTrackingId;

   /* ******************************************************************************
    * Generic Entity Properties
    * ******************************************************************************/
   @Temporal(TemporalType.TIMESTAMP)
   @Column(name = "_createdOn", updatable = false, nullable = false)
   // we assign a value here to work around http://opensource.atlassian.com/projects/hibernate/browse/EJB-46
   // which may also exist with other JPA implementations
   private Date _firstPersistedOn = new Date();

   @Temporal(TemporalType.TIMESTAMP)
   @Column(name = "_modifiedOn", nullable = false)
   private @Nullable Date _lastPersistedOn;

   @Basic(optional = false)
   @Column(nullable = false)
   private boolean _isMarkedAsDeleted = false;

   @Version
   @Column(nullable = false)
   private int _version = 0;

   protected AbstractJPAEntity() {
      _hashCodeTrackingId = HashCodeManager.onEntityInstantiated(this);
   }

   public final @Nullable ImmutableDate _getFirstPersistedOn() {
      if (_isNew())
         return null;
      return ImmutableDate.of(_firstPersistedOn);
   }

   public final @Nullable ImmutableDate _getLastPersistedOn() {
      return _lastPersistedOn != null ? ImmutableDate.of(_lastPersistedOn) : null;
   }

   public final int _getVersion() {
      return _version;
   }

   public final boolean _isMarkedAsDeleted() {
      return _isMarkedAsDeleted;
   }

   /**
    * @return true if this is a new entity that has not been persisted yet
    */
   public final boolean _isNew() {
      return getId() != null;
   }

   public void _setMarkedAsDeleted(final boolean isMarkedAsDeleted) {
      _isMarkedAsDeleted = isMarkedAsDeleted;
   }

   @Override
   public boolean equals(final @Nullable Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      final AbstractJPAEntity<?> other = (AbstractJPAEntity<?>) obj;
      return Objects.equals(getId(), other.getId());
   }

   @Override
   public Object getIdRealm() {
      return getClass();
   }

   @Override
   public int hashCode() {
      return HashCodeManager.hashCodeFor(this, _hashCodeTrackingId);
   }

   @PostPersist
   private void onAfterIdSet() {
      HashCodeManager.onIdSet(this, _hashCodeTrackingId);
   }

   @PrePersist
   private void onPrePersist() {
      _lastPersistedOn = new Date();
   }

   @PreUpdate
   private void onPreUpdate() {
      _lastPersistedOn = new Date();
   }

   public CharSequence toDebugString() {
      final var sb = new StringBuilder(64);
      Class<?> currClazz = getClass();
      final var intend = new StringBuilder("");

      try {
         while (currClazz != Object.class && currClazz != null) {
            sb.append(intend).append(currClazz).append(" *** START ***").append(NEW_LINE);
            intend.append("  ");
            for (final Field field : currClazz.getDeclaredFields())
               if (!Fields.isStatic(field) && !field.getName().startsWith("class$")) {
                  final Object fieldValue = Fields.read(this, field);
                  if (fieldValue == null || !field.getType().isAssignableFrom(AbstractJPAEntity.class)) {
                     sb.append(intend).append("[").append(field.getName()).append("] ") //
                        .append(fieldValue).append(" | ").append(field.getType().getName()).append(NEW_LINE);
                  } else {
                     final AbstractJPAEntity<?> referencedEntity = (AbstractJPAEntity<?>) fieldValue;
                     sb.append(intend).append("[").append(field.getName()).append("] id=").append(referencedEntity.getId()).append(" | ")
                        .append(referencedEntity.getClass().getName()).append(NEW_LINE);
                  }
               }
            sb.append(NEW_LINE);

            currClazz = currClazz.getSuperclass();
         }
         return sb.append(getClass()).append(" *** END ***").append(NEW_LINE);
      } catch (final Exception ex) {
         LOG.warn(ex, "toDebugString() failed on %s", this);
         return toString();
      }
   }

   @Override
   public final String toString() {
      return getClass().getName() + "[id=" + getId() + ", version=, " + _getVersion() + "hashCode=" + hashCode() + "]";
   }
}
