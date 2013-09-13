/*
 * Drogon : WebLoggerModule.java
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

import org.joemonti.drogon.kernel.event.DrogonEvent;
import org.joemonti.drogon.kernel.event.DrogonEventCommand;
import org.joemonti.drogon.kernel.event.DrogonEventHandler;
import org.joemonti.drogon.kernel.event.DrogonEventManager;
import org.joemonti.drogon.kernel.module.DrogonModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * @author Joseph Monti <joe.monti@gmail.com>
 * @version 1.0
 */
public class WebLoggerModule implements DrogonModule, DrogonEventHandler {
    private static final Logger logger = LoggerFactory.getLogger( WebLoggerModule.class );
    
    private static final String EVENT_CLIENT_NAME = "web-logger";
    
    private long eventClientId = 0;
    private DrogonEventManager eventManager = null;
    
    /* (non-Javadoc)
     * @see org.joemonti.drogon.kernel.module.DrogonModule#initialize()
     */
    @Override
    public void initialize() {
        eventManager = DrogonEventManager.getInstance( );
        eventClientId = eventManager.registerClient( EVENT_CLIENT_NAME );
        
        eventManager.subscribe( eventClientId, DrogonEventCommand.ARDUINO_DATA_LOG, this );
    }

    /* (non-Javadoc)
     * @see org.joemonti.drogon.kernel.module.DrogonModule#shutdown()
     */
    @Override
    public void shutdown() {
        // TODO Auto-generated method stub

    }

    @Override
    public void handle( DrogonEvent event ) {
    }
}
