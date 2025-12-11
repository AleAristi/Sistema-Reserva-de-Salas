package modelos;

import java.util.UUID;

/**
 * Modelo Reserva con id como String (UUID).
 * Tiene constructor sin-args para que Gson pueda deserializar.
 */
public class Reserva {
    private String id;
    private Persona persona;
    private String edificio;
    private String sala;
    private String fecha;
    private String horaInicio;
    private String horaFin;

    // Constructor principal usado en código
    public Reserva(Persona persona, String edificio, String sala, String fecha, String horaInicio, String horaFin) {
        this.id = UUID.randomUUID().toString();
        this.persona = persona;
        this.edificio = edificio;
        this.sala = sala;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    // Constructor vacío requerido por Gson
    public Reserva() {
        // No generar id aquí: lo hacemos al acceder si está nulo para mantener compatibilidad
    }

    // Asegura que siempre haya un id al obtenerlo
    public String getId() {
        if (id == null || id.isEmpty()) {
            id = UUID.randomUUID().toString();
        }
        return id;
    }

    public Persona getPersona() { return persona; }
    public String getEdificio() { return edificio; }
    public String getSala() { return sala; }
    public String getFecha() { return fecha; }
    public String getHoraInicio() { return horaInicio; }
    public String getHoraFin() { return horaFin; }

    public void setId(String id) { this.id = id; }
    public void setPersona(Persona persona) { this.persona = persona; }
    public void setEdificio(String edificio) { this.edificio = edificio; }
    public void setSala(String sala) { this.sala = sala; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public void setHoraInicio(String horaInicio) { this.horaInicio = horaInicio; }
    public void setHoraFin(String horaFin) { this.horaFin = horaFin; }

    @Override
    public String toString() {
        String nombre = persona != null ? persona.getNombre() : "N/A";
        String func = persona != null ? persona.getFuncionOCarrera() : "N/A";
        return getId() + " | " + nombre + " | " + func + " | Edificio " + edificio + " | Sala " + sala + " | " + fecha + " " + horaInicio + "-" + horaFin;
    }
}
