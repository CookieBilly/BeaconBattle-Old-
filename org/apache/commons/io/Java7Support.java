

package org.apache.commons.io;

import java.lang.reflect.Array;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.io.File;
import java.lang.reflect.Method;

class Java7Support
{
    private static final boolean IS_JAVA7;
    private static Method isSymbolicLink;
    private static Method delete;
    private static Method toPath;
    private static Method exists;
    private static Method toFile;
    private static Method readSymlink;
    private static Method createSymlink;
    private static Object emptyLinkOpts;
    private static Object emptyFileAttributes;
    
    public static boolean isSymLink(final File obj) {
        try {
            return (boolean)Java7Support.isSymbolicLink.invoke(null, Java7Support.toPath.invoke(obj, new Object[0]));
        }
        catch (IllegalAccessException cause) {
            throw new RuntimeException(cause);
        }
        catch (InvocationTargetException cause2) {
            throw new RuntimeException(cause2);
        }
    }
    
    public static File readSymbolicLink(final File obj) {
        try {
            return (File)Java7Support.toFile.invoke(Java7Support.readSymlink.invoke(null, Java7Support.toPath.invoke(obj, new Object[0])), new Object[0]);
        }
        catch (IllegalAccessException cause) {
            throw new RuntimeException(cause);
        }
        catch (InvocationTargetException cause2) {
            throw new RuntimeException(cause2);
        }
    }
    
    private static boolean exists(final File obj) {
        try {
            return (boolean)Java7Support.exists.invoke(null, Java7Support.toPath.invoke(obj, new Object[0]), Java7Support.emptyLinkOpts);
        }
        catch (IllegalAccessException cause) {
            throw new RuntimeException(cause);
        }
        catch (InvocationTargetException ex) {
            throw (RuntimeException)ex.getTargetException();
        }
    }
    
    public static File createSymbolicLink(final File obj, final File obj2) {
        try {
            if (!exists(obj)) {
                return (File)Java7Support.toFile.invoke(Java7Support.createSymlink.invoke(null, Java7Support.toPath.invoke(obj, new Object[0]), Java7Support.toPath.invoke(obj2, new Object[0]), Java7Support.emptyFileAttributes), new Object[0]);
            }
            return obj;
        }
        catch (IllegalAccessException cause) {
            throw new RuntimeException(cause);
        }
        catch (InvocationTargetException ex) {
            throw (IOException)ex.getTargetException();
        }
    }
    
    public static void delete(final File obj) {
        try {
            Java7Support.delete.invoke(null, Java7Support.toPath.invoke(obj, new Object[0]));
        }
        catch (IllegalAccessException cause) {
            throw new RuntimeException(cause);
        }
        catch (InvocationTargetException ex) {
            throw (IOException)ex.getTargetException();
        }
    }
    
    public static boolean isAtLeastJava7() {
        return Java7Support.IS_JAVA7;
    }
    
    static {
        boolean is_JAVA7 = true;
        try {
            final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            final Class<?> loadClass = contextClassLoader.loadClass("java.nio.file.Files");
            final Class<?> loadClass2 = contextClassLoader.loadClass("java.nio.file.Path");
            final Class<?> loadClass3 = contextClassLoader.loadClass("java.nio.file.attribute.FileAttribute");
            final Class<?> loadClass4 = contextClassLoader.loadClass("java.nio.file.LinkOption");
            Java7Support.isSymbolicLink = loadClass.getMethod("isSymbolicLink", loadClass2);
            Java7Support.delete = loadClass.getMethod("delete", loadClass2);
            Java7Support.readSymlink = loadClass.getMethod("readSymbolicLink", loadClass2);
            Java7Support.emptyFileAttributes = Array.newInstance(loadClass3, 0);
            Java7Support.createSymlink = loadClass.getMethod("createSymbolicLink", loadClass2, loadClass2, Java7Support.emptyFileAttributes.getClass());
            Java7Support.emptyLinkOpts = Array.newInstance(loadClass4, 0);
            Java7Support.exists = loadClass.getMethod("exists", loadClass2, Java7Support.emptyLinkOpts.getClass());
            Java7Support.toPath = File.class.getMethod("toPath", (Class<?>[])new Class[0]);
            Java7Support.toFile = loadClass2.getMethod("toFile", (Class[])new Class[0]);
        }
        catch (ClassNotFoundException ex) {
            is_JAVA7 = false;
        }
        catch (NoSuchMethodException ex2) {
            is_JAVA7 = false;
        }
        IS_JAVA7 = is_JAVA7;
    }
}
