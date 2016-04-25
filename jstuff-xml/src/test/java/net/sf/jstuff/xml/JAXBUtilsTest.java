/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2016 Sebastian
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
package net.sf.jstuff.xml;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class JAXBUtilsTest extends TestCase {

    public static class MyEntity {
        private MyEntity child;
        private String name;

        public MyEntity getChild() {
            return child;
        }

        public String getName() {
            return name;
        }

        public void setChild(final MyEntity child) {
            this.child = child;
        }

        public void setName(final String name) {
            this.name = name;
        }
    }

    public void testToXML() {
        final MyEntity e = new MyEntity();
        e.name = "a";
        e.child = new MyEntity();
        e.child.name = "b";
        System.out.println(JAXBUtils.toXML(e));
    }

    public void testToXSD() {
        System.out.println(JAXBUtils.toXSD(MyEntity.class));
    }
}
