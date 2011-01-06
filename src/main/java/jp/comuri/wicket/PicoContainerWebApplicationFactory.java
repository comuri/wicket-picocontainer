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

import javax.servlet.ServletContext;

import org.apache.wicket.protocol.http.IWebApplicationFactory;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WicketFilter;
import org.picocontainer.PicoContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PicoContainerWebApplicationFactory implements
        IWebApplicationFactory
{
    private static final Logger LOGGER = LoggerFactory.getLogger(PicoContainerWebApplicationFactory.class);

    public WebApplication createApplication(WicketFilter filter)
    {
        PicoContainer container;

        String containerContextAttribute = filter.getFilterConfig().getInitParameter("containerContextAttribute");

        if (containerContextAttribute != null) {
            ServletContext sc = filter.getFilterConfig().getServletContext();

            container = (PicoContainer) sc.getAttribute(containerContextAttribute);
            if (container == null) {
                throw new RuntimeException("Could not find PicoContainer in the ServletContext under attribute: " + containerContextAttribute);
            }
        }
        else if (filter.getFilterConfig().getInitParameter("container") != null) {
            String paramValue = filter.getFilterConfig().getInitParameter("container");
            String containerName = paramValue.trim();

            try {
                Class<?> moduleClazz = Class.forName(containerName);
                container = (PicoContainer) moduleClazz.newInstance();
            }
            catch (InstantiationException e) {
                throw new RuntimeException("Could not create new instance of PicoContainer class " + containerName, e);
            }
            catch (ClassNotFoundException e) {
                throw new RuntimeException("Could not create new instance of PicoContainer class " + containerName, e);
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException("Could not create new instance of PicoContainer class " + containerName, e);
            }
        }
        else {
            throw new RuntimeException("To use PicoContainerWebApplicationFactory, you must specify either an 'containerContextAttribute' or a 'container' init-param.");
        }

        WebApplication app = container.getComponent(WebApplication.class);
        app.addComponentInstantiationListener(new PicoContainerComponentInjector(app,
                                                                                 container));
        return app;
    }

}
