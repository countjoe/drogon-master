/*
 * Drogon : DrogonArduino.java
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

package org.joemonti.drogon.module.arduino;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;

import org.joemonti.drogon.kernel.event.DrogonEventCommand;
import org.joemonti.drogon.kernel.event.DrogonEventManager;
import org.joemonti.drogon.kernel.module.DrogonModule;

/**
 * 
 * 
 * @author Joseph Monti <joe.monti@gmail.com>
 * @version 1.0
 */
public class DrogonArduino implements DrogonModule {
    private static final String EVENT_CLIENT_NAME = "arduino";
    private static final String PORT_NAME = "/dev/ttyXYZ";
    
    private long eventClientId = 0;
    private DrogonEventManager eventManager = null;
    
    private SerialPort serialPort;
    private ArduinoReader arduinoReader;
    
    /* (non-Javadoc)
     * @see org.joemonti.drogon.kernel.module.DrogonModule#initialize()
     */
    @Override
    public void initialize() {
        eventManager = DrogonEventManager.getInstance( );
        eventClientId = eventManager.registerClient( EVENT_CLIENT_NAME );
        eventManager.registerEvent( eventClientId, DrogonEventCommand.ARDUINO_DATA_LOG, EventArduinoDataLog.class );
        
        try {
            int timeout = 2000;
            
            CommPortIdentifier portIdentifier = 
                    CommPortIdentifier.getPortIdentifier( PORT_NAME );
            
            serialPort = (SerialPort) portIdentifier.open( 
                    this.getClass().getName(), timeout );
            serialPort.setSerialPortParams( 57600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE );
            
            arduinoReader = new ArduinoReader( serialPort.getInputStream( ) );
        } catch ( NoSuchPortException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch ( PortInUseException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch ( UnsupportedCommOperationException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch ( IOException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see org.joemonti.drogon.kernel.module.DrogonModule#shutdown()
     */
    @Override
    public void shutdown() {
        if ( arduinoReader != null && arduinoReader.isAlive( ) ) {
            arduinoReader.interrupt( );
        }
        
        serialPort.close( );
    }
    
    class ArduinoReader extends Thread {
        private InputStream is;
        
        public ArduinoReader( InputStream is ) { 
            this.is = is;
        }
        
        @Override
        public void run() {
            // read data....
            
            if ( isInterrupted( ) ) {
                try {
                    is.close( );
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
            }
        }
    }
}
