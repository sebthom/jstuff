/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.profiler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.logging.Logger;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public final class CallTree {
   private static final Logger LOG = Logger.create();

   private static final int INDENT = 2;

   private long idleSeen;
   private long executingSeen;
   private final Map<CodeLocation, CallTree> children = new HashMap<>(32);

   CallTree markSeen(final String clazz, final String method, final int lineNumber, final boolean isExecuting) {
      final CodeLocation codeLocation = new CodeLocation(clazz, method, lineNumber);
      CallTree ct = children.get(codeLocation);
      if (ct == null) {
         ct = new CallTree();
         children.put(codeLocation, ct);
      }
      if (isExecuting) {
         ct.executingSeen++;
      } else {
         ct.idleSeen++;
      }
      return ct;
   }

   @Override
   public String toString() {
      final StringBuilder sb = new StringBuilder();
      try {
         toString(sb, -1, 0);
      } catch (final IOException ex) {
         LOG.debug(ex);
      }
      return sb.toString();
   }

   private void toString(final Appendable out, final CallTree tree, final int indent, final long percent, int maxDepth, final int minPercent)
      throws IOException {
      if (maxDepth == 0 || tree.children.isEmpty())
         return;

      maxDepth--;

      long totalSeen = 0;
      for (final CallTree ch : tree.children.values()) {
         totalSeen += ch.idleSeen;
         totalSeen += ch.executingSeen;
      }

      for (final Map.Entry<CodeLocation, CallTree> childEntry : tree.children.entrySet()) {
         final CallTree childTree = childEntry.getValue();
         final long childPercentage = (childTree.idleSeen + childTree.executingSeen) * percent / totalSeen;
         if (childPercentage > 0 && childPercentage > minPercent) {
            out.append(String.format("%3d%% | %3d%% | %3d%% | ", //
               childPercentage, //
               childTree.executingSeen * percent / totalSeen, //
               childTree.idleSeen * percent / totalSeen //
            ));
            for (int i = 0; i < indent; i++) {
               out.append(' ');
            }
            out.append(childEntry.getKey().toString());
            out.append(Strings.NEW_LINE);

            toString(out, childTree, indent + INDENT, childPercentage, maxDepth, minPercent);
         }
      }
   }

   public void toString(final Appendable out, final int maxDepth, final int minPercent) throws IOException {
      out.append("total| cpu  | wait | location").append(Strings.NEW_LINE);
      out.append("---------------------------------------").append(Strings.NEW_LINE);
      toString(out, this, 0, 100, maxDepth == 0 ? -1 : maxDepth, minPercent);
   }
}
