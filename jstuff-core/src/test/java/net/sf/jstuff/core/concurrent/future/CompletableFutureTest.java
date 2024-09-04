/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent.future;

import java.util.concurrent.CompletableFuture;

import org.junit.Test;

/**
 * Tests the cancellation behaviour of {@link CompletableFuture}.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class CompletableFutureTest extends AbstractFutureTest {

   @Test
   public void testCompletedFuture() {
      testCompletedFuture(CompletableFuture::new, CompletableFuture::completedFuture);
   }

   @Test
   public void testCopy() {
      testCopy(CompletableFuture::new);
   }

   @Test
   public void testMultipleStagesCancelDownstream() throws InterruptedException {
      testMultipleStagesCancelDownstream(CompletableFuture::runAsync, false);
   }

   /**
    * Upstream cancellation is not supported by {@link CompletableFuture}
    */
   @Test
   public void testMultipleStagesCancelUpstream() throws InterruptedException {
      testMultipleStagesCancelUpstream(CompletableFuture::runAsync, false, false);
   }

   @Test
   public void testSingleStageCancel() throws InterruptedException {
      testSingleStageCancel(CompletableFuture::runAsync, false);
   }
}
