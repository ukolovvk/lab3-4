package ru.ssau.mongo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.bson.types.ObjectId;

@Data
@AllArgsConstructor
@ToString
public class MongoDepartment {
    private ObjectId id;
    private String title;
    private String address;

    public MongoDepartment(String title, String address) {
        this.title = title;
        this.address = address;
    }
}
