package com.example.hospitalappointment.dao;

import java.util.List;
import com.example.hospitalappointment.model.Employee;

public interface EmployeeDAO {

    Employee save(Employee employee);

    Employee findById(Long id);

    Employee findByEmail(String email);

    boolean existsByEmail(String email);

    List<Employee> findAll();

    void delete(Employee employee);
}