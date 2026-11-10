package lirihiwa.dev.controller;

import lirihiwa.dev.employee.Employee;
import lirihiwa.dev.employee.dto.CreateEmployeeRequest;
import lirihiwa.dev.employee.dto.UpdateEmployeeRequest;
import lirihiwa.dev.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Employee create(@RequestBody CreateEmployeeRequest request) {
        return employeeService.create(request);
    }

    @GetMapping
    public List<Employee> getAll() {
        return employeeService.findAll();
    }

    @GetMapping("/{id}")
    public Employee getById(@PathVariable Long id) {
        return employeeService.findById(id);
    }

    @PutMapping("/{id}")
    public Employee update(@PathVariable Long id, @RequestBody UpdateEmployeeRequest request) {
        return employeeService.update(id, request);
    }
}
