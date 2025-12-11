package servicios;

import modelos.*;
import java.util.*;
import java.io.*;

// Escritura y modificacion de csv para armar una reserva
public class CSVWriter {
    public static void guardarReservas(String ruta, List<Reserva> reservas){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(ruta))){
            bw.write("TipoPersona,Nombre,RUT,FuncionOCarrera,Edificio,Sala,Fecha,HoraInicio,HoraFin\n");
            for(Reserva r : reservas){
                String tipo = r.getPersona() instanceof Alumno ? "Alumno" : "Profesor";
                bw.write(tipo + "," + r.getPersona().getNombre() + "," + r.getPersona().getRut() + "," +
                         r.getPersona().getFuncionOCarrera() + "," + r.getEdificio() + "," + r.getSala() + "," +
                         r.getFecha() + "," + r.getHoraInicio() + "," + r.getHoraFin() + "\n");
            }
        } catch(Exception e){ System.out.println("Error al guardar CSV: "+e.getMessage()); }
    }
}
