

package ws.billy.bedwars.libs.com.zaxxer.hikari.util;

import java.util.Comparator;
import java.util.function.UnaryOperator;
import java.util.function.Predicate;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.ListIterator;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Iterator;
import java.lang.reflect.Array;
import java.io.Serializable;
import java.util.RandomAccess;
import java.util.List;

public final class FastList<T> implements List<T>, RandomAccess, Serializable
{
    private static final long serialVersionUID = -4598088075242913858L;
    private final Class<?> clazz;
    private T[] elementData;
    private int size;
    
    public FastList(final Class<?> clazz) {
        this.elementData = (T[])Array.newInstance(clazz, 32);
        this.clazz = clazz;
    }
    
    public FastList(final Class<?> clazz, final int length) {
        this.elementData = (T[])Array.newInstance(clazz, length);
        this.clazz = clazz;
    }
    
    @Override
    public boolean add(final T t) {
        if (this.size < this.elementData.length) {
            this.elementData[this.size++] = t;
        }
        else {
            final int length = this.elementData.length;
            final Object[] elementData = (Object[])Array.newInstance(this.clazz, length << 1);
            System.arraycopy(this.elementData, 0, elementData, 0, length);
            elementData[this.size++] = t;
            this.elementData = (T[])elementData;
        }
        return true;
    }
    
    @Override
    public T get(final int n) {
        return this.elementData[n];
    }
    
    public T removeLast() {
        final T[] elementData = this.elementData;
        final int size = this.size - 1;
        this.size = size;
        final T t = elementData[size];
        this.elementData[this.size] = null;
        return t;
    }
    
    @Override
    public boolean remove(final Object o) {
        for (int i = this.size - 1; i >= 0; --i) {
            if (o == this.elementData[i]) {
                final int n = this.size - i - 1;
                if (n > 0) {
                    System.arraycopy(this.elementData, i + 1, this.elementData, i, n);
                }
                this.elementData[--this.size] = null;
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void clear() {
        for (int i = 0; i < this.size; ++i) {
            this.elementData[i] = null;
        }
        this.size = 0;
    }
    
    @Override
    public int size() {
        return this.size;
    }
    
    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }
    
    @Override
    public T set(final int n, final T t) {
        final T t2 = this.elementData[n];
        this.elementData[n] = t;
        return t2;
    }
    
    @Override
    public T remove(final int n) {
        if (this.size == 0) {
            return null;
        }
        final T t = this.elementData[n];
        final int n2 = this.size - n - 1;
        if (n2 > 0) {
            System.arraycopy(this.elementData, n + 1, this.elementData, n, n2);
        }
        this.elementData[--this.size] = null;
        return t;
    }
    
    @Override
    public boolean contains(final Object o) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int index;
            
            @Override
            public boolean hasNext() {
                return this.index < FastList.this.size;
            }
            
            @Override
            public T next() {
                if (this.index < FastList.this.size) {
                    return FastList.this.elementData[this.index++];
                }
                throw new NoSuchElementException("No more elements in FastList");
            }
        };
    }
    
    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public <E> E[] toArray(final E[] array) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean containsAll(final Collection<?> collection) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean addAll(final Collection<? extends T> collection) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean addAll(final int n, final Collection<? extends T> collection) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean removeAll(final Collection<?> collection) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean retainAll(final Collection<?> collection) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void add(final int n, final T t) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int indexOf(final Object o) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int lastIndexOf(final Object o) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public ListIterator<T> listIterator() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public ListIterator<T> listIterator(final int n) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public List<T> subList(final int n, final int n2) {
        throw new UnsupportedOperationException();
    }
    
    public Object clone() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void forEach(final Consumer<? super T> consumer) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Spliterator<T> spliterator() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean removeIf(final Predicate<? super T> predicate) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void replaceAll(final UnaryOperator<T> unaryOperator) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void sort(final Comparator<? super T> comparator) {
        throw new UnsupportedOperationException();
    }
}
