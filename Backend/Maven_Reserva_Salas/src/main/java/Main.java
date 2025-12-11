import modelos.DatosStatic;
import servicios.GestorReservas;
import servicios.LectorCSV;
import servicios.RestServerSpark;
import servicios.AuthService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {

        System.out.println("Backend MiSalaUDD");

        // Ruta donde se encuentra actualmente en el entorno operado
        String base = System.getProperty("user.dir");
        System.out.println("Working dir = " + base);

        // Rutas dinamicas para los archivos generados en el generador
        Path datosStaticPath = Paths.get(base, "..", "..", "Generador", "Datos", "DatosStatic.csv");
        Path reservasCsvPath = Paths.get(base, "..", "..", "Generador", "Datos", "Reservas.csv");

        System.out.println("Ruta DatosStatic = " + datosStaticPath.toAbsolutePath());
        System.out.println("Ruta Reservas = " + reservasCsvPath.toAbsolutePath());

        // Validaciones
        if (!Files.exists(datosStaticPath)) {
            System.out.println("ERROR: No se encontró DatosStatic.csv");
            return;
        }

        if (!Files.exists(reservasCsvPath)) {
            System.out.println("ADVERTENCIA: No existe Reservas.csv (se cargará vacío).");
        }

        // Carga de datos permanentes
        DatosStatic datosStatic = LectorCSV.cargarDatosStatic(datosStaticPath.toString());

        // Nuevo manejo de reservas
        GestorReservas gestor = new GestorReservas(datosStatic);

        // Respuesta del manejo de reservas
        try {
            if (Files.exists(reservasCsvPath)) {
                gestor.cargarDatos(reservasCsvPath.toString());
                System.out.println("Reservas CSV cargadas correctamente.");
            } else {
                System.out.println("No hay CSV de reservas para cargar.");
            }
        } catch (Exception e) {
            System.out.println("Error cargando CSV de reservas: " + e.getMessage());
        }

        // Inicio del servidor local
        AuthService auth = new AuthService();

        try {
            RestServerSpark server = new RestServerSpark(gestor, auth, datosStatic);
            server.start(4567);

            System.out.println("REST API iniciada en: http://localhost:4567/api/");
            System.out.println("Backend listo para recibir requests desde WPF.");
        } catch (Exception e) {
            System.out.println("ERROR iniciando Spark: " + e.getMessage());
            e.printStackTrace();
        }

        // Evitar cierre automatico del servidor local
        try {
            System.out.println("Servidor ejecutándose...");
            Thread.currentThread().join();
        } catch (InterruptedException ex) {
            System.out.println("Backend detenido.");
        }
    }
}
