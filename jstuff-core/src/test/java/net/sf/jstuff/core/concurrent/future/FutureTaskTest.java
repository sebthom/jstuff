/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent.future;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.junit.Test;

/**
 * Tests the cancellation behaviour of {@link FutureTask}.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class FutureTaskTest extends AbstractFutureTest {

   final ExecutorService executor = Executors.newSingleThreadExecutor();

   @Test
   public void testSingleStageCancel() throws InterruptedException {
      testSingleStageCancel(executor::submit, true);
   }
}
