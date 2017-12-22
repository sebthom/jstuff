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

import java.lang.reflect.Field;
import java.util.Map;

import javax.xml.bind.annotation.XmlElementRefs;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JAnnotationValue;
import com.sun.codemodel.JFieldVar;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;

import net.sf.jstuff.core.reflection.Fields;

/**
 * JAXB 2.1 misses the 'required' attribute. This method removes the 'required' attribute if present in the code model
 *
 * <pre>
 * <code>
 * &#64;XmlElementRefs({
 *    &#64;XmlElementRef(name = "car", namespace = "my-config", type = JAXBElement.class, required = false),
 *    &#64;XmlElementRef(name = "bike", namespace = "my-config", type = JAXBElement.class, required = false)
 *  })
 * private List<JAXBElement<?>> carsAndBikes;
 * </code>
 * </pre>
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class MakeJAXB21CompatiblePlugin extends AbstractPlugin {

    private static final Field MEMBER_VALUES_FIELD = Fields.find(JAnnotationUse.class, "memberValues");

    public static final String OPTION_NAME = "Xjaxb21compat";

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }

    @Override
    public String getUsage() {
        return "  -" + OPTION_NAME + "    :  fixes the generate code to be compatible with JAXB2.1";
    }

    private void removeRequiredAttribute(final JFieldVar fieldDecl) {
        for (final JAnnotationUse a : fieldDecl.annotations()) {
            if (XmlElementRefs.class.getName().equals(a.getAnnotationClass().binaryName())) {
                for (final JAnnotationUse xmlElementRefAnno : ((JAnnotationArrayMember) a.getAnnotationMembers().get("value")).annotations()) {
                    final JAnnotationValue requiredAttribute = xmlElementRefAnno.getAnnotationMembers().get("required");
                    if (requiredAttribute != null) {
                        ((Map<?, ?>) Fields.read(xmlElementRefAnno, MEMBER_VALUES_FIELD)).remove("required");
                    }
                }
            }
        }
    }

    @Override
    public boolean run(final Outline outline, final Options options, final ErrorHandler errorHandler) throws SAXException {
        // iterate over all classes
        for (final ClassOutline classDef : outline.getClasses()) {
            // iterate over all fields
            for (final JFieldVar fieldDecl : classDef.implClass.fields().values()) {
                removeRequiredAttribute(fieldDecl);
            }
        }
        return true;
    }

}
