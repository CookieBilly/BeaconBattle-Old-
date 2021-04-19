

package ws.billy.bedwars.api.exceptions;

import ws.billy.bedwars.api.server.VersionSupport;

public class InvalidMaterialException extends Exception
{
    public InvalidMaterialException(final String str) {
        super(str + " is not a valid " + VersionSupport.getName() + " material! Using defaults..");
    }
}
