package modelos;

public class Persona {
    protected String nombre;
    protected String rut;
    protected String funcionOCarrera;

    public Persona(String nombre, String rut, String funcionOCarrera) {
        this.nombre = nombre;
        this.rut = rut;
        this.funcionOCarrera = funcionOCarrera;
    }

    public String getNombre() { return nombre; }
    public String getRut() { return rut; }
    public String getFuncionOCarrera() { return funcionOCarrera; }
}
