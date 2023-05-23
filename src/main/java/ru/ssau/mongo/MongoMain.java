package ru.ssau.mongo;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;
import ru.ssau.mongo.model.MongoDepartment;
import ru.ssau.mongo.model.MongoEmployee;

public class MongoMain {

    public static void main(String[] args) {
        disableMongoLogging();

        MongoEmployeeDAO employeeDAO = new MongoEmployeeDAO();
        employeeDAO.removeAllEmployees();
        MongoDepartmentDAO departmentDAO = new MongoDepartmentDAO();
        departmentDAO.removeAllDepartments();

        MongoDepartment department1 = new MongoDepartment("title1", "address1");
        MongoDepartment department2 = new MongoDepartment("title2", "address2");

        departmentDAO.insertDepartment(department1);
        departmentDAO.insertDepartment(department2);

        departmentDAO.getAllDepartments().forEach(System.out::println);

        MongoEmployee employee1 = new MongoEmployee("f_name_1", "l_name_1", 150., department1.getId());
        MongoEmployee employee2 = new MongoEmployee("f_name_2", "l_name_2", 250., department1.getId());
        MongoEmployee employee3 = new MongoEmployee("f_name_3", "l_name_3", 350., department2.getId());

        employeeDAO.insertEmployee(employee1);
        employeeDAO.insertEmployee(employee2);
        employeeDAO.insertEmployee(employee3);

        System.out.println("-----");

        employeeDAO.getAllEmployees().forEach(System.out::println);

        System.out.println("-----");

        employee2.setFirstName("updated_first_name");
        employee2.setLastName("updated_last_name");
        employeeDAO.updateEmployee(employee2);
        employeeDAO.getAllEmployees().forEach(System.out::println);

        System.out.println("-----");

        employeeDAO.removeEmployeeById(employee2.getId());
        employeeDAO.getAllEmployees().forEach(System.out::println);

        System.out.println("-----");

        employeeDAO.joinDepartments().forEach(System.out::println);
    }

    private static void disableMongoLogging() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = loggerContext.getLogger("org.mongodb.driver");
        rootLogger.setLevel(Level.OFF);
    }

}