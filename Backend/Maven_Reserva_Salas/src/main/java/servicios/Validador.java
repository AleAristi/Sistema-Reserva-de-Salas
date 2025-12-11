/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicios;


// Clase usada previo al frontend para validar inputs

public class Validador {

    public static boolean validarNombre(String nombre) {
        return nombre.matches("[A-Za-z ]+");
    }

    public static boolean validarRUT(String rut) {
        return rut.matches("\\d{7,8}-[\\dkK]");
    }
}
