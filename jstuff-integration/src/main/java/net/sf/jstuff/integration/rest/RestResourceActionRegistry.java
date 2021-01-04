/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.rest;

import static net.sf.jstuff.core.collection.CollectionUtils.*;

import java.lang.reflect.Method;
import java.util.List;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.collection.MapWith;
import net.sf.jstuff.core.collection.MapWithLists;
import net.sf.jstuff.integration.spring.SpringBeanParanamer;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class RestResourceActionRegistry {
   private final MapWith<HttpRequestMethod, MapWithLists<String, RestResourceAction>> actions //
      = new MapWith<HttpRequestMethod, MapWithLists<String, RestResourceAction>>() {
         private static final long serialVersionUID = 1L;

         @Override
         protected MapWithLists<String, RestResourceAction> create(final HttpRequestMethod key) {
            return MapWithLists.create();
         }
      };

   public List<RestResourceAction> getAllResourceActions() {
      final List<RestResourceAction> coll = newArrayList();
      for (final MapWithLists<String, RestResourceAction> actionsByMethod : actions.values()) {
         for (final List<RestResourceAction> actionsByResourceId : actionsByMethod.values()) {
            coll.addAll(actionsByResourceId);
         }
      }
      return coll;
   }

   public RestResourceAction getResourceAction(final HttpRequestMethod requestMethod, final String requestParameters) {
      final MapWithLists<String, RestResourceAction> resourceActions = actions.getNullSafe(requestMethod);

      // going backwards to the requestParameters to find the best matching rest action
      String key = requestParameters + "/";
      List<RestResourceAction> matchingActions = null;
      do {
         key = Strings.substringBeforeLast(key, "/");

         if (resourceActions.containsKey(key)) {
            matchingActions = resourceActions.get(key);
            if (matchingActions.size() == 1)
               return matchingActions.get(0);
            final int paramCount = Strings.countMatches(requestParameters.substring(key.length()), "/");
            for (final RestResourceAction action : matchingActions)
               if (action.getRequiredURLParameterCount() == paramCount)
                  return action;
            return matchingActions.get(0);
         }
      }
      while (key.contains("/"));

      return null;
   }

   public RestResourceAction registerFallbackResourceAction(final String resourceId, final Method serviceMethod, final Object serviceImpl,
      final RestResourceAction primaryResourceAction) {
      // fallback method for DELETE is POST, for HEAD is GET
      final HttpRequestMethod reqMethod = primaryResourceAction.getHttpRequestMethod() == HttpRequestMethod.DELETE ? HttpRequestMethod.POST
         : HttpRequestMethod.GET;

      final RestResourceAction action = new RestResourceAction(resourceId, reqMethod, serviceMethod, SpringBeanParanamer.getParameterNames(serviceMethod,
         serviceImpl), primaryResourceAction);
      for (final RestResourceAction mappedAction : actions.getNullSafe(reqMethod).getNullSafe(resourceId))
         if (mappedAction.getRequiredURLParameterCount() == action.getRequiredURLParameterCount())
            throw new IllegalArgumentException("Another service method with the same number of parameters is mapped to the same resourceId+requestMethod: "
               + action + " <> " + mappedAction);
      actions.getOrCreate(reqMethod).add(resourceId, action);
      return action;
   }

   public RestResourceAction registerResourceAction(final String resourceId, final HttpRequestMethod reqMethod, final Method serviceMethod,
      final Object serviceImpl) {
      final RestResourceAction action = new RestResourceAction(resourceId, reqMethod, serviceMethod, SpringBeanParanamer.getParameterNames(serviceMethod,
         serviceImpl));
      for (final RestResourceAction mappedAction : actions.getNullSafe(reqMethod).getNullSafe(resourceId))
         if (mappedAction.getRequiredURLParameterCount() == action.getRequiredURLParameterCount())
            throw new IllegalArgumentException("Another service method with the same number of parameters is mapped to the same resourceId+requestMethod: "
               + action + " <> " + mappedAction);
      actions.getOrCreate(reqMethod).add(resourceId, action);
      return action;
   }
}
