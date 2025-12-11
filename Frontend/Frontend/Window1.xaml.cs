using System;
using System.Collections.Generic;
using System.Net.Http;
using System.Windows;
using Newtonsoft.Json;

namespace Aplicacion_Ejemplo
{
    public partial class Window1 : Window
    {
        public Window1()
        {
            InitializeComponent();
            txtBienvenida.Text = $"Bienvenido, {Session.Nombre}";
            txtTipoUsuario.Text = $"{Session.Tipo} - Universidad del Desarrollo";

        }

        public static class Api
        {
            public static readonly HttpClient Client = new HttpClient
            {
                BaseAddress = new Uri("http://localhost:4567")
            };
        }

        private void btn_inicio_Click(object sender, RoutedEventArgs e)
        {
            var ventana = new Window1();
            ventana.Show();
            this.Close();
        }

        private async void btn_edificios_Click(object sender, RoutedEventArgs e)
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


        private void btn_reservas_Click(object sender, RoutedEventArgs e)
        {
            var ventana = new Window4();
            ventana.Show();
            this.Close();
        }

        private void btn_logout_Click(object sender, RoutedEventArgs e)
        {
            Session.Token = "";
            Session.Nombre = "";
            Session.Tipo = "";

            var ventana = new MainWindow();
            ventana.Show();
            this.Close();
        }

        private void btn_perfil_Click(object sender, RoutedEventArgs e)
        {
            var ventana = new Window3();
            ventana.Show();
            this.Close();
        }
    }
}
