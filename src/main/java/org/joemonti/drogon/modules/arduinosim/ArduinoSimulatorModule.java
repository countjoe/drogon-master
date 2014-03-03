/*
 * Drogon : ArduinoSimlatorModule.java
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
package org.joemonti.drogon.modules.arduinosim;

import org.joemonti.drogon.kernel.event.DrogonEvent;
import org.joemonti.drogon.kernel.event.DrogonEventData;
import org.joemonti.drogon.kernel.event.DrogonEventManager;
import org.joemonti.drogon.kernel.module.DrogonModule;
import org.joemonti.drogon.modules.arduino.EventArduinoDataLog;
import org.joemonti.drogon.modules.arduino.EventArduinoMessage;

/**
 * @author joe
 *
 */
public class ArduinoSimulatorModule implements DrogonModule {
    private static final long SIM_INTERVAL = 250;
    
    private long eventClientId = 0;
    private DrogonEventManager eventManager = null;
    
    private Thread simThread;
    
    private static final String EVENT_CLIENT_NAME = "arduinosim";
    
    /* (non-Javadoc)
     * @see org.joemonti.drogon.kernel.module.DrogonModule#initialize()
     */
    @Override
    public void initialize() {
        eventManager = DrogonEventManager.getInstance( );
        
        eventClientId = eventManager.registerClient( EVENT_CLIENT_NAME );
        eventManager.registerEvent( eventClientId, EventArduinoMessage.EVENT_NAME, EventArduinoMessage.class );
        eventManager.registerEvent( eventClientId, EventArduinoDataLog.EVENT_NAME, EventArduinoDataLog.class );
    
        simThread = new Thread(new SimRunner( ));
        simThread.start( );
    }

    /* (non-Javadoc)
     * @see org.joemonti.drogon.kernel.module.DrogonModule#shutdown()
     */
    @Override
    public void shutdown() {
        simThread.interrupt( );
    }
    
    class SimRunner implements Runnable {
        @Override
        public void run() {
            ValueTracker[] accel = ValueTracker.init(3);
            ValueTracker[] gyro = ValueTracker.init(3);
            ValueTracker[] motor = ValueTracker.init(4);
            ValueTracker[] position = ValueTracker.init(2);
            
            while ( true ) {
                try {
                    Thread.sleep( SIM_INTERVAL );
                } catch ( InterruptedException ex ) {
                    return;
                }
                
                ValueTracker.update( accel );
                ValueTracker.update( gyro );
                ValueTracker.update( motor );
                ValueTracker.update( position );
                
                StringBuilder dataBuilder = new StringBuilder( );
                dataBuilder.append( System.currentTimeMillis( ) );
                dataBuilder.append( '\t' );
                dataBuilder.append( 0 );
                dataBuilder.append( '\t' );
                dataBuilder.append( accel[0] );
                dataBuilder.append( '\t' );
                dataBuilder.append( accel[1] );
                dataBuilder.append( '\t' );
                dataBuilder.append( accel[2] );
                dataBuilder.append( '\t' );
                dataBuilder.append( gyro[0] );
                dataBuilder.append( '\t' );
                dataBuilder.append( gyro[1] );
                dataBuilder.append( '\t' );
                dataBuilder.append( gyro[2] );
                dataBuilder.append( '\t' );
                dataBuilder.append( motor[0] );
                dataBuilder.append( '\t' );
                dataBuilder.append( motor[1] );
                dataBuilder.append( '\t' );
                dataBuilder.append( motor[2] );
                dataBuilder.append( '\t' );
                dataBuilder.append( motor[3] );
                dataBuilder.append( '\t' );
                dataBuilder.append( position[0] );
                dataBuilder.append( '\t' );
                dataBuilder.append( position[1] );
                
                String data = dataBuilder.toString( );
                
                DrogonEventData object = new EventArduinoDataLog( data );
                
                DrogonEvent event = new DrogonEvent( 
                        eventClientId, 
                        EventArduinoDataLog.EVENT_NAME, 
                        object );
                
                eventManager.send( event );
            }
        }
        
    }
    
    static class ValueTracker {
        double value;
        double rate;
        int direction;
        
        ValueTracker( ) {
            value = 0.0;
            rate = 10;
            direction = 1;
        }
        
        void update() {
            if ( Math.random() <= ( value/(100.0*direction) ) ) {
                direction *= -1;
                rate = ( Math.random() * 10 ) + 2;
            } else {
                value += ( rate * direction );
            }
        }
        
        @Override
        public String toString() {
            return Double.toString( value );
        }

        static ValueTracker[] init(int c) {
            ValueTracker[] vals = new ValueTracker[c];
            
            for ( int i = 0; i < c; i++ ) {
                vals[i] = new ValueTracker( );
            }
            
            return vals;
        }
        
        static void update( ValueTracker[] vals ) {
            for ( int i = 0; i < vals.length; i++ ) {
                vals[i].update( );
            }
        }
    }
}
