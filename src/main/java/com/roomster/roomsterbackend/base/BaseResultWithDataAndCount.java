package com.roomster.roomsterbackend.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseResultWithDataAndCount<T> {
    private T data;

    private Long count;

    public void set(T data, Long count) {
        this.data = data;
        this.count = count;
    }
}
