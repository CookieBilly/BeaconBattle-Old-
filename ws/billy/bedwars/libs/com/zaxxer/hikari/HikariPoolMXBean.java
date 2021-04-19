

package ws.billy.bedwars.libs.com.zaxxer.hikari;

public interface HikariPoolMXBean
{
    int getIdleConnections();
    
    int getActiveConnections();
    
    int getTotalConnections();
    
    int getThreadsAwaitingConnection();
    
    void softEvictConnections();
    
    void suspendPool();
    
    void resumePool();
}
