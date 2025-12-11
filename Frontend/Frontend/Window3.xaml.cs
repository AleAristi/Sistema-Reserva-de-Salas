using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Net.Http;
using System.Windows;

namespace Aplicacion_Ejemplo
{
    public partial class Window3 : Window
    {
        //Categoria definida para los bindings
        public class PerfilUsuario
        {
            public string Nombre { get; set; }
            public string Carrera { get; set; }
            public string Usuario { get; set; }
            public string Tipo { get; set; }
        }

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
            var perfil = new PerfilUsuario
            {
                Nombre = Session.Nombre,
                Carrera = Session.Carrera,
                Usuario = Session.Usuario,
                Tipo = Session.Tipo
            };
            this.DataContext = perfil;
        }

        // Barra superior

        private void btn_inicio_Click(object sender, RoutedEventArgs e)
        {
            var ventana = new Window1();
            ventana.Show();
            this.Close();
        }

        // Cargar edificios desde la api previo a abrir la ventana
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

        }

        private void btn_logout_Click(object sender, RoutedEventArgs e)
        {
            // Limpieza de datos
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