/*
 * Drogon : EventGetVersion.java
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

import org.joemonti.drogon.util.BytesUtil;

/**
 * 
 * 
 * @author Joseph Monti <joe.monti@gmail.com>
 * @version 1.0
 */
public class EventGetVersion implements DrogonEventData {
    public static final String EVENT_NAME = "drogon.getVersion";
    
    private long client;
    
    public EventGetVersion() { }
    
    public EventGetVersion( long client ) {
        this.client = client;
    }
    
    public long getClient() {
        return client;
    }
    
    /* (non-Javadoc)
     * @see org.joemonti.drogon.kernel.event.DrogonEventObject#serialize()
     */
    @Override
    public byte[] serialize() {
        byte[] bytes = new byte[ BytesUtil.SIZEOF_LONG ];
        BytesUtil.writeLong( bytes, 0, client );
        return bytes;
    }

    /* (non-Javadoc)
     * @see org.joemonti.drogon.kernel.event.DrogonEventObject#deserialize(byte[])
     */
    @Override
    public void deserialize( byte[] bytes ) {
        client = BytesUtil.readLong( bytes, 0 );
    }
}
