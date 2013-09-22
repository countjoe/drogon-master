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

import java.io.IOException;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.joemonti.drogon.kernel.event.DrogonEvent;
import org.joemonti.drogon.kernel.event.DrogonEventCommand;
import org.joemonti.drogon.kernel.event.DrogonEventHandler;
import org.joemonti.drogon.modules.arduino.EventArduinoDataLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DrogonLoggerSocket extends WebSocketAdapter implements DrogonEventHandler {
    private static final Logger logger = LoggerFactory.getLogger( DrogonLoggerSocket.class );
    
    private WebLoggerModule webLoggerModule;
    private long eventClientId;
    
    public DrogonLoggerSocket( WebLoggerModule webLoggerModule ) {
        this.webLoggerModule = webLoggerModule;
    }
    
    @Override
    public void onWebSocketConnect(Session session) {
        super.onWebSocketConnect( session );
        
        this.eventClientId = webLoggerModule.subscribe( this );
        logger.debug("Client " + session.getRemoteAddress() + " Connected, event client " + eventClientId );
    }
    
    @Override
    public void onWebSocketError(Throwable cause) {
        cause.printStackTrace(System.err);
        webLoggerModule.unsubscribe( eventClientId );
    }
    
    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        logger.debug("Client " + getSession().getRemoteAddress() + " Disconnected [" + statusCode + "] : " + reason);
        webLoggerModule.unsubscribe( eventClientId );
        super.onWebSocketClose( statusCode, reason );
    }
    
    @Override
    public void handle( DrogonEvent event ) {
        if ( getSession( ) == null || !getSession( ).isOpen( ) ) {
            webLoggerModule.unsubscribe( eventClientId );
        }
        
        if ( event.getCommand( ) == DrogonEventCommand.ARDUINO_DATA_LOG ) {
            RemoteEndpoint remote = getSession().getRemote();
            try {
                remote.sendString( ((EventArduinoDataLog)event.getObject( )).getData( ) );
            } catch ( IOException ex ) {
                logger.error( "Error sending data log", ex );
            }
        }
    }
}
