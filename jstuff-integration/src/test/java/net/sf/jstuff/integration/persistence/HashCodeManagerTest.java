/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import org.junit.Test;

import net.sf.jstuff.core.concurrent.Threads;
import net.sf.jstuff.core.io.SerializationUtils;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class HashCodeManagerTest {

   @Test
   public void testManagedHashCode() {
      /*
       * #1 client: new()
       */
      Entity client_e = new Entity().setLabel("client_e");
      final int client_HC = client_e.hashCode();

      assertThat(HashCodeManager.getManagedIdsCount()).isZero();
      assertThat(HashCodeManager.getManagedTrackingIdsCount()).isEqualTo(1);

      /*
       * #2 server: deserialize()
       */
      Entity server_e = SerializationUtils.clone(client_e);
      server_e.setLabel("server_e");
      final int server_HC = server_e.hashCode();

      assertThat(HashCodeManager.getManagedIdsCount()).isZero();
      assertThat(HashCodeManager.getManagedTrackingIdsCount()).isEqualTo(1);

      /*
       * #3 server: persist()
       */
      server_e.setId(22);
      assertThat(server_HC).isEqualTo(server_e.hashCode());

      assertThat(HashCodeManager.getManagedIdsCount()).isEqualTo(1);
      assertThat(HashCodeManager.getManagedTrackingIdsCount()).isEqualTo(1);

      /*
       * #4 client: applyId()
       */
      client_e.setId(22);
      assertThat(client_HC).isEqualTo(server_e.hashCode());

      assertThat(HashCodeManager.getManagedIdsCount()).isEqualTo(1);
      assertThat(HashCodeManager.getManagedTrackingIdsCount()).isEqualTo(1);

      /*
       * #5 server: loadFromDB()
       */
      Entity server_e2 = new Entity().setLabel("server_e2");
      server_e2.setId(22);
      assertThat(server_e2.hashCode()).hasSameHashCodeAs(server_e.hashCode());

      assertThat(HashCodeManager.getManagedIdsCount()).isEqualTo(1);
      assertThat(HashCodeManager.getManagedTrackingIdsCount()).isEqualTo(2);

      /*
       * #6 client: receive()
       */
      Entity client_e2 = SerializationUtils.clone(server_e2);
      client_e2.setLabel("client_e2");
      assertThat(client_e2.hashCode()).hasSameHashCodeAs(client_e.hashCode());

      assertThat(HashCodeManager.getManagedIdsCount()).isEqualTo(1);
      assertThat(HashCodeManager.getManagedTrackingIdsCount()).isEqualTo(2);

      client_e = null;
      client_e2 = null;
      server_e = null;
      server_e2 = null;

      System.gc();
      Threads.sleep(500);
      System.gc();
      Threads.sleep(500);

      assertThat(HashCodeManager.getManagedIdsCount()).isZero();
      assertThat(HashCodeManager.getManagedTrackingIdsCount()).isZero();

      /*
       * #7 server: loadFromDB()
       */
      final Entity server_e3 = new Entity().setLabel("server_e3");
      server_e3.setId(22);
      assertNotEquals(server_e3.hashCode(), server_HC);

      /*
       * #8 client: receive()
       */
      final Entity client_e3 = SerializationUtils.clone(server_e3);
      assertNotEquals(client_e3.hashCode(), client_HC);

      assertThat(HashCodeManager.getManagedIdsCount()).isEqualTo(1);
      assertThat(HashCodeManager.getManagedTrackingIdsCount()).isEqualTo(1);
   }
}
