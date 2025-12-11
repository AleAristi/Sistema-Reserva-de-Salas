import servicios.GestorReservas;
import servicios.RestServerSpark;
import servicios.AuthService;
import modelos.DatosStatic;
import modelos.Reserva;
import modelos.Persona;
import modelos.Alumno;

import java.nio.file.Path;
import java.nio.file.Paths;

public class DebugTest {

    public static void main(String[] args) {

        System.out.println("=== DEBUG RESERVAS ===");

        // ---------- Ruta BASE ----------
        String base = System.getProperty("user.dir");
        System.out.println("Working dir = " + base);

        // ---------- Ruta dinámica del CSV ----------
        Path reservasCsvPath = Paths.get(base, "..", "..", "Generador", "Datos", "Reservas.csv");
        System.out.println("Ruta esperada CSV = " + reservasCsvPath.toAbsolutePath());

        // Cargar datos estáticos
        DatosStatic datos = new DatosStatic();

        // Crear servicio autenticación
        AuthService auth = new AuthService();

        // Crear el gestor que maneja JSON + CSV
        GestorReservas gestor = new GestorReservas(datos);

        // -----------------------------
        // Mostrar contenido JSON si existe
        // -----------------------------
        System.out.println("\n=== JSON ACTUAL ===");
        gestor.mostrarReservas();

        // -----------------------------
        // Prueba lectura CSV
        // -----------------------------
        System.out.println("\n=== Intentando cargar CSV ===");

        try {
            gestor.cargarDatos(reservasCsvPath.toString());
            System.out.println("CSV cargado correctamente desde:");
            System.out.println("   " + reservasCsvPath.toAbsolutePath());
        } catch (Exception e) {
            System.out.println("ERROR cargando CSV: " + e.getMessage());
        }

        // -----------------------------
        // Crear una reserva de prueba
        // -----------------------------
        System.out.println("\n=== Agregando reserva de prueba ===");

        Persona alumnoTest = new Alumno("Test Alumno", "11111111-1", "Ingeniería");

        Reserva nueva = new Reserva(
                alumnoTest,
                "Edificio A",
                "Sala 101",
                "2025-12-10",
                "10:00",
                "11:00"
        );

        gestor.agregarReserva(nueva);

        System.out.println("\nReservas después de agregar:");
        gestor.mostrarReservas();

        // -----------------------------
        // Eliminar reserva recién creada
        // -----------------------------
        System.out.println("\n=== Intentando eliminar reserva por ID ===");
        boolean eliminado = gestor.eliminarReservaPorId(nueva.getId());
        System.out.println("Resultado eliminar: " + eliminado);

        System.out.println("\nReservas después de eliminar:");
        gestor.mostrarReservas();

    }
}
