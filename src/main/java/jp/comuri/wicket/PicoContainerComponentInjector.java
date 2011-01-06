/*
 *  Copyright 2009
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package jp.comuri.wicket;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.injection.ComponentInjector;
import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.proxy.LazyInitProxyFactory;
import org.picocontainer.PicoContainer;
import org.picocontainer.annotations.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author k2
 *
 */
public class PicoContainerComponentInjector extends ComponentInjector
{
    private static final Logger LOGGER = LoggerFactory.getLogger(PicoContainerComponentInjector.class);
    
    public static final MetaDataKey<PicoContainer> PICO_CONTAINER_KEY = new MetaDataKey<PicoContainer>() {
        private static final long serialVersionUID = 1L;
    };

    public PicoContainerComponentInjector(Application app,
                                          PicoContainer container)
    {
        app.setMetaData(PICO_CONTAINER_KEY, container);
        InjectorHolder.setInjector(new PicoContainerInjector());
    }

    @Override
    public void onInstantiation(Component component)
    {
        inject(component);
        super.onInstantiation(component);
    }

    public void inject(Object object)
    {
        Class<?> current = object.getClass();

        do {
            Method[] methods = current.getDeclaredMethods();
            for (Method method : methods) {
                if (Modifier.isStatic(method.getModifiers())
                    || method.getAnnotation(Inject.class) == null)
                    continue;

                Class<?>[] paramTypes = method.getParameterTypes();
                Object[] params = new Object[paramTypes.length];
                for (int i = 0, n = paramTypes.length; i < n; i++) {
                    params[i] = LazyInitProxyFactory.createProxy(paramTypes[i],
                                                                 new PicoContainerProxyTargetLocator(paramTypes[i]));
                }

                try {
                    method.invoke(object, params);
                }
                catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }

            current = current.getSuperclass();
        }
        while (current != null && current != Object.class);
    }
}
