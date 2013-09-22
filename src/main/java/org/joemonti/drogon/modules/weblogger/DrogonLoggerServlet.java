/*
 * Drogon : DrogonLoggerServlet.java
 * 
 * This file is part of Drogon.
 *
 * Drogon is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Drogon is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Drogon.  If not, see <http://www.gnu.org/licenses/>. 
 *
 * Author: Joseph Monti <joe.monti@gmail.com>
 * Copyright (c) 2013 Joseph Monti All Rights Reserved, http://joemonti.org/
 */

package org.joemonti.drogon.modules.weblogger;

import org.eclipse.jetty.websocket.api.UpgradeRequest;
import org.eclipse.jetty.websocket.api.UpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class DrogonLoggerServlet extends WebSocketServlet {
    private static final long serialVersionUID = 6952823967401149476L;
    
    private WebLoggerModule webLoggerModule;
    
    public void setWebLoggerModule( WebLoggerModule webLoggerModule ) {
        this.webLoggerModule = webLoggerModule;
    }
    
    public WebLoggerModule getWebLoggerModule( ) {
        return webLoggerModule;
    }
    
    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.getPolicy().setIdleTimeout(10000);
        factory.setCreator(new WebSocketCreator() {
                @Override
                public Object createWebSocket(UpgradeRequest req, UpgradeResponse resp) {
                    return new DrogonLoggerSocket( webLoggerModule );
                }
            });
    }
}
