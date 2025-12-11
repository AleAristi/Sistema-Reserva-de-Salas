using Aplicacion_Ejemplo;
using Frontend_Reserva_Salas;
using Newtonsoft.Json;
using System;
using System.Net.Http;
using System.Text;
using System.Windows;
using System.Windows.Controls;

namespace Aplicacion_Ejemplo
{
    public partial class MainWindow : Window
    {
        private static readonly HttpClient client = new HttpClient
        {
            BaseAddress = new Uri("http://localhost:4567")
        };

        public MainWindow()
        {
            InitializeComponent();
        }

        private async void BtnIniciarSesion_Click(object sender, RoutedEventArgs e)
        {
            string usuario = txt_correo.Text.Trim();
            string password = txt_contrasena.Password;

            if (usuario == "" || password == "")
            {
                MessageBox.Show("Debe ingresar correo y contraseña.");
                return;
            }

            try
            {
                var body = new
                {
                    usuario,
                    password
                };

                string json = JsonConvert.SerializeObject(body);
                var content = new StringContent(json, Encoding.UTF8, "application/json");

                var response = await client.PostAsync("/api/login", content);
                string respStr = await response.Content.ReadAsStringAsync();

                var result = JsonConvert.DeserializeObject<LoginResponse>(respStr);

                if (response.IsSuccessStatusCode && result.ok)
                {
                    // Guardar token y datos del usuario
                    Session.Token = result.token;
                    Session.Nombre = result.nombre;
                    Session.Tipo = result.tipo;
                    Session.Usuario = result.usuario;         // <--- AGREGAR
                    Session.Carrera = result.carreraOFuncion; // <--- AGREGAR

                    var ventana = new Window1();
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
                MessageBox.Show("Error al conectar con el servidor: " + ex.Message);
            }
        }

        private void BtnRegistro_Click(object sender, RoutedEventArgs e)
        {
            var ventana = new RegistroWindow();
            ventana.Show();
            this.Close();
        }

        private void Input_Correo(object sender, TextChangedEventArgs e)
        {

        }

        // FIRMA CORRECTA PARA PasswordBox
        private void Input_contasena(object sender, RoutedEventArgs e)
        {
        
        }
    }

    // Respuesta del API
    // Respuesta del API (Actualizar para incluir carreraOFuncion)
    public class LoginResponse
    {
        public bool ok { get; set; }
        public string token { get; set; }
        public string msg { get; set; }
        public string nombre { get; set; }
        public string tipo { get; set; }
        public string usuario { get; set; }
        public string carreraOFuncion { get; set; } // <--- AGREGAR ESTO
    }

    // Sesión global (Actualizar para incluir Carrera)
    public static class Session
    {
        public static string Token = "";
        public static string Nombre = "";
        public static string Tipo = "";
        public static string Usuario = "";
        public static string Carrera = ""; // <--- AGREGAR ESTO
    }
}
