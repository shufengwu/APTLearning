package com.delta.test.aptlearning;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    System.out.println("welcome to pizza store");
    PizzaStore pizzaStore = new PizzaStore();
    Meal meal = pizzaStore.order("Tiramisu");
    System.out.println("Bill:$" + meal.getPrice());
  }
}
