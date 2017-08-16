package com.delta.test.aptlearning;

/**
 * Created by Shufeng.Wu on 2017/7/19.
 */

public class PizzaStore {
  MealFactory mealFactory = new MealFactory();

  public Meal order(String mealName) {
    return mealFactory.create(mealName);
  }
}
