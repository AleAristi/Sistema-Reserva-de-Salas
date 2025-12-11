package modelos;

import java.util.ArrayList;
import java.util.List;

public class DatosStatic {
    private List<String> carreras;
    private List<String> funciones;
    private List<String> edificios;
    private List<String> salas;

    public DatosStatic() {
        carreras = new ArrayList<>();
        funciones = new ArrayList<>();
        edificios = new ArrayList<>();
        salas = new ArrayList<>();
    }

    public void agregarCarrera(String c) { carreras.add(c); }
    public void agregarFuncion(String f) { funciones.add(f); }
    public void agregarEdificio(String e) { edificios.add(e); }
    public void agregarSala(String s) { salas.add(s); }

    public List<String> getCarreras() { return carreras; }
    public List<String> getFunciones() { return funciones; }
    public List<String> getEdificios() { return edificios; }
    public List<String> getSalas() { return salas; }
}
