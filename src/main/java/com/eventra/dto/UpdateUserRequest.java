package com.eventra.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) untuk permintaan pembaruan data profil pengguna.
 * DTO ini hanya mencakup field yang diizinkan untuk diubah oleh pengguna 
 * melalui endpoint PUT /users/{userId}.
 * * Field sensitif seperti 'password', 'email', 'nik', dan 'role' 
 * DIKECUALIKAN untuk alasan keamanan dan integritas data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    /**
     * Nama lengkap pengguna.
     */
    @NotBlank(message = "Nama lengkap tidak boleh kosong")
    @Size(min = 3, max = 100, message = "Nama lengkap harus antara 3 dan 100 karakter")
    private String fullName;

    /**
     * Nomor telepon pengguna.
     * Diharapkan menerima format lengkap dengan kode negara, contoh: +62812...
     * Validasi size disesuaikan untuk mengakomodasi prefix '+62'.
     */
    @NotBlank(message = "Nomor telepon tidak boleh kosong")
    @Size(min = 12, max = 16, message = "Nomor telepon harus valid, termasuk kode negara") 
    private String phone;

    /**
     * Jenis kelamin pengguna.
     * Diharapkan nilainya adalah nilai yang diterima di backend (misal: 'male' atau 'female').
     */
    @NotBlank(message = "Jenis kelamin tidak boleh kosong")
    private String gender;

    /**
     * Nilai wallet. 
     * Diberi anotasi @NotNull.
     */
    @NotNull(message = "Nilai wallet tidak boleh kosong")
    private Integer wallet;
    
    // Field yang dikecualikan (tidak boleh diubah):
    private String password;
    private String email;
    private String nik;
    private String role;
}