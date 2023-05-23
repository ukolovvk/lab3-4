package ru.ssau.redis;

import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.Jedis;
import ru.ssau.redis.model.RedisEmployee;

import java.util.*;
import java.util.stream.Collectors;

public class RedisEmployeeDAO {

    private DefaultJedisClientConfig config;

    public RedisEmployeeDAO() {
        config = DefaultJedisClientConfig.builder().password(RedisConstants.PASSWORD).build();
    }

    public void insertEmployee(RedisEmployee employee) {
        try (var jedis = new Jedis(RedisConstants.HOST, RedisConstants.PORT, config)) {
            UUID uuid = UUID.randomUUID();
            employee.setUuid(uuid.toString());
            jedis.hset(employee.getUuid(), employee.getFields());
        }
    }

    public Optional<RedisEmployee> getEmployeeByUuid(String uuid) {
        try (var jedis = new Jedis(RedisConstants.HOST, RedisConstants.PORT, config)) {
            Map<String, String> fields = jedis.hgetAll(uuid);
            if (fields == null || fields.get(RedisEmployee.UUID) == null) return Optional.empty();
            RedisEmployee redisEmployee = new RedisEmployee(
                    fields.get(RedisEmployee.UUID),
                    fields.get(RedisEmployee.FIRST_NAME),
                    fields.get(RedisEmployee.LAST_NAME),
                    Double.parseDouble(fields.get(RedisEmployee.SALARY))
                    );
            return Optional.of(redisEmployee);
        }
    }

    public List<RedisEmployee> getEmployees(List<String> uuids) {
        return uuids.stream().map(this::getEmployeeByUuid)
                .flatMap(Optional::stream).collect(Collectors.toList());
    }

    public void updateEmployee(RedisEmployee employee) {
        if (getEmployeeByUuid(employee.getUuid()).isPresent()) {
            try (var jedis = new Jedis(RedisConstants.HOST, RedisConstants.PORT, config)) {
                jedis.hset(employee.getUuid(), employee.getFields());
            }
        }
        else System.out.printf("Employee with %s uuid doesn't exist", employee.getUuid());
    }

    public void removeEmployeeByUuid(String uuid) {
        try (var jedis = new Jedis(RedisConstants.HOST, RedisConstants.PORT, config)) {
            jedis.del(uuid);
        }
    }

    public Set<String> getAllKeys() {
        try (var jedis = new Jedis(RedisConstants.HOST, RedisConstants.PORT, config)) {
            return jedis.keys("*");
        }
    }

    public void removeAllKeysFromDatabase() {
        try (var jedis = new Jedis(RedisConstants.HOST, RedisConstants.PORT, config)) {
            jedis.flushDB();
        }
    }
}
