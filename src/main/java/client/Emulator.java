package client;

public class Emulator {
	/**
	 * @param args
	 * 
	 * @param ip_server
	 * @param port_server
	 * @param start_numUspd 
	 * @param end_numUspd
	 * @param interval 
	 */

	public static void main(String[] args) {
		
		if(args.length != 5) {
		   System.out.println("Parameters needed: ip_server port_server start_numUspd end_numUspd interval_time"); 
		   System.exit(0);
		}
		
		final String ip = args[0];
		final int port  = Integer.parseInt(args[1]);
		final int start_numUspd = Integer.parseInt(args[2]);
		final int end_numUspd = Integer.parseInt(args[3]);
		final int interval = Integer.parseInt(args[4]);
		
		for(int coef = 0, i = start_numUspd; i <= end_numUspd; i++, coef++) {
			JobTime jt = new JobTime(ip, port, i, coef);
			JobExecutors job = new JobExecutors();
		    job.setJobRun(jt, interval);
		}

	}

	

}

