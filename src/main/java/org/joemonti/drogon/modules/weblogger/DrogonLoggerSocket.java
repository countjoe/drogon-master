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
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DrogonLoggerSocket extends WebSocketAdapter {
    private static final Logger logger = LoggerFactory.getLogger( DrogonLoggerSocket.class );
    
    private Thread t;
    
    @Override
    public void onWebSocketConnect(Session session) {
        super.onWebSocketConnect( session );
        
        logger.debug("Client " + session.getRemoteAddress() + " Connected");
        t = new Thread( new DrogonLoggerRunner() );
        t.start();
    }
    
    @Override
    public void onWebSocketError(Throwable cause) {
        cause.printStackTrace(System.err);
    }
    
    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        logger.debug("Client " + getSession().getRemoteAddress() + " Disconnected [" + statusCode + "] : " + reason);
        super.onWebSocketClose( statusCode, reason );
    }
    
    class DrogonLoggerRunner implements Runnable {
        public void run() {
            RemoteEndpoint remote = getSession().getRemote();
            
            final int VALUES = 10;
            ValueTracker values[] = new ValueTracker[VALUES];
            for ( int i = 0; i < VALUES; i++ ) {
                values[i] = new ValueTracker();
            }
             
            while ( isConnected() ) {
                StringBuilder sb = new StringBuilder();
                for ( int i = 0; i < VALUES; i++ ) {
                    values[i].update();
                    if ( i > 0 ) sb.append(",");
                    sb.append( String.format("%.4f", values[i].value) );
                }
                
                try {
                    remote.sendString( sb.toString() );
                } catch ( IOException ex ) {
                    ex.printStackTrace( System.err );
                    return;
                }
                
                try {
                    Thread.sleep(100);
                } catch ( InterruptedException ex ) {
                    logger.error("Interrupted!!\n");
                    return;
                }
            }
        }
    }
    
    static class ValueTracker {
        private double value = 0.0;
        double lastValue = 0.0;
        public void update() {
            double inc = Math.random() * 10;
            if ( value < lastValue ) {
                inc *= -1;
            }
            if ( ( ( ( value < lastValue ) && value < 0 ) || ( ( value > lastValue ) && value > 0 ) ) && 
                    Math.random() <= Math.abs( ( value + inc ) / 100.0 ) ) {
                inc *= -1;
            }
            lastValue = value;
            value += inc;
        }
    }
}
