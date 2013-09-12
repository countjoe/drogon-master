/*
 * Drogon : DrogonEventCommand.java
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

package org.joemonti.drogon.kernel.event;

/**
 * 
 * 
 * @author Joseph Monti <joe.monti@gmail.com>
 * @version 1.0
 */
public enum DrogonEventCommand {
    GET_VERSION( 1 ),
    
    /* ARDUINO EVENTS */
    ARDUINO_DEFAULTS( 2 ),
    ARDUINO_GET_DEFAULTS( 2 ),
    ARDUINO_DATA_LOG( 3 ),
    
    /* VIDEO EVENTS */
    VIDEO_FRAME( 4 );
    
    public final int command;
    private DrogonEventCommand( int command ) {
        this.command = command;
    }
}
