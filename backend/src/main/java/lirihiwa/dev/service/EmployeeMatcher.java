package lirihiwa.dev.service;

import lirihiwa.dev.employee.Employee;
import lirihiwa.dev.employee.EmployeeRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component
public class EmployeeMatcher {

    private final EmployeeRepository employeeRepository;

    public EmployeeMatcher(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    /**
     * Возвращает кандидатов на роль ответственного.
     * - имя не названо: пустой список (никого не угадываем);
     * - есть совпадения по имени: только они;
     * - совпадений нет (например, "Маша" вместо "Мария"): все активные сотрудники,
     * чтобы LLM могла разобрать краткие формы по контексту.
     */
    public List<Employee> findCandidates(String rawAssignee) {
        if (rawAssignee == null || rawAssignee.isBlank()) {
            return List.of();
        }
        String[] words = rawAssignee.trim().split("\\s+");
        List<Employee> active = employeeRepository.findByActiveTrue();

        List<Employee> byName = active.stream()
                .filter(employee -> matchesAll(employee, words))
                .toList();

        return byName.isEmpty() ? active : byName;
    }

    private boolean matchesAll(Employee employee, String[] words) {
        for (String word : words) {
            if (!sameWord(employee.getFirstName(), word)
                    && !sameWord(employee.getLastName(), word)
                    && !sameWord(employee.getPatronymic(), word)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Учитывает падежные окончания: "Ивану" ~ "Иван", "Маше" ~ "Маша".
     */
    private boolean sameWord(String name, String word) {
        if (name == null || word == null) {
            return false;
        }
        String a = name.toLowerCase(Locale.ROOT).replace('ё', 'е');
        String b = word.toLowerCase(Locale.ROOT).replace('ё', 'е');
        if (a.equals(b)) {
            return true;
        }
        int min = Math.min(a.length(), b.length());
        if (min < 3 || Math.abs(a.length() - b.length()) > 2) {
            return false;
        }
        int prefix = 0;
        while (prefix < min && a.charAt(prefix) == b.charAt(prefix)) {
            prefix++;
        }
        return prefix >= min - 1;
    }
}
