package com.example.hospitalappointment.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.hospitalappointment.dao.EmployeeDAO;
import com.example.hospitalappointment.exception.EmployeeNotFoundException;
import com.example.hospitalappointment.exception.InvalidLeaveException;
import com.example.hospitalappointment.model.Employee;

@Service
public class EmployeeService {

    private final EmployeeDAO dao;

    public EmployeeService(EmployeeDAO dao) {
        this.dao = dao;
    }

    public Employee addEmployee(Employee employee) {
        if (employee == null) {
            throw new InvalidLeaveException("Employee details are required");
        }

        if (dao.existsByEmail(employee.getEmail())) {
            throw new InvalidLeaveException("Email already exists");
        }

        return dao.save(employee);
    }

    public List<Employee> getAllEmployees() {
        return dao.findAll();
    }

    public Employee getEmployeeById(Long id) {
        Employee employee = dao.findById(id);

        if (employee == null) {
            throw new EmployeeNotFoundException("Employee not found with ID: " + id);
        }

        return employee;
    }

    public Employee updateEmployee(Long id, Employee updatedEmployee) {
        if (updatedEmployee == null) {
            throw new InvalidLeaveException("Employee details are required");
        }

        Employee employee = getEmployeeById(id);

        if (updatedEmployee.getEmail() != null
                && updatedEmployee.getEmail().equals(employee.getEmail()) == false) {

            Employee existingEmployee = dao.findByEmail(updatedEmployee.getEmail());

            if (existingEmployee != null) {
                throw new InvalidLeaveException("Email already exists");
            }
        }

        if (updatedEmployee.getEmployeeName() != null) {
            employee.setEmployeeName(updatedEmployee.getEmployeeName());
        }

        if (updatedEmployee.getEmail() != null) {
            employee.setEmail(updatedEmployee.getEmail());
        }

        return dao.save(employee);
    }

    public void deleteEmployee(Long id) {
        Employee employee = getEmployeeById(id);
        dao.delete(employee);
    }
}