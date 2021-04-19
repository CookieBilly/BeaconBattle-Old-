

package ws.billy.bedwars.api.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.Properties;
import ws.billy.bedwars.api.server.VersionSupport;
import java.io.File;

public class FileUtil
{
    public static void delete(final File file) {
        if (file.isDirectory()) {
            final File[] listFiles = file.listFiles();
            for (int length = listFiles.length, i = 0; i < length; ++i) {
                delete(listFiles[i]);
            }
        }
        else {
            file.delete();
        }
    }
    
    public static void setMainLevel(final String value, final VersionSupport versionSupport) {
        final Properties properties = new Properties();
        try (final FileInputStream inStream = new FileInputStream("server.properties")) {
            properties.load(inStream);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        properties.setProperty("level-name", value);
        properties.setProperty("generator-settings", (versionSupport.getVersion() > 5) ? "minecraft:air;minecraft:air;minecraft:air" : "1;0;1");
        properties.setProperty("allow-nether", "false");
        properties.setProperty("level-type", "flat");
        properties.setProperty("generate-structures", "false");
        properties.setProperty("spawn-monsters", "false");
        properties.setProperty("max-world-size", "1000");
        properties.setProperty("spawn-animals", "false");
        try (final FileOutputStream out = new FileOutputStream("server.properties")) {
            properties.store(out, null);
        }
        catch (IOException ex2) {
            ex2.printStackTrace();
        }
    }
}
