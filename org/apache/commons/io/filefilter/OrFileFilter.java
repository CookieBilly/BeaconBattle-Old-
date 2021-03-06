

package org.apache.commons.io.filefilter;

import java.util.Iterator;
import java.io.File;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class OrFileFilter extends AbstractFileFilter implements ConditionalFileFilter, Serializable
{
    private static final long serialVersionUID = 5767770777065432721L;
    private final List<IOFileFilter> fileFilters;
    
    public OrFileFilter() {
        this.fileFilters = new ArrayList<IOFileFilter>();
    }
    
    public OrFileFilter(final List<IOFileFilter> c) {
        if (c == null) {
            this.fileFilters = new ArrayList<IOFileFilter>();
        }
        else {
            this.fileFilters = new ArrayList<IOFileFilter>(c);
        }
    }
    
    public OrFileFilter(final IOFileFilter ioFileFilter, final IOFileFilter ioFileFilter2) {
        if (ioFileFilter == null || ioFileFilter2 == null) {
            throw new IllegalArgumentException("The filters must not be null");
        }
        this.fileFilters = new ArrayList<IOFileFilter>(2);
        this.addFileFilter(ioFileFilter);
        this.addFileFilter(ioFileFilter2);
    }
    
    @Override
    public void addFileFilter(final IOFileFilter ioFileFilter) {
        this.fileFilters.add(ioFileFilter);
    }
    
    @Override
    public List<IOFileFilter> getFileFilters() {
        return Collections.unmodifiableList((List<? extends IOFileFilter>)this.fileFilters);
    }
    
    @Override
    public boolean removeFileFilter(final IOFileFilter ioFileFilter) {
        return this.fileFilters.remove(ioFileFilter);
    }
    
    @Override
    public void setFileFilters(final List<IOFileFilter> list) {
        this.fileFilters.clear();
        this.fileFilters.addAll(list);
    }
    
    @Override
    public boolean accept(final File file) {
        final Iterator<IOFileFilter> iterator = this.fileFilters.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().accept(file)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean accept(final File file, final String s) {
        final Iterator<IOFileFilter> iterator = this.fileFilters.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().accept(file, s)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append("(");
        if (this.fileFilters != null) {
            for (int i = 0; i < this.fileFilters.size(); ++i) {
                if (i > 0) {
                    sb.append(",");
                }
                final IOFileFilter value = this.fileFilters.get(i);
                sb.append((value == null) ? "null" : value.toString());
            }
        }
        sb.append(")");
        return sb.toString();
    }
}
