package client;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class JobExecutors {

	ScheduledExecutorService service = null;
	
	public JobExecutors() {
		this.service = Executors.newSingleThreadScheduledExecutor();
	}
	
	public void setJobRun(Runnable runThread, int intervalTime) {
		service.scheduleAtFixedRate(runThread, 1, intervalTime, TimeUnit.MINUTES);
	}
}
