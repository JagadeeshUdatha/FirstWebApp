package DAO;

import Entity.Employee;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import com.google.gson.Gson;
import java.io.PrintWriter;
import java.util.*;
@WebServlet("/webApp/*")
public class ServletProcess extends HttpServlet {
    private EmployeeDAO employeeDAO = new DAOImplementation();


@Override
protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    try {
        BufferedReader reader = req.getReader();
        StringBuilder jsonBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonBuilder.append(line);
        }
        String jsonData = jsonBuilder.toString();
        System.out.println(jsonData);
        Gson gson = new Gson();
        Employee employee = gson.fromJson(jsonData, Employee.class);
        Employee existingEmployeeId = null;
        try {
            existingEmployeeId = employeeDAO.getEmployee(employee.getId());
            System.out.println(existingEmployeeId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(existingEmployeeId != null) {
            System.out.println("Employee ID already exists");
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500 Internal Server Error
            sendErrorResponse(resp, "EmployeeID already Exists");
        } else {
            employeeDAO.createEmployee(employee);
            resp.setStatus(HttpServletResponse.SC_CREATED); // 201 Created
            resp.setContentType("application/json");
            String jsonResponse = gson.toJson("Employee has been successfully created.");
            PrintWriter out = resp.getWriter();
            out.print(jsonResponse);
        }


    } catch (IOException e) {
        // Handle IOException
        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        sendErrorResponse(resp, "An error occurred while reading the request body.");
    } catch (IllegalArgumentException e) {
        // Handle IllegalArgumentException
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request
        sendErrorResponse(resp, e.getMessage());
    } catch (Exception e) {
        // Handle other exceptions
        e.printStackTrace();
        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        sendErrorResponse(resp, "An error occurred while creating the employee.");
    }
}

    private void sendErrorResponse(HttpServletResponse resp, String errorMessage) throws IOException {
        resp.setContentType("application/json");
        Gson gson = new Gson();
        String errorResponse = gson.toJson(errorMessage);
        PrintWriter out = resp.getWriter();
        out.print(errorResponse);
    }


    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestUri = req.getRequestURI();
        System.out.println(requestUri);

        String[] uriParts = requestUri.split("/");
        System.out.println(uriParts.length);
        if (uriParts.length == 3) {
            String id = uriParts[2];
            System.out.println("ID is"+id);
            Employee employee = employeeDAO.getEmployee(Integer.parseInt(id));
            if (employee != null) {
                resp.setContentType("application/json");
                PrintWriter out = resp.getWriter();
                out.print(employeeToJson(employee));
                out.flush();
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } else { // For requests like "/employee"
            List<Employee> employees = employeeDAO.getAllEmployees();
            resp.setContentType("application/json");
            PrintWriter out = resp.getWriter();
            out.print(employeesToJson(employees));
            out.flush();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String URI = req.getRequestURI();
        String[] uriParts = URI.split("/");
        Gson gson = new Gson();
        if(uriParts.length == 3) {
            String id = uriParts[2];

            try {
                Employee employee = employeeDAO.getEmployee(Integer.parseInt(id));
                if(employee != null) {
                    employeeDAO.deleteEmployee(Integer.parseInt(id));
                    resp.setContentType("application/json");
                    String jsonResponse = gson.toJson("Data has been deleted Succesfully");
                    PrintWriter out = resp.getWriter();
                    out.print(jsonResponse);
                } else {
                    resp.setContentType("application/json");
                    String jsonResponse = gson.toJson("No matching id to delete the data");
                    PrintWriter out = resp.getWriter();
                    out.print(jsonResponse);
                }
            } catch(Exception e) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                sendErrorResponse(resp, "Found Error While Deleting the data");
            }


        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            sendErrorResponse(resp, "Cannot Delete data without the userID");
        }
    }

    private String employeeToJson(Employee employee) {
        Gson gson = new Gson();
        return gson.toJson(employee);
    }

    public static String employeesToJson(List<Employee> employees) {
        Gson gson = new Gson();
        return gson.toJson(employees);
    }

}