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
        // Configuración de la API
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
            // 1. Configurar el Token de sesión en el Header para todas las llamadas
            if (!string.IsNullOrEmpty(Session.Token))
            {
                Api.Client.DefaultRequestHeaders.Authorization =
                    new AuthenticationHeaderValue("Bearer", Session.Token);
            }

            // 2. Controlar visibilidad de pestañas según rol
            if (Session.Tipo == "Admin")
            {
                TabTodas.Visibility = Visibility.Visible;
            }
            else
            {
                TabTodas.Visibility = Visibility.Collapsed;
            }

            // 3. Cargar datos iniciales
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

                // Generar Fechas (Próximos 30 días)
                List<string> fechas = new List<string>();
                DateTime hoy = DateTime.Now;
                for (int i = 0; i < 30; i++)
                {
                    fechas.Add(hoy.AddDays(i).ToString("yyyy-MM-dd"));
                }
                cb_fecha.ItemsSource = fechas;
                cb_fecha.SelectedIndex = 0;

                // Generar Horas (08:00 a 21:00)
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

        // --- CREAR RESERVA (Conectado a la API) ---
        private async void btn_crear_Click(object sender, RoutedEventArgs e)
        {
            // Validar campos
            if (cb_edificio.SelectedItem == null || cb_sala.SelectedItem == null ||
                cb_fecha.SelectedItem == null || cb_hora.SelectedItem == null)
            {
                MessageBox.Show("Por favor completa todos los campos.");
                return;
            }

            try
            {
                // Crear objeto JSON exacto como lo espera el Backend Java
                var nuevaReserva = new
                {
                    edificio = cb_edificio.SelectedItem.ToString(),
                    sala = cb_sala.SelectedItem.ToString(),
                    fecha = cb_fecha.SelectedItem.ToString(),
                    horaInicio = cb_hora.SelectedItem.ToString()
                    // Nota: No enviamos usuario ni carrera, el backend lo saca del Token
                };

                string json = JsonConvert.SerializeObject(nuevaReserva);
                var content = new StringContent(json, Encoding.UTF8, "application/json");

                // Enviar POST
                var response = await Api.Client.PostAsync("/api/reservas", content);
                var respStr = await response.Content.ReadAsStringAsync();

                // Usamos dynamic para leer la respuesta flexiblemente
                dynamic result = JsonConvert.DeserializeObject(respStr);

                if (response.IsSuccessStatusCode && (bool)result.ok)
                {
                    MessageBox.Show($"¡Reserva creada con éxito!\nID: {result.reservaId}", "Éxito");

                    // Limpiar selección
                    cb_edificio.SelectedIndex = -1;
                    cb_sala.SelectedIndex = -1;

                    // Si estamos viendo la lista, actualizarla
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

        // --- GESTIÓN DE PESTAÑAS ---
        private void TabNueva_Click(object sender, MouseButtonEventArgs e)
        {
            ViewNuevaReserva.Visibility = Visibility.Visible;
            ViewTodasReservas.Visibility = Visibility.Collapsed;
        }

        private void TabTodas_Click(object sender, MouseButtonEventArgs e)
        {
            ViewNuevaReserva.Visibility = Visibility.Collapsed;
            ViewTodasReservas.Visibility = Visibility.Visible;

            CargarTodasLasReservas();
        }

        // --- ADMINISTRACIÓN (Solo Admin) ---
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
                    CargarTodasLasReservas(); // Recargar tabla
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

        // --- NAVEGACIÓN ---
        private void btn_inicio_Click(object sender, RoutedEventArgs e) { new Window1().Show(); this.Close(); }
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
        private void btn_reserva_Click(object sender, RoutedEventArgs e) { /* Ya estamos aquí */ }
        private void btn_perfil_Click(object sender, RoutedEventArgs e) { new Window3().Show(); this.Close(); }
        private void btn_logout_Click(object sender, RoutedEventArgs e) { Session.Token = ""; new MainWindow().Show(); this.Close(); }
    }

    // --- CLASES DE MODELO ---
    public class ReservaModel
    {
        public string id { get; set; }
        public string edificio { get; set; }
        public string sala { get; set; }
        public string fecha { get; set; }
        public string horaInicio { get; set; }
        public PersonaModel persona { get; set; }
    }

    public class PersonaModel
    {
        public string nombre { get; set; }
        public string tipo { get; set; }
    }
}