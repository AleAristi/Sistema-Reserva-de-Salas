package servicios;

import modelos.Usuario;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.*;

// Permite crear un login
public class AuthService {
    //almacena los datos principales de un usuario en un json
    private final Path usuariosPath = Paths.get("usuarios.json");
    private final Gson gson = new Gson();
    
    // Estructura de datos para realizar busqueda eficiente de los usuarios
    private Map<String, Usuario> usuariosByUsuario = new HashMap<>();
    private Map<String, String> sessions = new HashMap<>(); // token -> usuario (username)

    public AuthService() {
        loadUsers();
    }

    // Carga la lista de usuarios del JSON
    private void loadUsers() {
        try {
            if (Files.exists(usuariosPath)) {
                String json = new String(Files.readAllBytes(usuariosPath), "UTF-8");
                Type t = new TypeToken<List<Usuario>>() {}.getType();
                List<Usuario> lista = gson.fromJson(json, t);
                if (lista != null) {
                    for (Usuario u : lista) usuariosByUsuario.put(u.getUsuario(), u);
                }
            }
        } catch (Exception e) {
            System.out.println("Error leyendo usuarios.json: " + e.getMessage());
        }
    }
    
    // Permite actualizar la lista de usuarios del JSON
    private void saveUsers() {
        try (Writer w = Files.newBufferedWriter(usuariosPath)) {
            List<Usuario> lista = new ArrayList<>(usuariosByUsuario.values());
            gson.toJson(lista, w);
        } catch (IOException e) {
            System.out.println("Error guardando usuarios.json: " + e.getMessage());
        }
    }

    // Registro de usuarios nuevos
    public boolean register(String usuario, String nombre, String rut, String carreraOFuncion, String tipo, String password) {
        if (usuario == null || usuario.trim().isEmpty()) return false;
        if (password == null || password.length() < 4) return false;
        if (usuariosByUsuario.containsKey(usuario)) return false;
        Usuario u = new Usuario(usuario, nombre, rut, carreraOFuncion, tipo, password);
        usuariosByUsuario.put(usuario, u);
        saveUsers();
        return true;
    }

    // Inicio de sesion y asignacion de un identificador unico para cada usuario
    public String login(String usuario, String password) {
        Usuario u = usuariosByUsuario.get(usuario);
        if (u == null) return null;
        if (!u.getPassword().equals(password)) return null;
        String token = UUID.randomUUID().toString();
        sessions.put(token, usuario);
        return token;
    }
    
    // Obtencion de datos de usuario por su identificador
    public Usuario getUsuarioPorToken(String token) {
        String usuario = sessions.get(token);
        if (usuario == null) return null;
        return usuariosByUsuario.get(usuario);
    }
    
    // Obtencion de datos por su nombre
    public Usuario getUsuarioPorNombre(String usuario) {
        return usuariosByUsuario.get(usuario);
    }
}
