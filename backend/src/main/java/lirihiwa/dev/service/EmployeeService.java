package lirihiwa.dev.service;

import lirihiwa.dev.employee.Employee;
import lirihiwa.dev.employee.EmployeeRepository;
import lirihiwa.dev.employee.dto.CreateEmployeeRequest;
import lirihiwa.dev.employee.dto.UpdateEmployeeRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public Employee create(CreateEmployeeRequest request) {
        Employee employee = new Employee();
        employee.setFirstName(request.firstName());
        employee.setLastName(request.lastName());
        employee.setPatronymic(request.patronymic());
        employee.setEmail(request.email());
        employee.setPosition(request.position());
        employee.setOrganization(request.organization());
        employee.setTerritory(request.territory());
        employee.setScope(request.scope());
        return employeeRepository.save(employee);
    }

    @Transactional(readOnly = true)
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Employee findById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Employee not found"
                ));
    }

    @Transactional
    public Employee update(Long id, UpdateEmployeeRequest request) {
        Employee employee = findById(id);
        employee.setFirstName(request.firstName());
        employee.setLastName(request.lastName());
        employee.setPatronymic(request.patronymic());
        employee.setEmail(request.email());
        employee.setPosition(request.position());
        employee.setOrganization(request.organization());
        employee.setTerritory(request.territory());
        employee.setScope(request.scope());
        employee.setActive(request.active());
        return employee;
    }
}
