package cn.org.y24.ui.framework;

public interface IBaseManager<T> {

    boolean add(T t, String name);

    T get(String name);

    boolean delete(T t);

    boolean delete(String name);

    void destroy();
}