package servicios;

import interfaces.IGestorReservas;
import modelos.DatosStatic;
import modelos.Reserva;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.*;

public class GestorReservas implements IGestorReservas {
    private List<Reserva> reservas;
    private DatosStatic datos;
    private final Path reservasPath = Paths.get("reservas.json");
    private final Gson gson = new Gson();

    public GestorReservas(DatosStatic datos) {
        this.datos = datos;
        this.reservas = new ArrayList<>();
        loadJson();
    }
    
    // Carga del JSON de reservas
    private void loadJson() {
        try {
            if (Files.exists(reservasPath)) {
                String json = Files.readString(reservasPath);
                Type t = new TypeToken<List<Reserva>>() {}.getType();
                List<Reserva> lista = gson.fromJson(json, t);
                if (lista != null) reservas = lista;
                // asegurar IDs válidos
                for (Reserva r : reservas) r.getId();
            }
        } catch (Exception e) {
            System.out.println("Error leyendo reservas.json: " + e.getMessage());
        }
    }

    // Guardado del JSON
    private void saveJson() {
        try {
            try (Writer w = Files.newBufferedWriter(reservasPath)) {
                gson.toJson(reservas, w);
            }
        } catch (IOException e) {
            System.out.println("Error guardando reservas.json: " + e.getMessage());
        }
    }

    @Override
    public void agregarReserva(Reserva r) {
        r.getId(); // asegurar id
        reservas.add(r);
        saveJson();
        System.out.println("Reserva agregada correctamente.");
    }

    @Override
    public void eliminarReserva(int indice) {
        if (indice >= 0 && indice < reservas.size()) {
            reservas.remove(indice);
            saveJson();
            System.out.println("Reserva eliminada correctamente.");
        } else {
            System.out.println("Indice invalido.");
        }
    }

    @Override
    public boolean eliminarReservaPorId(String id) {
        Optional<Reserva> opt = reservas.stream().filter(x -> x.getId().equals(id)).findFirst();
        if (opt.isPresent()) {
            reservas.remove(opt.get());
            saveJson();
            return true;
        }
        return false;
    }

    @Override
    public void mostrarReservas() {
        if (reservas.isEmpty()) {
            System.out.println("No hay reservas.");
            return;
        }
        for (int i = 0; i < reservas.size(); i++) {
            System.out.println(i + ". " + reservas.get(i));
        }
    }

    // Fusion CSV -> JSON: mantiene UUID si ya existe, añade si no existe
    @Override
    // Principalemente una alternativa, no fue usada finalmente
    public void cargarDatos(String ruta) throws IOException {

        List<Reserva> reservasCSV = LectorCSV.cargarReservas(ruta);

        int nuevas = 0;

        for (Reserva rCSV : reservasCSV) {

            boolean existe = reservas.stream().anyMatch(r ->
                    r.getPersona().getRut().equals(rCSV.getPersona().getRut()) &&
                    r.getEdificio().equals(rCSV.getEdificio()) &&
                    r.getSala().equals(rCSV.getSala()) &&
                    r.getFecha().equals(rCSV.getFecha()) &&
                    r.getHoraInicio().equals(rCSV.getHoraInicio())
            );

            if (!existe) {
                reservas.add(rCSV);
                nuevas++;
            }
        }

        if (nuevas > 0) {
            saveJson();
            System.out.println("Reservas nuevas desde CSV: " + nuevas);
        } else {
            System.out.println("CSV no tenía reservas nuevas.");
        }
    }

        private boolean equalsNullSafe(String a, String b) {
            if (a == null && b == null) return true;
            if (a == null || b == null) return false;
            return a.equals(b);
        }

        private String normalizarHora(String h) {
            if (h == null) return null;
            String s = h.trim();
            if (s.length() == 4 && s.charAt(1) == ':') s = "0" + s;
            return s;
        }

        @Override
        public List<Reserva> getReservas() {
            return reservas;
        }

        // por si necesitas acceder a DatosStatic desde fuera
        public DatosStatic getDatos() {
            return datos;
        }
    }
