package com.example.hospitalappointment.dao;

import java.util.List;

import com.example.hospitalappointment.model.Employee;
import com.example.hospitalappointment.model.LeaveApplication;
import com.example.hospitalappointment.model.LeaveStatus;

public interface LeaveDAO {

    LeaveApplication save(LeaveApplication leave);

    LeaveApplication findById(Long id);

    List<LeaveApplication> findAll();

    List<LeaveApplication> findByEmployee(Employee employee);

    List<LeaveApplication> findByStatus(LeaveStatus status);

    List<LeaveApplication> findByEmployeeEmployeeId(Long employeeId);

    boolean existsByEmployeeEmployeeIdAndStatus(Long employeeId, LeaveStatus status);

    void delete(LeaveApplication leave);
}