/*
 * Drogon : DrogonEvent.java
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
public class DrogonEvent {
    //private static final int SIZEOF_LONG = Long.SIZE / Byte.SIZE;
    //private static final int SIZEOF_INT = Integer.SIZE / Byte.SIZE;
    
    private final long source;
    private final DrogonEventCommand command;
    private final DrogonEventObject object;
    
    public DrogonEvent( long source, DrogonEventCommand command, DrogonEventObject object ) {
        this.source = source;
        this.command = command;
        this.object = object;
    }
    
    public DrogonEvent( byte[] bytes ) {
        // TODO
        
        this.source = 0;
        this.command = null;
        this.object = null;
    }

    public long getSource() {
        return source;
    }

    public DrogonEventCommand getCommand() {
        return command;
    }

    public DrogonEventObject getObject() {
        return object;
    }
    
    public byte[] serialize() {
        // TODO
        
        return null;
    }
}
