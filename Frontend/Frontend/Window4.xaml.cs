using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using System.Windows.Media;

namespace Aplicacion_Ejemplo
{
    public partial class Window4 : Window
    {
        public static class Api
        {
            public static readonly HttpClient Client = new HttpClient
            {
                BaseAddress = new Uri("http://localhost:4567")
            };
        }

        public Window4()
        {
            InitializeComponent();
        }

        private void Window_Loaded(object sender, RoutedEventArgs e)
        {
            // Validacion para el API
            if (!string.IsNullOrEmpty(Session.Token))
            {
                Api.Client.DefaultRequestHeaders.Authorization =
                    new AuthenticationHeaderValue("Bearer", Session.Token);
            }

            // Visibilidad de pestañas según rol
            if (Session.Tipo == "Admin")
            {
                TabTodas.Visibility = Visibility.Visible;
            }
            else
            {
                TabTodas.Visibility = Visibility.Collapsed;
            }
            CargarListas();
        }

        private async void CargarListas()
        {
            try
            {
                // Cargar Edificios
                var respEdificios = await Api.Client.GetAsync("/api/edificios");
                if (respEdificios.IsSuccessStatusCode)
                {
                    var json = await respEdificios.Content.ReadAsStringAsync();
                    var result = JsonConvert.DeserializeObject<ApiResponse<List<string>>>(json);
                    if (result.ok) cb_edificio.ItemsSource = result.data;
                }

                // Cargar Salas
                var respSalas = await Api.Client.GetAsync("/api/salas");
                if (respSalas.IsSuccessStatusCode)
                {
                    var json = await respSalas.Content.ReadAsStringAsync();
                    var result = JsonConvert.DeserializeObject<ApiResponse<List<string>>>(json);
                    if (result.ok) cb_sala.ItemsSource = result.data;
                }

                // Lista de fechas (No hay necesidad de validar si existen reservas para la fecha, el backend ya lo realiza)
                List<string> fechas = new List<string>();
                DateTime hoy = DateTime.Now;
                for (int i = 0; i < 30; i++)
                {
                    fechas.Add(hoy.AddDays(i).ToString("yyyy-MM-dd"));
                }
                cb_fecha.ItemsSource = fechas;
                cb_fecha.SelectedIndex = 0;

                // Lista de horas (No hay necesidad de validar si existen reservas para la hora, el backend ya lo realiza)
                List<string> horas = new List<string>();
                for (int i = 8; i <= 21; i++)
                {
                    horas.Add(i.ToString("00") + ":00");
                }
                cb_hora.ItemsSource = horas;
            }
            catch (Exception ex)
            {
                MessageBox.Show("Error al cargar listas: " + ex.Message);
            }
        }

