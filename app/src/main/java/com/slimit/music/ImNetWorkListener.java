package com.slimit.music;

public interface ImNetWorkListener<T> {
    void failed();

    void succeed(T response);

    void noData();
}
