package com.delta.test.aptlearning;

import com.example.Factory;

/**
 * Created by Shufeng.Wu on 2017/7/19.
 */

@Factory(id="Calzone")
public class CalzonePizza implements Meal {
    @Override
    public float getPrice() {
        return 0.2f;
    }
}
