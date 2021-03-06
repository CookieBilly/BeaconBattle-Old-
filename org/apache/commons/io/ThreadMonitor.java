

package org.apache.commons.io;

class ThreadMonitor implements Runnable
{
    private final Thread thread;
    private final long timeout;
    
    public static Thread start(final long n) {
        return start(Thread.currentThread(), n);
    }
    
    public static Thread start(final Thread thread, final long n) {
        Thread thread2 = null;
        if (n > 0L) {
            thread2 = new Thread(new ThreadMonitor(thread, n), ThreadMonitor.class.getSimpleName());
            thread2.setDaemon(true);
            thread2.start();
        }
        return thread2;
    }
    
    public static void stop(final Thread thread) {
        if (thread != null) {
            thread.interrupt();
        }
    }
    
    private ThreadMonitor(final Thread thread, final long timeout) {
        this.thread = thread;
        this.timeout = timeout;
    }
    
    @Override
    public void run() {
        try {
            sleep(this.timeout);
            this.thread.interrupt();
        }
        catch (InterruptedException ex) {}
    }
    
    private static void sleep(final long n) {
        final long n2 = System.currentTimeMillis() + n;
        long n3 = n;
        do {
            Thread.sleep(n3);
            n3 = n2 - System.currentTimeMillis();
        } while (n3 > 0L);
    }
}
