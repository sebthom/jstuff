/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.spring;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;

import net.sf.jstuff.core.Strings;

/**
 * Bean names generated by {@link AnnotationBeanNameGenerator} starting with "Default" are
 * returned without this prefix. e.g. <code>defaultMailService</code> becomes <code>mailService</code>.
 *
 * Bean names generated by {@link AnnotationBeanNameGenerator} ending with "Impl" are
 * returned without this suffix. e.g. <code>mailServiceImpl</code> becomes <code>mailService</code>.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class AffixesRemovingBeanNameGenerator extends AnnotationBeanNameGenerator {
   @Override
   public String generateBeanName(final BeanDefinition definition, final BeanDefinitionRegistry registry) {
      final String beanName = super.generateBeanName(definition, registry);
      if (beanName.startsWith("Default"))
         return Strings.substringAfter(beanName, "Default");
      return Strings.substringBeforeLast(beanName, "Impl");
   }
}
