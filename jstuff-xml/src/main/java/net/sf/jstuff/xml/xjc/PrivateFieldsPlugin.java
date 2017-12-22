/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
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
package net.sf.jstuff.xml.xjc;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMod;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;

import net.sf.jstuff.core.logging.Logger;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class PrivateFieldsPlugin extends AbstractPlugin {

    private static final Logger LOG = Logger.create();

    public static final String OPTION_NAME = "Xprivate-fields";

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }

    @Override
    public String getUsage() {
        return "  -" + OPTION_NAME + "    : Changes the field visibility from protected to private";
    }

    @Override
    public boolean run(final Outline model, final Options options, final ErrorHandler errorHandler) throws SAXException {

        // scan all XSD classes
        for (final ClassOutline classDef : model.getClasses()) {

            for (final JFieldVar fieldDef : classDef.implClass.fields().values()) {
                // ignore static fields
                if ((fieldDef.mods().getValue() & JMod.STATIC) != 0) {
                    continue;
                }

                // ignore private fields
                if ((fieldDef.mods().getValue() & JMod.PRIVATE) != 0) {
                    continue;
                }

                LOG.info("Declaring [%s#%s] private.", classDef.implClass.name(), fieldDef.name());
                fieldDef.mods().setPrivate();
            }
        }
        return true;
    }

}