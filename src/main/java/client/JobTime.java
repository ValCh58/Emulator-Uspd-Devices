package client;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

import protocol.TcpConstants;
import shorttcp.ShortConnectorHandler;
import shorttcp.ShortTcpConnector;


public class JobTime implements Runnable {
	int numUspd = 0;
	String ipAdr = null;
	int port = 0;
	int coeff = 800;
	
	
	public JobTime(String ipadr, int port, int numUspd, int coeFF) {
		this.numUspd = numUspd;
		this.ipAdr = ipadr;
		this.port = port;
		this.coeff += coeFF;//To change the flow rate for all sensors
	}
	
	
    /**
	 * Client start
	 * @param numUspd
	 */
	public void run() {
		System.out.println("Client start, USPD # " + this.numUspd);
		try {
            startClient();
        } catch (Exception ex) {
            Logger.getLogger(JobTime.class.getName()).log(Level.SEVERE, null, ex);
        }

	}
	//"KARAT(20480806,100,10:26 26.05.21)5002(CE301v11.8s4)5003(008842133258513)0001(01091219133723)1001(2.2789123)(2.2721717)USPD(4)A000(100)(200)(300)(400)(500)(600)(700)(800)Inp(3F)Out(00)Power(4.16)LowPower(0)AlrCnt(00)ErrIn(C0)"
	public void startClient() throws Exception {
        RecvPacketRuleCfg rule = getRecvPacketRuleOfEndChar2();
        rule.set("ip", ipAdr);
        rule.set("port", port);
        rule.set("timeout", 500000);

        ShortTcpConnector connector = new ShortTcpConnector(rule, new ShortConnectorHandler());
        connector.start();
        byte[] tmp = new byte[]{2,3,2};
        String str = dataToServer();
        byte[] message_1 = str.getBytes();
        byte[] message = new byte[message_1.length +3];
        System.arraycopy(message_1, 0, message, 1, message_1.length);
        System.arraycopy(tmp, 0, message, 0, 1);//Writing byte \ 02 at position == 0
        System.arraycopy(tmp, 1, message, message.length-2, 2);//write end of message marker \ 02 \ 03
        connector.execute(message, new Object[0]);

    }
	//java -jar uspdemulator-1.0.jar 10.0.0.8 8880 103 113 60
	/**
	 * Transfer string to USPD 
	 * @return strBuild
	 */
	private String dataToServer() {
		StringBuffer strBuff = new StringBuffer("KARAT(20480,,)"//Теплосчетчик
				+ "5002(CE301v11.8s4)"
				+ "5003(000000000000)" //Device number
				+ "0001()" //date / time day of the week, day, month, year, hour, minutes, seconds, 2 characters for each parameter
				+ "1001()()"//Electric meter day rate, night
				+ "USPD()A000()()(0)(0)(0)(0)(0)(0)"//Water meters: 1 channel Cold Water, 2 channel Hot Water
				+ "Inp(00)Out(00)Power(4.16)LowPower(0)AlrCnt(00)ErrIn(00)");//Diagnostics of USPD and accidents
		buildStr(strBuff, numUspd, "5003(000000000000");//Electric meter number
		buildStr(strBuff, getDataTimeElectro(), "0001(");//Date Time Electric meter  
		buildStr(strBuff, electroConsum(10, 163000000), "1001()(");//night rate readings
		buildStr(strBuff, electroConsum(9, 163000000), "1001(");  //daily rate readings
		buildStr(strBuff, getTimeDateHeater(), "KARAT(20480,,"); //Date Time Hot meter
		buildStr(strBuff, heaterConsum(this.coeff), "KARAT(20480,");// Data Hot meter
		buildStr(strBuff, numUspd, "KARAT(20480");//Hot meter number
		buildStr(strBuff, numUspd, "USPD(");//USPD number
		buildStr(strBuff, waterConsum(this.coeff), "A000()(");// Cold water
		buildStr(strBuff, waterConsum(this.coeff/2), "A000(");  //Hot water
        return strBuff.toString();
	}
	
	private <T> void buildStr(StringBuffer sBuff, T nUspd, String mark) {
		int begin = sBuff.indexOf(mark);
		sBuff.insert(begin + mark.length(), nUspd);
	}
	
	private int waterConsum(int coeff) {
		Instant timeSecNow = Instant.now();
	    long timeNowLong = timeSecNow.getEpochSecond();
	    int retVal = (int) (timeNowLong/coeff - 1000000);
		return retVal;
	}
	
	private String electroConsum(int coeff1, int coeff2) {
		Instant timeSecNow = Instant.now();
	    long timeNowLong = timeSecNow.getEpochSecond();
	    long retVal = (timeNowLong/coeff1 - coeff2);
	    StringBuffer sLong = new StringBuffer(Long.toString(retVal));
	    sLong.insert(sLong.length()-3, '.');
		return sLong.toString();
	}
	
	private int heaterConsum(int coeff) {
		Instant timeSecNow = Instant.now();
	    long timeNowLong = timeSecNow.getEpochSecond();
	    int retVal = (int) (timeNowLong/coeff - 1800000);
		return retVal;
	}
	
	private String getTimeDateHeater() {
		LocalDateTime ld = LocalDateTime.now();
		return ld.getHour() + ":" + ld.getMinute() + " " + 
				ld.getDayOfMonth() + "." + ld.getMonthValue() + "." + Integer.toString(ld.getYear()).substring(2); 
		
	}
	
	private String getDataTimeElectro() {
		LocalDateTime ldt = LocalDateTime.now();
		return Integer.toString(ldt.getDayOfMonth()) + Integer.toString(ldt.getDayOfMonth()) + 
		            Integer.toString(ldt.getMonthValue()) + Integer.toString(ldt.getYear()).substring(2) +
		            Integer.toString(ldt.getHour()) + Integer.toString(ldt.getMinute()) + Integer.toString(ldt.getSecond());
	}
	
	public static RecvPacketRuleCfg getRecvPacketRuleOfEndChar2() {
        RecvPacketRuleCfg rule = new RecvPacketRuleCfg();
        rule.setType(TcpConstants.RecvPacketRuleConstants.TYPE_ENDCHAR_2);
        rule.set("endChar1", "0X03");//The penultimate byte of the package
        rule.set("endChar2", "0X02");//The last byte of the message
        //rule.set("escapeChar", "\\");
        return rule;
    }

}
