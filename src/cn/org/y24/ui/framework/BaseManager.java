/*
 * Copyright (c) 2019.
 *  Author: Y24
 *  All rights reserved.
 */

package cn.org.y24.ui.framework;

import java.util.HashMap;


public abstract class BaseManager<T> implements IBaseManager<T> {
    HashMap<String, T> hashMap = new HashMap<>();


    private boolean contains(String name) {
        return hashMap.containsKey(name);
    }

    private boolean contains(T t) {
        return hashMap.containsValue(t);
    }

    @Override
    public T get(String name) {
        return contains(name) ? hashMap.get(name) : null;
    }

    @Override
    public void destroy() {
        hashMap.clear();
    }

    @Override
    public boolean delete(T t) {
        for (String name : hashMap.keySet()) {
            if (get(name) == t) {
                hashMap.remove(name);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean delete(String name) {
        if (contains(name)) {
            hashMap.remove(name);
            return true;
        } else return false;
    }

    @Override
    public boolean add(T t, String name) {
        if (!contains(t) && !contains(name)) {
            hashMap.put(name, t);
        }
        return true;
    }
}
