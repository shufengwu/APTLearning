package com.delta.test.aptlearning;

/**
 * Created by Shufeng.Wu on 2017/7/19.
 */

public class MealFactory {
    public Meal create(String id) {
        if (null == id) {
            throw new IllegalArgumentException("name of meal is null!");
        }
        if ("Margherita".equals(id)) {
            return new MargheritaPizza();
        }

        if ("Calzone".equals(id)) {
            return new CalzonePizza();
        }

        if ("Tiramisu".equals(id)) {
            return new Tiramisu();
        }

        throw new IllegalArgumentException("Unknown meal '" + id + "'");
    }
}
