package org.joemonti.drogon.kernel.event;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

public abstract class DrogonEventProtobufData<T extends Message> implements DrogonEventData {
    private boolean built = false;
    private T data;
    private byte[] bytes;
    
    public DrogonEventProtobufData( ) {
        built = false;
    }
    
    public DrogonEventProtobufData( T data ) {
        this.data = data;
    }
    
    public boolean isBuilt() {
        return built;
    }

    public T getData() {
        return data;
    }

    public byte[] getBytes() {
        return bytes;
    }
    
    public abstract T build( byte[] bytes ) throws InvalidProtocolBufferException;
    
    @Override
    public byte[] serialize() {
        return data.toByteArray( );
    }

    @Override
    public void deserialize( byte[] bytes ) throws DrogonEventSerializationException {
        try {
            this.data = build( bytes );
        } catch ( InvalidProtocolBufferException ex ) {
            throw new DrogonEventSerializationException( ex );
        }
    }
}
