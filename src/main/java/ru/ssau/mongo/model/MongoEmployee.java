package ru.ssau.mongo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.bson.types.ObjectId;

@Data
@AllArgsConstructor
@ToString
public class MongoEmployee {
    private ObjectId id;
    private String firstName;
    private String lastName;
    private double salary;
    private ObjectId departmentId;

    public MongoEmployee(String firstName, String lastName, double salary, ObjectId departmentId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.salary = salary;
        this.departmentId = departmentId;
    }
}
