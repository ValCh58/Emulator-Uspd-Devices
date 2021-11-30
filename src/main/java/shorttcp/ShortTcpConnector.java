package shorttcp;

import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;

import client.RecvPacketRuleCfg;
import client.TcpConnectorHandler;
import client.TcpConnector;

public class ShortTcpConnector extends TcpConnector {

	public final static Logger logger = Logger.getLogger(ShortTcpConnector.class);

	public ShortTcpConnector(RecvPacketRuleCfg rule, TcpConnectorHandler handler) {
		super(rule, handler);
	}
	
	public byte[] execute(byte[] message, Object... args) throws Exception {
		IoSession session = null;
		try {
			// Подключиться к серверу
			session = connect();
			return doExecute(session, message, args);
		} finally {
			// Закрыть соединение
			if (session != null) {
				close(session);
				handler.removeReadFuture(session); // Это предложение лучше всего ставить после закрытия сессии
			}
		}
	}

	protected String getConnectorName() {
		return "Tcp Короткое соединение";
	}

}
