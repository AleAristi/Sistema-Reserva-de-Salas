package servicios;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import static spark.Spark.*;
import modelos.Reserva;
import modelos.Persona;
import modelos.Alumno;
import modelos.Profesor;
import modelos.Usuario;
import modelos.DatosStatic;

import java.util.*;

// Creacion de las peticiones para conectar Backend con el Frontend

public class RestServerSpark {
    private final GestorReservas gestor;
    private final AuthService auth;
    private final DatosStatic datosStatic;
    private final Gson gson;

    // Datos que tendra
    public RestServerSpark(GestorReservas gestor, AuthService auth, DatosStatic datosStatic) {
        this.gestor = gestor;
        this.auth = auth;
        this.datosStatic = datosStatic;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public void start(int port) {
        port(port);

        // Configuración inicial del servidor local para CORS (forma de conexion entre backend y frontend)
        options("/*", (req, res) -> {
            String acrh = req.headers("Access-Control-Request-Headers");
            if (acrh != null) res.header("Access-Control-Allow-Headers", acrh);
            String acrm = req.headers("Access-Control-Request-Method");
            if (acrm != null) res.header("Access-Control-Allow-Methods", acrm);
            return "OK";
        });

        before((req, res) -> {
            res.header("Access-Control-Allow-Origin", "*");
            res.type("application/json");
        });
        
        
        /// get, envia tokens del backend que consumira el frontend a peticion
        /// post, envia tokens del frontend al backend que permite operar al backend con datos obtenidos desde el frontend
        /// delete, permite la eliminacion de tokens solicitados
 
        // Ping para validar salud del servidor
        get("/api/ping", (req, res) -> gson.toJson(Map.of("ok", true, "msg", "pong")));

        // Obtener reservas
        get("/api/reservas", (req, res) -> {
            try {
                return gson.toJson(Map.of("ok", true, "data", gestor.getReservas()));
            } catch (Exception ex) {
                res.status(500);
                return gson.toJson(Map.of("ok", false, "msg", ex.getMessage()));
            }
        });

        // Crear reserva
        post("/api/reservas", (req, res) -> {
            try {
                String token = extractBearerToken(req.headers("Authorization"));
                if (token == null) {
                    res.status(401);
                    return gson.toJson(Map.of("ok", false, "msg", "No token provided"));
                }

                Usuario u = auth.getUsuarioPorToken(token);
                if (u == null) {
                    res.status(401);
                    return gson.toJson(Map.of("ok", false, "msg", "Token invalido"));
                }

                Map<String, Object> body = gson.fromJson(req.body(), Map.class);
                String edificio = (String) body.get("edificio");
                String sala = (String) body.get("sala");
                String fecha = (String) body.get("fecha");
                String horaInicio = (String) body.get("horaInicio");

                if (edificio == null || sala == null || fecha == null || horaInicio == null) {
                    res.status(400);
                    return gson.toJson(Map.of("ok", false, "msg", "Faltan datos: edificio/sala/fecha/horaInicio"));
                }

                // --- CORRECCIÓN AQUÍ ---
                String horaInicioRaw = horaInicio;
                if (horaInicioRaw.length() == 4 && horaInicioRaw.charAt(1) == ':')
                    horaInicioRaw = "0" + horaInicioRaw;

                final String horaInicioNorm = horaInicioRaw;
                // ------------------------

                int h = Integer.parseInt(horaInicioNorm.split(":")[0]);
                String horaFin = (h + 1) + ":00";

                Persona persona = u.getTipo().equalsIgnoreCase("Alumno")
                        ? new Alumno(u.getNombre(), u.getRut(), u.getCarreraOFuncion())
                        : new Profesor(u.getNombre(), u.getRut(), u.getCarreraOFuncion());

                // Validación de choque de horario
                boolean ocupada = gestor.getReservas().stream().anyMatch(r ->
                        r.getEdificio().equals(edificio) &&
                        r.getSala().equals(sala) &&
                        r.getFecha().equals(fecha) &&
                        r.getHoraInicio().equals(horaInicioNorm)
                );

                if (ocupada) {
                    res.status(409);
                    return gson.toJson(Map.of("ok", false, "msg", "Hora ocupada"));
                }

                Reserva r = new Reserva(persona, edificio, sala, fecha, horaInicioNorm, horaFin);
                gestor.agregarReserva(r);

                return gson.toJson(Map.of("ok", true, "reservaId", r.getId()));
            } catch (NumberFormatException nfe) {
                res.status(400);
                return gson.toJson(Map.of("ok", false, "msg", "Formato de hora invalido"));
            } catch (Exception ex) {
                res.status(500);
                return gson.toJson(Map.of("ok", false, "msg", ex.getMessage()));
            }
        });

        // Eliminar reserva
        delete("/api/reservas/:id", (req, res) -> {
            try {
                String token = extractBearerToken(req.headers("Authorization"));
                if (token == null) {
                    res.status(401);
                    return gson.toJson(Map.of("ok", false, "msg", "No token provided"));
                }

                Usuario u = auth.getUsuarioPorToken(token);
                if (u == null) {
                    res.status(401);
                    return gson.toJson(Map.of("ok", false, "msg", "Token invalido"));
                }

                String id = req.params(":id");
                boolean ok = gestor.eliminarReservaPorId(id);

                if (!ok) {
                    res.status(404);
                    return gson.toJson(Map.of("ok", false, "msg", "Reserva no encontrada"));
                }

                return gson.toJson(Map.of("ok", true));
            } catch (Exception ex) {
                res.status(500);
                return gson.toJson(Map.of("ok", false, "msg", ex.getMessage()));
            }
        });

        // Registrar usuario
        post("/api/register", (req, res) -> {
            try {
                Map<String, String> body = gson.fromJson(req.body(), Map.class);

                String usuario = body.get("usuario");
                String nombre = body.get("nombre");
                String rut = body.get("rut");
                String tipo = body.get("tipo");
                String carrera = body.get("carrera");
                String password = body.get("password");

                if (usuario == null || password == null || password.length() < 4) {
                    res.status(400);
                    return gson.toJson(Map.of("ok", false, "msg", "Usuario o password invalidos (>=4)"));
                }

                boolean ok = auth.register(usuario, nombre, rut, carrera, tipo, password);
                if (!ok) {
                    res.status(400);
                    return gson.toJson(Map.of("ok", false, "msg", "Usuario existe o datos invalidos"));
                }

                return gson.toJson(Map.of("ok", true));
            } catch (Exception ex) {
                res.status(500);
                return gson.toJson(Map.of("ok", false, "msg", ex.getMessage()));
            }
        });

        // Login
        post("/api/login", (req, res) -> {
            try {
                Map<String, String> body = gson.fromJson(req.body(), Map.class);

                String usuario = body.get("usuario");
                String password = body.get("password");

                String token = auth.login(usuario, password);
                if (token == null) {
                    res.status(401);
                    return gson.toJson(Map.of("ok", false, "msg", "Credenciales invalidas"));
                }

                // === OBTENER USUARIO COMPLETO PARA SACAR EL NOMBRE ===
                Usuario u = auth.getUsuarioPorToken(token);
                if (u == null) {
                    res.status(500);
                    return gson.toJson(Map.of("ok", false, "msg", "Error interno: usuario nulo"));
                }

                // === RESPUESTA COMPLETA A FRONTEND ===
                    return gson.toJson(Map.of(
                        "ok", true,
                        "token", token,
                        "nombre", u.getNombre(),
                        "tipo", u.getTipo(),
                        "usuario", u.getUsuario(),
                        "carreraOFuncion", u.getCarreraOFuncion()
                    ));
                    
            } catch (Exception ex) {
                res.status(500);
                return gson.toJson(Map.of("ok", false, "msg", ex.getMessage()));
            }
        });


        // Datos estáticos
        get("/api/edificios", (req, res) -> gson.toJson(Map.of("ok", true, "data", datosStatic.getEdificios())));
        get("/api/salas", (req, res) -> gson.toJson(Map.of("ok", true, "data", datosStatic.getSalas())));
        get("/api/carreras", (req, res) -> gson.toJson(Map.of("ok", true, "data", datosStatic.getCarreras())));
        get("/api/funciones", (req, res) -> gson.toJson(Map.of("ok", true, "data", datosStatic.getFunciones())));
    }

    private String extractBearerToken(String header) {
        if (header == null) return null;
        header = header.trim();
        if (header.isEmpty()) return null;
        if (!header.startsWith("Bearer ")) return null;
        return header.substring("Bearer ".length()).trim();
    }
}
