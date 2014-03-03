/*
 * Drogon : DrogonArduinoModule.java
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

package org.joemonti.drogon.modules.arduino;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.joemonti.drogon.kernel.event.DrogonEvent;
import org.joemonti.drogon.kernel.event.DrogonEventData;
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
public class DrogonArduinoModule implements DrogonModule {
    private static final Logger logger = LoggerFactory.getLogger( DrogonArduinoModule.class );
    
    private static final String EVENT_CLIENT_NAME = "arduino";
    private static final String PORT_NAME = "/dev/ttyACM0";
    private static final int BAUD_RATE = 9600;
    
    private static final char ARDUINO_COMMAND_DEBUG = 'D';
    private static final char ARDUINO_COMMAND_LOG = 'L';
    private static final char ARDUINO_COMMAND_ARM = 'A';
    private static final char ARDUINO_COMMAND_MOTORS = 'M';
    
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
        eventManager.registerEvent( eventClientId, EventArduinoMessage.EVENT_NAME, EventArduinoMessage.class );
        eventManager.registerEvent( eventClientId, EventArduinoDataLog.EVENT_NAME, EventArduinoDataLog.class );
        
        try {
            int timeout = 2000;
            
            CommPortIdentifier portIdentifier = 
                    CommPortIdentifier.getPortIdentifier( PORT_NAME );
            
            serialPort = (SerialPort) portIdentifier.open( 
                    this.getClass().getName(), timeout );
            serialPort.setSerialPortParams( BAUD_RATE,
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
        
        if ( serialPort != null ) {
            serialPort.close( );
        }
    }
    
    public void setMotors( double value )
    {
        
    }
    
    public void arm( boolean armed )
    {
        
    }
    
    class ArduinoReader extends Thread {
        private InputStream is;
        
        public ArduinoReader( InputStream is ) { 
            this.is = is;
        }
        
        @Override
        public void run() {
            // read data....
            BufferedReader reader = new BufferedReader( new InputStreamReader( is ) );
            
            String line;
            try {
                while ( ( line = reader.readLine( ) ) != null && !isInterrupted( )  ) {
                    logger.debug( "ARDUINO LINE: " + line );
                    
                    parseLine( line );
                }
            } catch ( IOException ex ) {
                logger.error( "Error reading from Arduino", ex );
            }
            
            try {
                is.close( );
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }
        
        private void parseLine( String line ) {
            if ( line.length( ) < 3 ) return;
            
            char firstChar = line.charAt( 0 );
            
            if ( line.charAt( 1 ) != '\t' ) return;
            
            DrogonEventData object = null;
            String command = null;
            
            switch ( firstChar )
            {
            case ARDUINO_COMMAND_DEBUG:
                object = new EventArduinoMessage( line.substring( 2 ) );
                command = EventArduinoMessage.EVENT_NAME;
                break;
            case ARDUINO_COMMAND_LOG:
                object = new EventArduinoDataLog( line.substring( 2 ) );
                command = EventArduinoDataLog.EVENT_NAME;
                break;
            default:
                logger.warn( "Command not supported: " + line );
            }
            
            DrogonEvent event = new DrogonEvent( eventClientId, command, object );
            eventManager.send( event );
        }
    }
}
