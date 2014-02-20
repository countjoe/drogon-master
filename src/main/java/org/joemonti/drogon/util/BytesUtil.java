/*
 * Drogon : BytesUtil.java
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

package org.joemonti.drogon.util;

import java.io.UnsupportedEncodingException;

/**
 * 
 * 
 * @author Joseph Monti <joe.monti@gmail.com>
 * @version 1.0
 */
public class BytesUtil {
    public static final String UTF8_ENCODING = "UTF-8";
    
    public static final int SIZEOF_LONG = Long.SIZE / Byte.SIZE;
    public static final int SIZEOF_INT = Integer.SIZE / Byte.SIZE;
    public static final int SIZEOF_SHORT = Short.SIZE / Byte.SIZE;
    
    public static long readLong( byte[] bytes, int offset ) {
        if ( offset < 0 || ( offset + SIZEOF_LONG ) > bytes.length ) {
            throw new IllegalArgumentException( "Invalid offset " + offset + " for bytes length " + bytes.length );
        }
        long value = 0l;
        for ( int i = offset; i < ( offset + SIZEOF_LONG ); i++ ) {
            value <<= 8;
            value ^= bytes[i] & 0xFF;
        }
        return value;
    }
    
    public static int writeLong( byte[] bytes, int offset, long value ) {
        if ( offset < 0 || ( offset + SIZEOF_LONG ) > bytes.length ) {
            throw new IllegalArgumentException( "Invalid offset " + offset + " for bytes length " + bytes.length );
        }
        for ( int i = offset + ( SIZEOF_LONG - 1 ); i > offset; i-- ) {
            bytes[i] = (byte) value;
            value >>>= 8;
        }
        bytes[offset] = (byte) value;
        return offset + SIZEOF_LONG;
    }
    
    public static int readInt( byte[] bytes, int offset ) {
        if ( offset < 0 || ( offset + SIZEOF_INT ) > bytes.length ) {
            throw new IllegalArgumentException( "Invalid offset " + offset + " for bytes length " + bytes.length );
        }
        int value = 0;
        for ( int i = offset; i < ( offset + SIZEOF_INT ); i++ ) {
            value <<= 8;
            value ^= bytes[i] & 0xFF;
        }
        return value;
    }
    
    public static int writeInt( byte[] bytes, int offset, int value ) {
        if ( offset < 0 || ( offset + SIZEOF_INT ) > bytes.length ) {
            throw new IllegalArgumentException( "Invalid offset " + offset + " for bytes length " + bytes.length );
        }
        for ( int i = offset + ( SIZEOF_INT - 1 ); i > offset; i-- ) {
            bytes[i] = (byte) value;
            value >>>= 8;
        }
        bytes[offset] = (byte) value;
        return offset + SIZEOF_INT;
    }
    
    public static short readShort( byte[] bytes, int offset ) {
        if ( offset < 0 || ( offset + SIZEOF_SHORT ) > bytes.length ) {
            throw new IllegalArgumentException( "Invalid offset " + offset + " for bytes length " + bytes.length );
        }
        short value = 0;
        for ( int i = offset; i < ( offset + SIZEOF_INT ); i++ ) {
            value <<= 8;
            value ^= bytes[i] & 0xFF;
        }
        return value;
    }
    
    public static int writeShort( byte[] bytes, int offset, short value ) {
        if ( offset < 0 || ( offset + SIZEOF_SHORT ) > bytes.length ) {
            throw new IllegalArgumentException( "Invalid offset " + offset + " for bytes length " + bytes.length );
        }
        for ( int i = offset + ( SIZEOF_SHORT - 1 ); i > offset; i-- ) {
            bytes[i] = (byte) value;
            value >>>= 8;
        }
        bytes[offset] = (byte) value;
        return offset + SIZEOF_SHORT;
    }
    
    public static void readBytes( byte[] bytes, int offset, byte[] dst, int dstOffset, int length ) {
        System.arraycopy( bytes, offset, dst, dstOffset, length );
    }
    
    public static int writeBytes( byte[] bytes, int offset, byte[] value, int valueOffset, int length ) {
        System.arraycopy( value, valueOffset, bytes, offset, length );
        return offset + length;
    }
    
    public static String readString( byte[] bytes, int offset, int length ) {
        if ( bytes == null ) return null;
        if ( length == 0 ) return "";
        
        try {
            return new String( bytes, offset, length, UTF8_ENCODING );
        } catch ( UnsupportedEncodingException ex ) {
            throw new IllegalArgumentException( "Unable to encode " + UTF8_ENCODING, ex );
        }
    }
    
    public static int writeString( byte[] bytes, int offset, String value ) {
        byte[] valueBytes;
        
        try {
            valueBytes = value.getBytes( UTF8_ENCODING );
        } catch ( UnsupportedEncodingException ex ) {
            throw new IllegalArgumentException( "Unable to encode " + UTF8_ENCODING, ex );
        }
        
        return writeBytes( bytes, offset, valueBytes, 0, valueBytes.length );
    }
}
