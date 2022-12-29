package com.example.gym.workouts;

public class exe_object {
    // set fields
    String name;
    Long reps;
    Long sets;
    double weight;

    public exe_object(String name, Long reps, Long sets, double weight) {
        this.name = name;
        this.reps = reps;
        this.sets = sets;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "name: "+ name +'\n' +
                "reps: " + reps + '\n' +
                "sets: " + sets + '\n' +
                "weight: " + weight +'\n';
    }
}
