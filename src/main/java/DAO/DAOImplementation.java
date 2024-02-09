package DAO;

import Entity.Employee;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class DAOImplementation implements EmployeeDAO{
    private final MongoCollection<Document> collection;

    public DAOImplementation() {
        try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            MongoDatabase db = mongoClient.getDatabase("employeeDBServlet");
            this.collection = db.getCollection("employeeDBCollection");
            System.out.println("Mongo Connection Done");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to connect to MongoDB.");
        }
    }

    @Override
    public void createEmployee(Employee employee) {
        Document doc = new Document("name", employee.getName())
                .append("company", employee.getCompany())
                .append("department", employee.getDepartment())
                .append("salary", employee.getSalary())
                .append("id", employee.getId())
                .append("role", employee.getRole())
                .append("gender", employee.getGender())
                .append("address", employee.getAddress());
        collection.insertOne(doc);
        System.out.println("The Employee is Created");
    }

    @Override
    public Employee getEmployee(int id) {
        Document query = new Document("id", id);
        Document doc = collection.find(query).first();
        return documentToEmployee(doc);
    }

    private Employee documentToEmployee(Document doc) {
        Employee employee = new Employee();
        employee.setId(doc.getInteger("id"));
        employee.setName(doc.getString("name"));
        employee.setAddress(doc.getString("address"));
        employee.setDepartment(doc.getString("department"));
        employee.setGender(doc.getString("gender"));
        employee.setSalary(doc.getDouble("salary"));
        employee.setRole(doc.getString("role"));
        employee.setCompany(doc.getString("company"));
        return employee;
    }

    @Override
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        for (Document doc : collection.find()) {
            employees.add(documentToEmployee(doc));
        }
        return employees;
    }

    @Override
    public void updateEmployee(int id, Employee employee) {
        Document filter = new Document("id", id);
        Document updateDoc = new Document("$set", new Document()
                .append("name", employee.getName())
                .append("company", employee.getCompany())
                .append("address", employee.getAddress())
                .append("department", employee.getDepartment())
                .append("gender", employee.getGender())
                .append("salary", employee.getSalary())
                .append("role", employee.getRole()));

        collection.updateOne(filter, updateDoc);
    }

    @Override
    public void deleteEmployee(int id) {
        collection.deleteOne(new Document("id", id));

    }


}
