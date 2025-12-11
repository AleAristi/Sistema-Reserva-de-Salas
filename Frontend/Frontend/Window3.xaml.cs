using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Net.Http;
using System.Text;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;

namespace Aplicacion_Ejemplo
{
    /// <summary>
    /// Lógica de interacción para Window3.xaml
    /// </summary>
    public partial class Window3 : Window
    {
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
        }

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
            var ventana = new Window4();
            ventana.Show();
            this.Close();
        }

        private void btn_perfil_Click(object sender, RoutedEventArgs e)
        {
            var ventana = new Window3();
            ventana.Show();
            this.Close();
        }

        private void btn_logout_Click(object sender, RoutedEventArgs e)
        {
            var ventana = new MainWindow();
            ventana.Show();
            this.Close();
        }
    }

}
