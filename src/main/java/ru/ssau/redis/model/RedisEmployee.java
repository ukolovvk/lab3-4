package ru.ssau.redis.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Data
@ToString
@AllArgsConstructor
public class RedisEmployee {

    public static final String UUID = "uuid";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String SALARY = "salary";

    private String uuid;
    private String firstName;
    private String lastName;
    private double salary;

    public RedisEmployee(String firstName, String lastName, double salary) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.salary = salary;
    }

    public Map<String, String> getFields() {
        Map<String, String> result = new HashMap<>();
        result.put(UUID, uuid);
        result.put(FIRST_NAME, firstName);
        result.put(LAST_NAME, lastName);
        result.put(SALARY, Double.toString(salary));
        return result;
    }
}
