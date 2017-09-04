/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.core.profiler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.sf.jstuff.core.Strings;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public final class CallTree {
    private static final int INDENT = 2;

    private long idleSeen = 0;
    private long executingSeen = 0;
    private final Map<CodeLocation, CallTree> children = new HashMap<CodeLocation, CallTree>(32);

    protected CallTree markSeen(final String clazz, final String method, final int lineNumber, final boolean isExecuting) {
        final CodeLocation codeLocation = new CodeLocation(clazz, method, lineNumber);
        CallTree ct = children.get(codeLocation);
        if (ct == null) {
            children.put(codeLocation, ct = new CallTree());
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
        } catch (final IOException ex) {}
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