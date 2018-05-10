package com.scott.transer;

import java.lang.reflect.Array;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * <P>Author: shijiale-PUBG</P>
 * <P>Date: 2018/5/10</P>
 * <P>Email: shilec@126.com</p>
 *  可以对元素进行排序的阻塞队列
 */
public class SmallTaskFirstDequeueBlockingQueue<T extends Runnable> extends AbstractQueue<T> implements BlockingQueue<T>{
    private ArrayList<T> arr = new ArrayList<>();
    private ReentrantLock lock;
    private Condition notEmpty;
    private int capacity;

    public SmallTaskFirstDequeueBlockingQueue(int capacity) {
        lock = new ReentrantLock(true);
        notEmpty = lock.newCondition();
        this.capacity = capacity;
    }

    @Override
    public Iterator<T> iterator() {
        return arr.iterator();
    }

    @Override
    public int size() {
        return arr.size();
    }

    @Override
    public void put(T t) throws InterruptedException {
        Objects.requireNonNull(t);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (arr.isEmpty())
                notEmpty.await();
            enqueue(t);
        } finally {
            lock.unlock();
        }
    }

    protected void enqueue(T t) {
        arr.add(t);
        notEmpty.signal();
//        Collections.sort(arr, new Comparator<T>() {
//            @Override
//            public int compare(T o1, T o2) {
//                if(o1 instanceof Comparable && o2 instanceof Comparable) {
//                    return ((Comparable) o1).compareTo(o2);
//                }
//                return 0;
//            }
//        });
    }

    @Override
    public boolean offer(T t, long timeout, TimeUnit unit) throws InterruptedException {
        Objects.requireNonNull(t);
        long nanos = unit.toNanos(timeout);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (arr.isEmpty()) {
                if (nanos <= 0L)
                    return false;
                nanos = notEmpty.awaitNanos(nanos);
            }
            enqueue(t);
            return true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public T take() throws InterruptedException {
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (arr.isEmpty())
                notEmpty.await();
            return dequeue();
        } finally {
            lock.unlock();
        }
    }

    protected T dequeue() {
        T t = arr.get(arr.size() - 1);
        arr.remove(arr.size() - 1);

        T min = Collections.min(arr, new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                if (o1 instanceof Comparable && o2 instanceof Comparable) {
                    return ((Comparable) o2).compareTo(o1);
                }
                return 0;
            }
        });
        arr.remove(min);
        return t;
    }

    @Override
    public T poll(long timeout, TimeUnit unit) throws InterruptedException {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return (arr.isEmpty()) ? null : dequeue();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int remainingCapacity() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return capacity - size();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int drainTo(Collection<? super T> c) {
        return 0;
    }

    @Override
    public int drainTo(Collection<? super T> c, int maxElements) {
        return 0;
    }

    @Override
    public boolean offer(T t) {
        Objects.requireNonNull(t);
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if (arr.size() >= capacity)
                return false;
            else {
                enqueue(t);
                return true;
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public T poll() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return (arr.isEmpty()) ? null : dequeue();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public T peek() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return arr.isEmpty() ? null : arr.get(0);
        } finally {
            lock.unlock();
        }
    }

}
