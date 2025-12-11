package modelos;

public class Usuario {
    private String usuario; // nombre de usuario (login)
    private String nombre;  // nombre completo
    private String rut;
    private String carreraOFuncion;
    private String tipo; // "Alumno" o "Profesor"
    private String password; // guardado en texto plano porque pediste simple

    public Usuario(String usuario, String nombre, String rut, String carreraOFuncion, String tipo, String password) {
        this.usuario = usuario;
        this.nombre = nombre;
        this.rut = rut;
        this.carreraOFuncion = carreraOFuncion;
        this.tipo = tipo;
        this.password = password;
    }

    // getters y setters (Gson los necesita)
    public String getUsuario() { return usuario; }
    public String getNombre() { return nombre; }
    public String getRut() { return rut; }
    public String getCarreraOFuncion() { return carreraOFuncion; }
    public String getTipo() { return tipo; }
    public String getPassword() { return password; }

    public void setUsuario(String usuario) { this.usuario = usuario; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setRut(String rut) { this.rut = rut; }
    public void setCarreraOFuncion(String carreraOFuncion) { this.carreraOFuncion = carreraOFuncion; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public void setPassword(String password) { this.password = password; }
}
