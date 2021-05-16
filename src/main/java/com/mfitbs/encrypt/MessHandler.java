package com.mfitbs.encrypt;

@FunctionalInterface
public interface MessHandler<T> {
    void exec(T t) throws Exception;
}
