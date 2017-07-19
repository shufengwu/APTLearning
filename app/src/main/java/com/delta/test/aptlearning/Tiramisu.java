package com.delta.test.aptlearning;

import com.example.Factory;

/**
 * Created by Shufeng.Wu on 2017/7/19.
 */

@Factory
public class Tiramisu implements Meal {
    @Override
    public float getPrice() {
        return 0.3f;
    }
}
