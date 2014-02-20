/**
 * 
 */
package org.joemonti.drogon.kernel.event;

/**
 * @author joe
 *
 */
public class DrogonEventSerializationException extends Exception {
    private static final long serialVersionUID = 2760138030206889170L;

    public DrogonEventSerializationException() { super(); }
    public DrogonEventSerializationException( String msg ) { super(msg); }
    public DrogonEventSerializationException( Throwable t ) { super(t); }
    public DrogonEventSerializationException( String msg, Throwable t ) { super( msg, t ); }
}
