

package ws.billy.bedwars.api.exceptions;

import ws.billy.bedwars.api.server.VersionSupport;

public class InvalidSoundException extends Throwable
{
    public InvalidSoundException(final String str) {
        super(str + " is not a valid " + VersionSupport.getName() + " sound! Using defaults..");
    }
}
