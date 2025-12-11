package servicios;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import modelos.Reserva;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class JSONWriter {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void guardarReservasJSON(String ruta, List<Reserva> reservas) {
        try (FileWriter writer = new FileWriter(ruta)) {
            gson.toJson(reservas, writer);
            System.out.println("Reservas exportadas correctamente a JSON.");
        } catch (IOException e) {
            System.out.println("Error al guardar JSON: " + e.getMessage());
        }
    }
}