        // Creacion de reserva
        private async void btn_crear_Click(object sender, RoutedEventArgs e)
        {
            // Validacion de nulos
            if (cb_edificio.SelectedItem == null || cb_sala.SelectedItem == null ||
                cb_fecha.SelectedItem == null || cb_hora.SelectedItem == null)
            {
                MessageBox.Show("Por favor completa todos los campos.");
                return;
            }

            try
            {
                // Armar JSON que recibira atravez de POST el backend, usuario, rut y carrera vienen de los datos almacenados al iniciar sesion (Session)
                var nuevaReserva = new
                {
                    edificio = cb_edificio.SelectedItem.ToString(),
                    sala = cb_sala.SelectedItem.ToString(),
                    fecha = cb_fecha.SelectedItem.ToString(),
                    horaInicio = cb_hora.SelectedItem.ToString()
                };

                string json = JsonConvert.SerializeObject(nuevaReserva);
                var content = new StringContent(json, Encoding.UTF8, "application/json");

                // Enviar POST
                var response = await Api.Client.PostAsync("/api/reservas", content);
                var respStr = await response.Content.ReadAsStringAsync();

                // Manejo de errores atravez de los codigos de estado de API
                dynamic result = JsonConvert.DeserializeObject(respStr);

                if (response.IsSuccessStatusCode && (bool)result.ok)
                {
                    MessageBox.Show($"¡Reserva creada con éxito!\nID: {result.reservaId}", "Éxito");

                    // Deselecciona la reserva despues de realizarse existosamente
                    cb_edificio.SelectedIndex = -1;
                    cb_sala.SelectedIndex = -1;

                    // Si es admin, actualizar la lista para mostrar la nueva reserva
                    if (ViewTodasReservas.Visibility == Visibility.Visible)
                        CargarTodasLasReservas();
                }
                else
                {
                    string msg = (string)result.msg;
                    MessageBox.Show("Error al crear reserva: " + msg, "Error");
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show("Error de conexión: " + ex.Message);
            }
        }

        // Mostrar pagina para crear reserva
        private void TabNueva_Click(object sender, MouseButtonEventArgs e)
        {
            ViewNuevaReserva.Visibility = Visibility.Visible;
            ViewTodasReservas.Visibility = Visibility.Collapsed;
        }

        // Mostrar pagina con todas las reservas (Solo Admin)
        private void TabTodas_Click(object sender, MouseButtonEventArgs e)
        {
            ViewNuevaReserva.Visibility = Visibility.Collapsed;
            ViewTodasReservas.Visibility = Visibility.Visible;

            CargarTodasLasReservas();
        }

        // Administracion y visualizacion de todas las reservas
        private async void CargarTodasLasReservas()
        {
            try
            {
                var response = await Api.Client.GetAsync("/api/reservas");
                if (response.IsSuccessStatusCode)
                {
                    var json = await response.Content.ReadAsStringAsync();
                    var result = JsonConvert.DeserializeObject<ApiResponse<List<ReservaModel>>>(json);

                    if (result.ok)
                    {
                        dgReservas.ItemsSource = result.data;
                    }
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show("Error al cargar reservas: " + ex.Message);
            }
        }

        private async void BtnEliminar_Click(object sender, RoutedEventArgs e)
        {
            if (MessageBox.Show("¿Eliminar esta reserva?", "Confirmar", MessageBoxButton.YesNo) != MessageBoxResult.Yes)
                return;

            try
            {
                Button btn = sender as Button;
                string id = btn.Tag.ToString();

                var response = await Api.Client.DeleteAsync($"/api/reservas/{id}");
                if (response.IsSuccessStatusCode)
                {
                    // Actualizar la lista de reservas
                    CargarTodasLasReservas();
                }
                else
                {
                    MessageBox.Show("No se pudo eliminar la reserva.");
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show("Error: " + ex.Message);
            }
        }

        // Botones barra navegacion
        private void btn_inicio_Click(object sender, RoutedEventArgs e) { new Window1().Show(); this.Close(); }

        // Cargar lista de edificios desde el API antes de mostrar la ventana
        private async void btn_edificio_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                var response = await Api.Client.GetAsync("/api/edificios");
                var json = await response.Content.ReadAsStringAsync();

                var result = JsonConvert.DeserializeObject<ApiResponse<List<string>>>(json);

                if (result.ok)
                {
                    var ventana = new Window2(result.data);
                    ventana.Show();
                    this.Close();
                }
                else
                {
                    MessageBox.Show("Error: " + result.msg);
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show("Error connecting to API: " + ex.Message);
            }
        }
        private void btn_reserva_Click(object sender, RoutedEventArgs e) 
        {
            }
        private void btn_perfil_Click(object sender, RoutedEventArgs e) 
        { 
            new Window3().Show(); 
            this.Close(); 
        }
        private void btn_logout_Click(object sender, RoutedEventArgs e) 
        {
            // Limpieza de datos
            Session.Token = "";
            Session.Nombre = "";
            Session.Tipo = "";
            Session.Usuario = "";
            Session.Carrera = "";
            new MainWindow().Show(); 
            this.Close(); 
        }
    }

    // Categorias para los bindigs de reservas
    public class ReservaModel
    {
        public string id { get; set; }
        public string edificio { get; set; }
        public string sala { get; set; }
        public string fecha { get; set; }
        public string horaInicio { get; set; }
        public PersonaModel persona { get; set; }
    }
    
    // Permite expandir el modelo para las reservas y realizar el criteio de admin
    public class PersonaModel
    {
        public string nombre { get; set; }
        public string tipo { get; set; }
    }
}