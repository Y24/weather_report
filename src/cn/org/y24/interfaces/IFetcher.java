package cn.org.y24.interfaces;

public interface IFetcher<T, V> {
    V fetch(T target);
}
