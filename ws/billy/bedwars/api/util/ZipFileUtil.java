

package ws.billy.bedwars.api.util;

import java.io.InputStream;
import java.util.Enumeration;
import java.io.IOException;
import java.util.zip.ZipFile;
import java.io.FileInputStream;
import java.util.zip.ZipEntry;
import java.io.OutputStream;
import java.util.zip.ZipOutputStream;
import java.io.FileOutputStream;
import java.io.File;

public final class ZipFileUtil
{
    public static void zipDirectory(final File file, final File file2) {
        final ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(file2));
        zipSubDirectory("", file, zipOutputStream);
        zipOutputStream.close();
    }
    
    private static void zipSubDirectory(final String s, final File file, final ZipOutputStream zipOutputStream) {
        final byte[] array = new byte[4096];
        final File[] listFiles = file.listFiles();
        if (listFiles == null) {
            return;
        }
        for (final File file2 : listFiles) {
            if (file2.isDirectory()) {
                final String string = s + file2.getName() + "/";
                zipOutputStream.putNextEntry(new ZipEntry(string));
                zipSubDirectory(string, file2, zipOutputStream);
                zipOutputStream.closeEntry();
            }
            else {
                final FileInputStream fileInputStream = new FileInputStream(file2);
                zipOutputStream.putNextEntry(new ZipEntry(s + file2.getName()));
                int read;
                while ((read = fileInputStream.read(array)) > 0) {
                    zipOutputStream.write(array, 0, read);
                }
                zipOutputStream.closeEntry();
                fileInputStream.close();
            }
        }
    }
    
    public static void unzipFileIntoDirectory(final File file, final File file2) {
        if (!file.exists()) {
            return;
        }
        final ZipFile zipFile = new ZipFile(file);
        final Enumeration<? extends ZipEntry> entries = zipFile.entries();
        FileOutputStream fileOutputStream = null;
        while (entries.hasMoreElements()) {
            try {
                final ZipEntry entry = (ZipEntry)entries.nextElement();
                final InputStream inputStream = zipFile.getInputStream(entry);
                final byte[] array = new byte[1024];
                final File file3 = new File(file2.getAbsolutePath(), entry.getName());
                if (entry.isDirectory()) {
                    file3.mkdirs();
                }
                file3.getParentFile().mkdirs();
                file3.createNewFile();
                fileOutputStream = new FileOutputStream(file3);
                int read;
                while ((read = inputStream.read(array)) != -1) {
                    fileOutputStream.write(array, 0, read);
                }
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
            finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    }
                    catch (IOException ex2) {}
                }
            }
        }
    }
}
