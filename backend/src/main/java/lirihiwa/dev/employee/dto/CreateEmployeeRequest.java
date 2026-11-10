package lirihiwa.dev.employee.dto;

public record CreateEmployeeRequest(
        String firstName,
        String lastName,
        String patronymic,
        String email,
        String position,
        String organization,
        String territory,
        String scope
) {
}
