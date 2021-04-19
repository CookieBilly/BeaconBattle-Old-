

package ws.billy.bedwars.api.exceptions;

import ws.billy.bedwars.api.server.VersionSupport;

public class InvalidEffectException extends Throwable
{
    public InvalidEffectException(final String str) {
        super(str + " is not a valid " + VersionSupport.getName() + " effect! Using defaults..");
    }
}
