package interfaces;

import modelos.Reserva;
import java.io.IOException;
import java.util.List;

public interface IGestorReservas {
    void agregarReserva(Reserva r);
    void eliminarReserva(int indice);
    boolean eliminarReservaPorId(String id);
    void mostrarReservas();
    void cargarDatos(String ruta) throws IOException;
    List<Reserva> getReservas();
}
