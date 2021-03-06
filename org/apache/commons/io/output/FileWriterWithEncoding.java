

package org.apache.commons.io.output;

import java.io.OutputStream;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.Charset;
import java.io.File;
import java.io.Writer;

public class FileWriterWithEncoding extends Writer
{
    private final Writer out;
    
    public FileWriterWithEncoding(final String pathname, final String s) {
        this(new File(pathname), s, false);
    }
    
    public FileWriterWithEncoding(final String pathname, final String s, final boolean b) {
        this(new File(pathname), s, b);
    }
    
    public FileWriterWithEncoding(final String pathname, final Charset charset) {
        this(new File(pathname), charset, false);
    }
    
    public FileWriterWithEncoding(final String pathname, final Charset charset, final boolean b) {
        this(new File(pathname), charset, b);
    }
    
    public FileWriterWithEncoding(final String pathname, final CharsetEncoder charsetEncoder) {
        this(new File(pathname), charsetEncoder, false);
    }
    
    public FileWriterWithEncoding(final String pathname, final CharsetEncoder charsetEncoder, final boolean b) {
        this(new File(pathname), charsetEncoder, b);
    }
    
    public FileWriterWithEncoding(final File file, final String s) {
        this(file, s, false);
    }
    
    public FileWriterWithEncoding(final File file, final String s, final boolean b) {
        this.out = initWriter(file, s, b);
    }
    
    public FileWriterWithEncoding(final File file, final Charset charset) {
        this(file, charset, false);
    }
    
    public FileWriterWithEncoding(final File file, final Charset charset, final boolean b) {
        this.out = initWriter(file, charset, b);
    }
    
    public FileWriterWithEncoding(final File file, final CharsetEncoder charsetEncoder) {
        this(file, charsetEncoder, false);
    }
    
    public FileWriterWithEncoding(final File file, final CharsetEncoder charsetEncoder, final boolean b) {
        this.out = initWriter(file, charsetEncoder, b);
    }
    
    private static Writer initWriter(final File file, final Object o, final boolean append) {
        if (file == null) {
            throw new NullPointerException("File is missing");
        }
        if (o == null) {
            throw new NullPointerException("Encoding is missing");
        }
        final boolean exists = file.exists();
        OutputStream out = null;
        Writer writer = null;
        try {
            out = new FileOutputStream(file, append);
            if (o instanceof Charset) {
                writer = new OutputStreamWriter(out, (Charset)o);
            }
            else if (o instanceof CharsetEncoder) {
                writer = new OutputStreamWriter(out, (CharsetEncoder)o);
            }
            else {
                writer = new OutputStreamWriter(out, (String)o);
            }
        }
        catch (IOException ex) {
            IOUtils.closeQuietly(writer);
            IOUtils.closeQuietly(out);
            if (!exists) {
                FileUtils.deleteQuietly(file);
            }
            throw ex;
        }
        catch (RuntimeException ex2) {
            IOUtils.closeQuietly(writer);
            IOUtils.closeQuietly(out);
            if (!exists) {
                FileUtils.deleteQuietly(file);
            }
            throw ex2;
        }
        return writer;
    }
    
    @Override
    public void write(final int c) {
        this.out.write(c);
    }
    
    @Override
    public void write(final char[] cbuf) {
        this.out.write(cbuf);
    }
    
    @Override
    public void write(final char[] array, final int n, final int n2) {
        this.out.write(array, n, n2);
    }
    
    @Override
    public void write(final String str) {
        this.out.write(str);
    }
    
    @Override
    public void write(final String str, final int off, final int len) {
        this.out.write(str, off, len);
    }
    
    @Override
    public void flush() {
        this.out.flush();
    }
    
    @Override
    public void close() {
        this.out.close();
    }
}
