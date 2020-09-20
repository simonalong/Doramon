package com.simon.ocean;

/**
 * @author zhouzhenyong
 * @since 2018/9/19 下午2:02
 */

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author zhouzhenyong
 * @since 2018/4/25 下午4:05
 */
@UtilityClass
public class Out {

    public void show(Object object) {
        if(null == object){
            System.out.println("obj is null ");
            return;
        }
        Optional.ofNullable(object).ifPresent(objects1 -> System.out.println(objects1.toString()));
    }

    public void tab() {
        show("----------------------------------------");
    }

    public void tab(Object... objects) {
        Arrays.asList(objects).stream().forEach(object -> {
            show("********* " + object);
        });
    }

    public void show(Object... objects) {
        Optional.ofNullable(objects).ifPresent(objects1 -> Arrays.stream(objects1).forEach(System.out::println));
    }

    @SneakyThrows
    public void sleep(int cnt){
        Thread.sleep(cnt * 1000);
    }
}
