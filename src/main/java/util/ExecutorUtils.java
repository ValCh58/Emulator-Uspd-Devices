package util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

/**
 * Служебный класс исполнителя пула потоков
 *
 * @author humy
 *
 */
public class ExecutorUtils {

    public static final Logger logger = Logger.getLogger(ExecutorUtils.class);
    public static final String GROUP_KEY = "GROUP_KEY";
    public static final String EXECUTE_ID_KEY = "EXECUTE_ID_KEY";

    /**
     * Создайте пул соединений с информацией журнала. Когда емкость = 0, запрос
     * не выполняется напрямую ThreadPool; Когда емкость! = 0, запрос
     * кэширования очереди выполняется непосредственно ThreadPool. Если число
     * очередей достигает верхнего предела, новые потоки распределяются до тех
     * пор, пока число потоков не достигнет maxSize.
     *
     * @param corePoolSize количество основных потоков
     * @param maxPoolSize Максимальное количество потоков
     * @param Количество кэшированных очередей задач
     * @param aliveTime простой поток живое время
     * @param group thread Имя группы MDC префикс @param
     * @return
     */
    public static ExecutorService newCachedThreadPool(int corePoolSize, 
    		                                          int maxPoolSize,
    		                                          int capacity, 
    		                                          int aliveTime, 
    		                                          String group, 
    		                                          final String prefix) {
        if (StringUtils.isEmpty(group)) {
            group = "sys";
        }
        final String gp = group;
        BlockingQueue<Runnable> bq = null;
        if (capacity == 0) {
            bq = new SynchronousQueue<Runnable>();
        } else {
            bq = new LinkedBlockingQueue<Runnable>(capacity);
        }
        return new ThreadPoolExecutor(corePoolSize, maxPoolSize, aliveTime, TimeUnit.SECONDS, bq, new NamedThreadFactory(group)) {
            AtomicInteger ai = new AtomicInteger(1);
            SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd_HHmmss");

            protected void beforeExecute(Thread t, Runnable r) {
                // Привязать имя группы к текущему контексту потока log4j
                MDC.put(GROUP_KEY, gp);
                if (prefix != null) {
                    MDC.put(EXECUTE_ID_KEY, prefix + "_" + f.format(new Date()) + "_" + ai.getAndIncrement());
                }
            }

            protected void afterExecute(Runnable r, Throwable t) {
                Hashtable<?, ?> ht = MDC.getContext();
                if (ht != null) {
                    ht.clear();
                }
            }
        };
    }

    /**
     * * Создание пула потоков планирования с информацией журнала.
     *
     * @param corePoolSize количество основных потоков
     * @param group thread Имя группы MDC префикс 
     * @param
     * @return
     */
    public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize, String group, final String prefix) {
        if (StringUtils.isEmpty(group)) {
            group = "sys";
        }
        final String gp = group;
        return new ScheduledThreadPoolExecutor(corePoolSize, new NamedThreadFactory(group)) {
            AtomicInteger ai = new AtomicInteger(1);
            SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd_HHmmss");

            protected void beforeExecute(Thread t, Runnable r) {
                // Привязать имя группы к текущему контексту потока log4j
            	/**
            	 * Класс MDC аналогичен классу NDC, за исключением того, 
            	 * что он основан на карте, а не на стеке. 
            	 * Он предоставляет сопоставленные диагностические контексты. 
            	 * Сопоставленный диагностический контекст, или сокращенно MDC, 
            	 * является инструментом для различения чередующихся выходных данных журнала из разных источников. 
            	 * Выходные данные журнала обычно чередуются, 
            	 * когда сервер обрабатывает несколько клиентов почти одновременно. 
            	 */
                MDC.put(GROUP_KEY, gp);
                if (prefix != null) {
                    MDC.put(EXECUTE_ID_KEY, prefix + "_" + f.format(new Date()) + "_" + ai.getAndIncrement());
                }
            }

            protected void afterExecute(Runnable r, Throwable t) {
                Hashtable<?, ?> ht = MDC.getContext();
                if (ht != null) {
                    ht.clear();
                }
            }
        };
    }

    static class NamedThreadFactory implements ThreadFactory {

        final ThreadGroup group;
        final AtomicInteger threadNumber = new AtomicInteger(1);
        final String namePrefix;

        public NamedThreadFactory(String name) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            namePrefix = "pool-" + name + "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }

    /**
     * Дождитесь, пока задача пула потоков завершит закрытие пула потоков после
     * завершения выполнения, и дождитесь его остановки <br>
     * Подождите, пока выполнение задачи пула потоков завершится не более 1
     * минуты, и немедленно остановите, если она превышена. Подождите, пока пул
     * потоков остановится на срок до 1 минуты, и немедленно вернитесь, если он
     * превышает
     *
     * @param pool
     */
    public static void shutdownAndAwaitTermination(ExecutorService pool) {
        if (pool == null) {
            return;
        }
        pool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("Pool did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Попробуйте немедленно закрыть пул потоков <br>
     * Подождите, пока пул потоков остановится на срок до 1 минуты, и немедленно
     * вернитесь, если он превышает
     *
     * @param pool
     */
    public static void shutdownNowAndAwaitTermination(ExecutorService pool) {
        if (pool == null) {
            return;
        }
        pool.shutdownNow();
        try {
            pool.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e1) {
            // Restore the interrupted status
            Thread.currentThread().interrupt();
        }
    }

}
