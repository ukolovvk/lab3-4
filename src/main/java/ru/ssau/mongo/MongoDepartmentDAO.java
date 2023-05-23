package ru.ssau.mongo;

import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import ru.ssau.mongo.model.MongoDepartment;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MongoDepartmentDAO {

    private static final String DEPARTMENTS = "departments";
    private static final String ID = "_id";
    private static final String TITLE = "title";
    private static final String ADDRESS = "address";

    public void insertDepartment(MongoDepartment department) {
        try (var mongoClient = MongoClients.create(MongoConstants.URL)) {
            var database = mongoClient.getDatabase(MongoConstants.DATABASE);
            MongoCollection<Document> departmentsCollection = database.getCollection(DEPARTMENTS);
            Document document = new Document();
            document.append(TITLE, department.getTitle());
            document.append(ADDRESS, department.getTitle());
            departmentsCollection.insertOne(document);
            ObjectId insertedId = (ObjectId) document.get(ID);
            department.setId(insertedId);
        } catch (MongoException ex) {
            throw new RuntimeException("Failed to insert employee", ex);
        }
    }

    public void removeDepartmentById(ObjectId id) {
        try (var mongoClient = MongoClients.create(MongoConstants.URL)) {
            var database = mongoClient.getDatabase(MongoConstants.DATABASE);
            MongoCollection<Document> departmentsCollection = database.getCollection(DEPARTMENTS);
            Bson filter = Filters.eq(ID, id);
            departmentsCollection.deleteOne(filter);
        } catch (MongoException ex) {
            ex.printStackTrace();
        }
    }

    public List<MongoDepartment> getAllDepartments() {
        try (var mongoClient = MongoClients.create(MongoConstants.URL)) {
            var database = mongoClient.getDatabase(MongoConstants.DATABASE);
            MongoCollection<Document> employeesCollection = database.getCollection(DEPARTMENTS);
            FindIterable<Document> documents = employeesCollection.find();
            List<MongoDepartment> result = new ArrayList<>();
            documents.forEach((Consumer<? super Document>) doc -> result.add(getDepartmentFromDocument(doc)));
            return result;
        } catch (MongoException ex) {
            throw new RuntimeException("Failed to get all employees", ex);
        }
    }

    private MongoDepartment getDepartmentFromDocument(Document document) {
        ObjectId id = document.getObjectId(ID);
        String title = document.getString(TITLE);
        String address = document.getString(ADDRESS);
        return new MongoDepartment(id, title, address);
    }

    public void removeAllDepartments() {
        try (var mongoClient = MongoClients.create(MongoConstants.URL)) {
            var database = mongoClient.getDatabase(MongoConstants.DATABASE);
            MongoCollection<Document> departmentsCollection = database.getCollection(DEPARTMENTS);
            departmentsCollection.deleteMany(new Document());
        }
    }

    public void updateDepratment(MongoDepartment department) {
        try (var mongoClient = MongoClients.create(MongoConstants.URL)) {
            var database = mongoClient.getDatabase(MongoConstants.DATABASE);
            MongoCollection<Document> departmentsCollection = database.getCollection(DEPARTMENTS);
            Document query = new Document().append(ID, department.getId());
            Bson updates = Updates.combine(
                    Updates.set(TITLE, department.getTitle()),
                    Updates.set(ADDRESS, department.getAddress())
            );
            departmentsCollection.updateOne(query, updates);
        } catch (MongoException ex) {
            ex.printStackTrace();
        }
    }
}
