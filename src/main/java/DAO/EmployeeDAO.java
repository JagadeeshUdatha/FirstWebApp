package DAO;
import Entity.Employee;
import java.util.*;
public interface EmployeeDAO {
    void createEmployee(Employee employee);
    Employee getEmployee(int id);
    List<Employee> getAllEmployees();
    void updateEmployee(int id, Employee employee);

    void deleteEmployee(int id);
}
