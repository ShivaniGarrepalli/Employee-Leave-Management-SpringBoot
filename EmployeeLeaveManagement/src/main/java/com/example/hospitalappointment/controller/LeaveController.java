package com.example.hospitalappointment.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.hospitalappointment.model.LeaveApplication;
import com.example.hospitalappointment.model.LeaveStatus;
import com.example.hospitalappointment.service.LeaveService;

@RestController
@RequestMapping("/api/leaves")
public class LeaveController {

    private final LeaveService service;

    public LeaveController(LeaveService service) {
        this.service = service;
    }

    @PostMapping("/apply/{employeeId}")
    public ResponseEntity<LeaveApplication> applyLeave(
            @PathVariable Long employeeId,
            @RequestBody LeaveApplication leave) {

        return new ResponseEntity<>(
                service.applyLeave(employeeId, leave),
                HttpStatus.CREATED);
    }

    @GetMapping
    public List<LeaveApplication> getAllLeaves() {
        return service.getAllLeaves();
    }

    @GetMapping("/{id}")
    public LeaveApplication getLeaveById(@PathVariable Long id) {
        return service.getLeaveById(id);
    }

    @GetMapping("/employee/{employeeId}")
    public List<LeaveApplication> getLeavesByEmployee(
            @PathVariable Long employeeId) {
        return service.getLeavesByEmployee(employeeId);
    }

    @GetMapping("/status/{status}")
    public List<LeaveApplication> getLeavesByStatus(
            @PathVariable LeaveStatus status) {
        return service.getLeavesByStatus(status);
    }

    @PutMapping("/{id}/approve")
    public LeaveApplication approveLeave(
            @PathVariable Long id,
            @RequestParam(defaultValue = "") String comment) {
        return service.approveLeave(id, comment);
    }

    @PutMapping("/{id}/reject")
    public LeaveApplication rejectLeave(
            @PathVariable Long id,
            @RequestParam(defaultValue = "") String comment) {
        return service.rejectLeave(id, comment);
    }

    @PutMapping("/{id}/cancel")
    public LeaveApplication cancelLeave(@PathVariable Long id) {
        return service.cancelLeave(id);
    }
}