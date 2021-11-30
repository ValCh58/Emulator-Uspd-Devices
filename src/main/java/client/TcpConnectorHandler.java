package client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;


public class TcpConnectorHandler extends IoHandlerAdapter {
	
	private final static Logger logger = Logger.getLogger(TcpConnectorHandler.class);
	
	/**The purpose of handlers is to handle multithreaded parallelism using a handler,
         but for every connection there is ReadFuture*/
	protected Map<IoSession, ReadFuture> handlers = new ConcurrentHashMap<IoSession, ReadFuture>();

	
	public TcpConnectorHandler() {}
	
	public void sessionCreated(IoSession session) throws Exception {
		logger.info("TcpConnector sessionCreated:" + session.getId());
	}

    public void sessionOpened(IoSession session) {
        logger.info("TcpConnector sessionOpened:" + session.getId());
    }

    // After the exception is caught, the current future thread is automatically executed.
    public void exceptionCaught(IoSession session, Throwable t){
    	logger.error("TcpConnector exceptionCaught:" + session.getId(), t);
    	ReadFuture future = getReadFuture(session);
    	if (future != null)
    		future.setException(new Exception(t));
        session.closeNow();
    }

    public void sessionClosed(IoSession session) {
        logger.info("TcpConnector sessionClosed:" + session.getId() +" total " + session.getReadBytes() + " byte(s)");
        ReadFuture future = null;
    	// The remote host may have closed the connection, but the client socket did not receive data, 
        //so an exception needs to be thrown here
    	future = getReadFuture(session);
    	if (future != null) {
    		//logger.info("Remote TCP server closed connection");	
    		future.setException(new Exception("Remote TCP server closed connection"));
    	}
    }

    public void sessionIdle(IoSession session, IdleStatus status) {
		logger.debug("TcpConnector sessionIdle:" + session.getId() +" the status is " + status + ". ");
    }

    public void messageSent(IoSession session, Object message) throws Exception {
    	if (logger.isInfoEnabled())
			logger.info("TcpConnector SENT: " + new String((byte[]) message));
    }

    public void messageReceived(IoSession session, Object message) {
    	// Logs are placed in TcpConnector to avoid, 
        //which access thread the log belongs to because the connector uses a thread pool.
        if (logger.isInfoEnabled())
    		logger.info("TcpConnector RECEIVED: " + new String((byte[]) message));
    //	logger.debug("TcpConnector RECEIVED:" + session.getId());
    	ReadFuture future = getReadFuture(session);
    	if (future != null)
    		future.setRead(message);
    }
    
    protected ReadFuture getReadFuture(IoSession s) {
    	return handlers.get(s);
    }
    
    public void addReadFuture(IoSession s, ReadFuture f) {
    	handlers.put(s, f);
    }
    
    public void removeReadFuture(IoSession s) {
    	handlers.remove(s);
    }
    
}
