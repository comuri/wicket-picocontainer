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

import org.apache.wicket.Application;
import org.apache.wicket.proxy.IProxyTargetLocator;
import org.picocontainer.PicoContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PicoContainerProxyTargetLocator implements IProxyTargetLocator
{
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(PicoContainerProxyTargetLocator.class);
    
    private final Class<?> targetType;
    
    public PicoContainerProxyTargetLocator(Class<?> targetType)
    {
        this.targetType = targetType;
    }

    public Object locateProxyTarget()
    {
        PicoContainer pico = Application.get().getMetaData(PicoContainerComponentInjector.PICO_CONTAINER_KEY);
        return pico.getComponent(targetType);
    }
}
