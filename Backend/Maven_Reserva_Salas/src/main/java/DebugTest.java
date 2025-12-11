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

        System.out.println("Debug de rutas, reservas, carga de archivos, union de bases de datos y funiconalidad de JSON");

        // Mostrar la ruta del entorno actual
        String base = System.getProperty("user.dir");
        System.out.println("Working dir = " + base);

        // Mostrar ruta dinamica que se espera
        Path reservasCsvPath = Paths.get(base, "..", "..", "Generador", "Datos", "Reservas.csv");
        System.out.println("Ruta esperada CSV = " + reservasCsvPath.toAbsolutePath());

        // Cargar datos estáticos
        DatosStatic datos = new DatosStatic();

        // Crear autenticacion para facilitar los tokens
        AuthService auth = new AuthService();

        // Crear el gestor que maneja JSON + CSV
        GestorReservas gestor = new GestorReservas(datos);

        // Mostrar si el json existe
        System.out.println("\n=== JSON ACTUAL ===");
        gestor.mostrarReservas();

        // Probar lectura de CSV
        System.out.println("\n=== Intentando cargar CSV ===");

        try {
            gestor.cargarDatos(reservasCsvPath.toString());
            System.out.println("CSV cargado correctamente desde:");
            System.out.println("   " + reservasCsvPath.toAbsolutePath());
        } catch (Exception e) {
            System.out.println("ERROR cargando CSV: " + e.getMessage());
        }

        // Probar funcionalidad de reservas
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

        // Probar funcionalidad de eliminar reservas y borrar el intento de prueba de paso
        System.out.println("\n=== Intentando eliminar reserva por ID ===");
        boolean eliminado = gestor.eliminarReservaPorId(nueva.getId());
        System.out.println("Resultado eliminar: " + eliminado);

        System.out.println("\nReservas después de eliminar:");
        gestor.mostrarReservas();

    }
}
