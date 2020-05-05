package cn.org.y24.interfaces;


public interface IHandler<T, O, R> {
    R handle(T target, O options);
    void dispose();
}
