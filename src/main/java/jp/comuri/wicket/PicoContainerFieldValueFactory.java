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

import java.lang.reflect.Field;

import org.apache.wicket.injection.IFieldValueFactory;
import org.apache.wicket.proxy.LazyInitProxyFactory;
import org.picocontainer.annotations.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PicoContainerFieldValueFactory implements IFieldValueFactory
{
    private static final Logger LOGGER = LoggerFactory.getLogger(PicoContainerFieldValueFactory.class);
    
    public Object getFieldValue(Field field, Object fieldOwner)
    {
        Class<?> type = field.getType();
        return LazyInitProxyFactory.createProxy(type, new PicoContainerProxyTargetLocator(type));
    }

    public boolean supportsField(Field field)
    {
        return field.getAnnotation(Inject.class) != null;
    }

}
