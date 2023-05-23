package ru.ssau.mongo;

import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import ru.ssau.mongo.model.MongoEmployee;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class MongoEmployeeDAO {

    private static final String ID = "_id";
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String SALARY = "salary";
    private static final String DEPARTMENT_ID = "department_id";
    private static final String EMPLOYEES = "employees";

    public void insertEmployee(MongoEmployee employee) {
        try (var mongoClient = MongoClients.create(MongoConstants.URL)) {
            var database = mongoClient.getDatabase(MongoConstants.DATABASE);
            MongoCollection<Document> employeesCollection = database.getCollection(EMPLOYEES);
            Document document = new Document();
            document.append(FIRST_NAME, employee.getFirstName());
            document.append(LAST_NAME, employee.getLastName());
            document.append(SALARY, employee.getSalary());
            document.append(DEPARTMENT_ID, employee.getDepartmentId());
            employeesCollection.insertOne(document);
            ObjectId insertedId = (ObjectId) document.get(ID);
            employee.setId(insertedId);
        } catch (MongoException ex) {
            throw new RuntimeException("Failed to insert employee", ex);
        }
    }

    public Optional<MongoEmployee> findEmployeeById(ObjectId id) {
        try (var mongoClient = MongoClients.create(MongoConstants.URL)) {
            var database = mongoClient.getDatabase(MongoConstants.DATABASE);
            MongoCollection<Document> employeesCollection = database.getCollection(EMPLOYEES);
            Bson filter = Filters.eq(ID, id);
            Document document = employeesCollection.find(filter).first();
            if (document == null) return Optional.empty();
            return Optional.of(getEmployeeFromDocument(document));
        } catch (MongoException ex) {
            throw new RuntimeException("Failed to find employee", ex);
        }
    }

    public void updateEmployee(MongoEmployee employee) {
        try (var mongoClient = MongoClients.create(MongoConstants.URL)) {
            var database = mongoClient.getDatabase(MongoConstants.DATABASE);
            MongoCollection<Document> employeesCollection = database.getCollection(EMPLOYEES);
            Document query = new Document().append(ID, employee.getId());
            Bson updates = Updates.combine(
                Updates.set(FIRST_NAME, employee.getFirstName()),
                Updates.set(LAST_NAME, employee.getLastName()),
                Updates.set(SALARY, employee.getSalary()),
                Updates.set(DEPARTMENT_ID, employee.getDepartmentId())
            );
            employeesCollection.updateOne(query, updates);
        } catch (MongoException ex) {
            ex.printStackTrace();
        }
    }

    public void removeEmployeeById(ObjectId id) {
        try (var mongoClient = MongoClients.create(MongoConstants.URL)) {
            var database = mongoClient.getDatabase(MongoConstants.DATABASE);
            MongoCollection<Document> employeesCollection = database.getCollection(EMPLOYEES);
            Bson filter = Filters.eq(ID, id);
            employeesCollection.deleteOne(filter);
        } catch (MongoException ex) {
            ex.printStackTrace();
        }
    }

    public void removeAllEmployees() {
        try (var mongoClient = MongoClients.create(MongoConstants.URL)) {
            var database = mongoClient.getDatabase(MongoConstants.DATABASE);
            MongoCollection<Document> employeesCollection = database.getCollection(EMPLOYEES);
            employeesCollection.deleteMany(new Document());
        }
    }

    public List<MongoEmployee> getAllEmployees() {
        try (var mongoClient = MongoClients.create(MongoConstants.URL)) {
            var database = mongoClient.getDatabase(MongoConstants.DATABASE);
            MongoCollection<Document> employeesCollection = database.getCollection(EMPLOYEES);
            FindIterable<Document> documents = employeesCollection.find();
            List<MongoEmployee> result = new ArrayList<>();
            documents.forEach((Consumer<? super Document>) doc -> result.add(getEmployeeFromDocument(doc)));
            return result;
        } catch (MongoException ex) {
            throw new RuntimeException("Failed to get all employees", ex);
        }
    }

    public List<Document> joinDepartments() {
        try (var mongoClient = MongoClients.create(MongoConstants.URL)) {
            var database = mongoClient.getDatabase(MongoConstants.DATABASE);
            MongoCollection<Document> employeesCollection = database.getCollection(EMPLOYEES);
            Bson joinOptions = Aggregates.lookup("departments"
                    , "department_id", "_id", "department_data");
            return employeesCollection.aggregate(Collections.singletonList(joinOptions)).into(new ArrayList<>());
        }
    }

    private MongoEmployee getEmployeeFromDocument(Document document) {
        ObjectId id = document.getObjectId(ID);
        String firstName = document.getString(FIRST_NAME);
        String lastName = document.getString(LAST_NAME);
        double salary = document.getDouble(SALARY);
        ObjectId departmentId = document.getObjectId(DEPARTMENT_ID);
        return new MongoEmployee(id, firstName, lastName, salary, departmentId);
    }
}
