/*
 * Drogon : EventDataLog.java
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

import org.joemonti.drogon.kernel.event.DrogonEventData;

/**
 * 
 * 
 * @author Joseph Monti <joe.monti@gmail.com>
 * @version 1.0
 */
public class EventArduinoDataLog implements DrogonEventData {
    public static final String EVENT_NAME = "arduino.data";
    
    private String data;
    
    private int timestamp;
    private int lastRunDuration;
    private double[] accelerometer;
    private double[] gyroscope;
    private double[] motorAdjust;
    private double[] rotation;
    private double[] pidError;
    
    public EventArduinoDataLog() { }
    
    public EventArduinoDataLog( String data ) { 
        this.setData( data );
    }
    
    private void setData( String data ) {
        String[] parts = data.split("\t");
        
        timestamp = Integer.parseInt( parts[0] );
        lastRunDuration = Integer.parseInt( parts[1] );
        
        accelerometer = parseDoubleArray( parts, 2, 3 );
        gyroscope = parseDoubleArray( parts, 5, 3 );
        motorAdjust = parseDoubleArray( parts, 8, 4 );
        rotation = parseDoubleArray( parts, 12, 2 );
        pidError = parseDoubleArray( parts, 14, 2 );
        
        this.data = data;
    }
    
    public String getData() { 
        return this.data;
    }
    
    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp( int timestamp ) {
        this.timestamp = timestamp;
    }

    public int getLastRunDuration() {
        return lastRunDuration;
    }

    public void setLastRunDuration( int lastRunDuration ) {
        this.lastRunDuration = lastRunDuration;
    }

    public double[] getAccelerometer() {
        return accelerometer;
    }

    public void setAccelerometer( double[] accelerometer ) {
        this.accelerometer = accelerometer;
    }

    public double[] getGyroscope() {
        return gyroscope;
    }

    public void setGyroscope( double[] gyroscope ) {
        this.gyroscope = gyroscope;
    }

    public double[] getMotorAdjust() {
        return motorAdjust;
    }

    public void setMotorAdjust( double[] motorAdjust ) {
        this.motorAdjust = motorAdjust;
    }

    public double[] getRotation() {
        return rotation;
    }

    public void setRotation( double[] rotation ) {
        this.rotation = rotation;
    }

    public double[] getPidError() {
        return pidError;
    }

    public void setPidError( double[] pidError ) {
        this.pidError = pidError;
    }
    
    @Override
    public byte[] serialize() {
        return data.getBytes( );
    }
    
    @Override
    public void deserialize( byte[] bytes ) {
        this.setData( new String( bytes ) );
    }
    
    private double[] parseDoubleArray( String[] parts, int start, int length ) {
        double[] d = new double[length];
        for ( int i = 0; i < length; i++ ) {
            d[i] = Double.parseDouble( parts[start+i] );
        }
        return d;
    }
}
