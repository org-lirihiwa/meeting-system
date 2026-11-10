package lirihiwa.dev.employee.dto;

public record UpdateEmployeeRequest(
        String firstName,
        String lastName,
        String patronymic,
        String email,
        String position,
        String organization,
        String territory,
        String scope,
        boolean active
) {
}
