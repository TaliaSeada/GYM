package com.example.gym.workouts;

public class exe_object {
    // set fields
    private final String name;
    private final Long reps;
    private final Long sets;
    private final double weight;

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
