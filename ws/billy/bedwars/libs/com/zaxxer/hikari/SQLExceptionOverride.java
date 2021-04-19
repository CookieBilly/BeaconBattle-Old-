

package ws.billy.bedwars.libs.com.zaxxer.hikari;

import java.sql.SQLException;

public interface SQLExceptionOverride
{
    default Override adjudicate(final SQLException sqlException) {
        return Override.CONTINUE_EVICT;
    }
    
    public enum Override
    {
        CONTINUE_EVICT, 
        DO_NOT_EVICT;
    }
}
