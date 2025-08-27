package com.creditcard.controller;

import com.creditcard.model.Employee;
import com.creditcard.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;      
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
	private static final Logger log = LoggerFactory.getLogger(EmployeeController.class);

 @Autowired
 private EmployeeService employeeService;

 @GetMapping
 public List<Employee> getAllEmployees() {
   log.info("Listing all employees");
   List<Employee> list = employeeService.getAllEmployees();
   log.info("Returned {} employees", list.size());
   return list;
 }


 @GetMapping("/{id}")
 public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
   log.info("Fetching employee by id={}", id);
   Optional<Employee> employee = employeeService.getEmployeeById(id);
   if (employee.isPresent()) {
     log.info("Employee found id={}", id);
     return ResponseEntity.ok(employee.get());
   } else {
     log.warn("Employee not found id={}", id);
     return ResponseEntity.notFound().build();
   }
 }

 @GetMapping("/firstname/{firstName}")
 public ResponseEntity<List<Employee>> getEmployeesByFirstName(@PathVariable String firstName) {
   log.info("Searching employees by firstName='{}'", firstName);
   List<Employee> employees = employeeService.getEmployeesByFirstName(firstName);
   if (!employees.isEmpty()) {
     log.info("Found {} employees for firstName='{}'", employees.size(), firstName);
     return ResponseEntity.ok(employees);
   } else {
     log.warn("No employees found for firstName='{}'", firstName);
     return ResponseEntity.notFound().build();
   }
 }


 @PostMapping
 public Employee createEmployee(@RequestBody Employee employee) {
   log.info("Creating employee email='{}'", employee.getEmail());
   Employee saved = employeeService.saveEmployee(employee);
   log.info("Created employee id={}", saved.getId());
   return saved;
 }

 @PutMapping("/{id}")
 public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employeeDetails) {
   log.info("Updating employee id={}", id);
   Optional<Employee> employee = employeeService.getEmployeeById(id);
   if (employee.isPresent()) {
     Employee updatedEmployee = employee.get();
     updatedEmployee.setFirstName(employeeDetails.getFirstName());
     updatedEmployee.setLastName(employeeDetails.getLastName());
     updatedEmployee.setEmail(employeeDetails.getEmail());
     employeeService.saveEmployee(updatedEmployee);
     log.info("Updated employee id={}", id);
     return ResponseEntity.ok(updatedEmployee);
   } else {
     log.warn("Cannot update; employee not found id={}", id);
     return ResponseEntity.notFound().build();
   }
 }

 @DeleteMapping("/{id}")
 public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
   log.info("Deleting employee id={}", id);
   Optional<Employee> employee = employeeService.getEmployeeById(id);
   if (employee.isPresent()) {
     employeeService.deleteEmployee(id);
     log.info("Deleted employee id={}", id);
     return ResponseEntity.noContent().build();
   } else {
     log.warn("Cannot delete; employee not found id={}", id);
     return ResponseEntity.notFound().build();
   }

 }
}