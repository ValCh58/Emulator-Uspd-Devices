package protocol;

import client.RecvPacketRuleCfg;
import org.apache.mina.filter.codec.ProtocolCodecFactory;

/**
 * Протокол фабрики декодирования экземпляра фабричного класса
 * 
 */
public class ProtocolCodecFactoryFactory implements TcpConstants.RecvPacketRuleConstants {
        //Выбираем тип протокола
	public static ProtocolCodecFactory getInstance(RecvPacketRuleCfg rule) {
		                
        if (TYPE_ENDCHAR_2.equals(rule.getType())) {
			EndChar2ProtocalCodecFactory1 ret = new EndChar2ProtocalCodecFactory1();
			ret.setEndChar((String) rule.get("endChar1"), (String) rule.get("endChar2"));
                        //ret.setEscapeChar((String) rule.get("escapeChar"));
			return ret;
		}
             return null;   
	}          
		
}
