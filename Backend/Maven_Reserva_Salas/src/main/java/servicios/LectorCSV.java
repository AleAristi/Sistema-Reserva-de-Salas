package servicios;

import modelos.DatosStatic;
import modelos.Reserva;
import modelos.Alumno;
import modelos.Profesor;
import modelos.Persona;
import java.io.*;
import java.util.*;

public class LectorCSV {

    public static DatosStatic cargarDatosStatic(String ruta) {
        DatosStatic datos = new DatosStatic();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(ruta), "UTF-8"))) {
            String linea = br.readLine(); // cabecera
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(",", -1);
                if (partes.length < 4) continue;
                if (!partes[0].isEmpty()) datos.agregarCarrera(partes[0].trim());
                if (!partes[1].isEmpty()) datos.agregarFuncion(partes[1].trim());
                if (!partes[2].isEmpty() && !datos.getEdificios().contains(partes[2].trim()))
                    datos.agregarEdificio(partes[2].trim());
                if (!partes[3].isEmpty() && !datos.getSalas().contains(partes[3].trim()))
                    datos.agregarSala(partes[3].trim());
            }
        } catch (IOException e) {
            System.out.println("Error al leer DatosStatic.csv: " + e.getMessage());
        }
        return datos;
    }

    public static List<Reserva> cargarReservas(String ruta) {
        List<Reserva> reservas = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(ruta), "UTF-8"))) {
            String linea = br.readLine(); // cabecera
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(",", -1);
                if (partes.length < 9) continue;
                String tipo = partes[0].trim();
                String nombre = partes[1].trim();
                String rut = partes[2].trim();
                String funcionOCarrera = partes[3].trim();
                String edificio = partes[4].trim();
                String sala = partes[5].trim();
                String fecha = partes[6].trim();
                String hInicio = partes[7].trim();
                String hFin = partes[8].trim();

                // normalizar hora inicio pequeño ajuste
                if (hInicio.length() == 4 && hInicio.charAt(1) == ':') hInicio = "0" + hInicio;
                if (hFin.length() == 4 && hFin.charAt(1) == ':') hFin = "0" + hFin;

                Persona persona = tipo.equalsIgnoreCase("Alumno")
                        ? new Alumno(nombre, rut, funcionOCarrera)
                        : new Profesor(nombre, rut, funcionOCarrera);

                reservas.add(new Reserva(persona, edificio, sala, fecha, hInicio, hFin));
            }
        } catch (IOException e) {
            System.out.println("Error al leer Reservas.csv: " + e.getMessage());
        }
        return reservas;
    }
}
