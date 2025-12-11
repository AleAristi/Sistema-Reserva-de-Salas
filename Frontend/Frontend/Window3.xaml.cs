using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Net.Http;
using System.Windows;

namespace Aplicacion_Ejemplo
{
    /// <summary>
    /// Lógica de interacción para Window3.xaml
    /// </summary>
    public partial class Window3 : Window
    {
        // Clase auxiliar para vincular los datos con el XAML
        public class PerfilUsuario
        {
            public string Nombre { get; set; }
            public string Carrera { get; set; }
            public string Usuario { get; set; }
            public string Tipo { get; set; }
        }

        // Cliente HTTP (mantenemos tu estructura original)
        public static class Api
        {
            public static readonly HttpClient Client = new HttpClient
            {
                BaseAddress = new Uri("http://localhost:4567")
            };
        }

        public Window3()
        {
            InitializeComponent();
            CargarDatosPerfil();
        }

        private void CargarDatosPerfil()
        {
            // Creamos el objeto con los datos guardados en la Sesión estática
            var perfil = new PerfilUsuario
            {
                Nombre = Session.Nombre,
                Carrera = Session.Carrera, // Ahora Session.Carrera tendrá valor
                Usuario = Session.Usuario,
                Tipo = Session.Tipo
            };

            // Asignamos al DataContext para que el XAML lo muestre
            this.DataContext = perfil;
        }

        // --- Navegación ---

        private void btn_inicio_Click(object sender, RoutedEventArgs e)
        {
            var ventana = new Window1();
            ventana.Show();
            this.Close();
        }

        private async void btn_edificio_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                var response = await Api.Client.GetAsync("/api/edificios");
                var json = await response.Content.ReadAsStringAsync();

                // Asegúrate de usar tu clase ApiResponse definida en el proyecto
                var result = JsonConvert.DeserializeObject<ApiResponse<List<string>>>(json);

                if (result != null && result.ok)
                {
                    var ventana = new Window2(result.data);
                    ventana.Show();
                    this.Close();
                }
                else
                {
                    MessageBox.Show("Error al obtener edificios: " + (result?.msg ?? "Error desconocido"));
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show("Error de conexión: " + ex.Message);
            }
        }

        private void btn_reserva_Click(object sender, RoutedEventArgs e)
        {
            var ventana = new Window4();
            ventana.Show();
            this.Close();
        }

        private void btn_perfil_Click(object sender, RoutedEventArgs e)
        {
            // Ya estamos en perfil
        }

        private void btn_logout_Click(object sender, RoutedEventArgs e)
        {
            // Limpiamos la sesión al salir
            Session.Token = "";
            Session.Nombre = "";
            Session.Tipo = "";
            Session.Usuario = "";
            Session.Carrera = "";

            var ventana = new MainWindow();
            ventana.Show();
            this.Close();
        }
    }
}