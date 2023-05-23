package ru.ssau.redis;

import ru.ssau.redis.model.RedisEmployee;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RedisMain {

    public static void main(String[] args) {
        RedisEmployeeDAO redisDao = new RedisEmployeeDAO();

        redisDao.removeAllKeysFromDatabase();

        RedisEmployee redisEmployee1 = new RedisEmployee("fname1", "lname1", 100);
        RedisEmployee redisEmployee2 = new RedisEmployee("fname2", "lname2", 200);
        RedisEmployee redisEmployee3 = new RedisEmployee("fname3", "lname3", 300);

        redisDao.insertEmployee(redisEmployee1);
        redisDao.insertEmployee(redisEmployee2);
        redisDao.insertEmployee(redisEmployee3);

        List<String> uuids = new ArrayList<>(Arrays.asList(redisEmployee1.getUuid()
                , redisEmployee2.getUuid(), redisEmployee3.getUuid()));
        redisDao.getEmployees(uuids).forEach(System.out::println);

        System.out.println("--------");

        redisEmployee2.setFirstName("updated_fname");
        redisEmployee2.setLastName("updated_lname");
        redisEmployee2.setSalary(400);
        redisDao.updateEmployee(redisEmployee2);

        redisDao.getEmployees(uuids).forEach(System.out::println);

        System.out.println("--------");

        redisDao.removeEmployeeByUuid(redisEmployee1.getUuid());
        redisDao.removeEmployeeByUuid(redisEmployee2.getUuid());
        redisDao.getEmployees(uuids).forEach(System.out::println);
    }

}
