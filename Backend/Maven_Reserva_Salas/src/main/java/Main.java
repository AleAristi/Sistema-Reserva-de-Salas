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

        System.out.println("=== INICIANDO BACKEND RESERVA DE SALAS ===");

        // ---------- Ruta BASE ----------
        String base = System.getProperty("user.dir");
        System.out.println("Working dir = " + base);

        // ---------- Rutas dinámicas ----------
        Path datosStaticPath = Paths.get(base, "..", "..", "Generador", "Datos", "DatosStatic.csv");
        Path reservasCsvPath = Paths.get(base, "..", "..", "Generador", "Datos", "Reservas.csv");

        System.out.println("Ruta DatosStatic = " + datosStaticPath.toAbsolutePath());
        System.out.println("Ruta Reservas = " + reservasCsvPath.toAbsolutePath());

        // ---------- Validación archivos ----------
        if (!Files.exists(datosStaticPath)) {
            System.out.println("ERROR: No se encontró DatosStatic.csv");
            return;
        }

        if (!Files.exists(reservasCsvPath)) {
            System.out.println("ADVERTENCIA: No existe Reservas.csv (se cargará vacío).");
        }

        // ---------- Cargar DatosStatic ----------
        DatosStatic datosStatic = LectorCSV.cargarDatosStatic(datosStaticPath.toString());

        // ---------- Crear Gestor ----------
        GestorReservas gestor = new GestorReservas(datosStatic);

        // ---------- Cargar CSV si existe ----------
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

        // ---------- Iniciar servicios ----------
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

        // ---------- Mantener proceso vivo (FIX del exit 100) ----------
        try {
            System.out.println("Servidor ejecutándose... CTRL+C para detener.");
            Thread.currentThread().join();
        } catch (InterruptedException ex) {
            System.out.println("Backend detenido.");
        }
    }
}
