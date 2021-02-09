package com.simon.ocean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author shizi
 * @since 2020/3/30 7:35 PM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public final class Pair<K,V> implements Serializable {

    private K key;
    private V value;
}
