/*
 * Drogon : EventTest.java
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

import junit.framework.TestCase;

import org.joemonti.drogon.kernel.event.DrogonEvent;
import org.joemonti.drogon.kernel.event.DrogonEventHandler;
import org.joemonti.drogon.kernel.event.DrogonEventManager;
import org.joemonti.drogon.modules.arduino.EventArduinoMessage;


/**
 * 
 * 
 * @author Joseph Monti <joe.monti@gmail.com>
 * @version 1.0
 */
public class EventTest extends TestCase {
    public void testEvents() throws InterruptedException {
        DrogonEventManager eventManager = DrogonEventManager.getInstance( );
        
        long eventClientId = eventManager.registerClient( "test-client" );
        eventManager.registerEvent( eventClientId, EventArduinoMessage.EVENT_NAME, EventArduinoMessage.class );
        
        TestEventHandler testHander = new TestEventHandler( );
        
        long handlerClientId = eventManager.registerClient( "handler-client" );
        
        eventManager.subscribe( handlerClientId, EventArduinoMessage.EVENT_NAME, testHander );
        
        String expected = "hello world";
        EventArduinoMessage object = new EventArduinoMessage( expected );
        DrogonEvent event = new DrogonEvent( eventClientId, EventArduinoMessage.EVENT_NAME, object );
        
        eventManager.send( event );
        
        for ( int i = 0; testHander.msg == null && i < 100; i++ ) {
            Thread.sleep(100);
        }
        
        assertEquals( "Got Message", expected, testHander.msg );
    }
    
    static class TestEventHandler implements DrogonEventHandler {
        String msg = null;
        
        @Override
        public void handle( DrogonEvent event ) {
            msg = ((EventArduinoMessage)event.getData( )).getMessage( );
        }
    }
}
