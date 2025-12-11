using Aplicacion_Ejemplo;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Net.Http;
using System.Text;
using System.Windows;
using System.Windows.Controls;

namespace Frontend_Reserva_Salas
{
    public partial class RegistroWindow : Window
    {
        private static readonly HttpClient client = new HttpClient
        {
            BaseAddress = new Uri("http://localhost:4567")
        };

        public RegistroWindow()
        {
            InitializeComponent();
        }

        // =====================================
        // Cargar CARRERAS
        // =====================================
        private async void CargarCarreras()
        {
            try
            {
                var resp = await client.GetAsync("/api/carreras");
                var json = await resp.Content.ReadAsStringAsync();

                var result = JsonConvert.DeserializeObject<ApiResponse<List<string>>>(json);

                if (result.ok)
                {
                    cbCarreras.ItemsSource = result.data;
                }
                else
                {
                    MessageBox.Show("Error cargando carreras: " + result.msg);
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show("Error conectando API: " + ex.Message);
            }
        }

        // =====================================
        // Cargar FUNCIONES
        // =====================================
        private async void CargarFunciones()
        {
            try
            {
                var resp = await client.GetAsync("/api/funciones");
                var json = await resp.Content.ReadAsStringAsync();

                var result = JsonConvert.DeserializeObject<ApiResponse<List<string>>>(json);

                if (result.ok)
                {
                    cbCarreras.ItemsSource = result.data;
                }
                else
                {
                    MessageBox.Show("Error cargando funciones: " + result.msg);
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show("Error conectando API: " + ex.Message);
            }
        }

        // =====================================
        // CAMBIO TIPO (Alumno / Profesor)
        // =====================================
        private void CbTipo_Changed(object sender, SelectionChangedEventArgs e)
        {
            if (cbTipo.SelectedItem is ComboBoxItem item)
            {
                string tipo = item.Content.ToString();

                if (tipo == "Alumno")
                {
                    lblCarreraFuncion.Text = "Carrera:";
                    CargarCarreras();
                }
                else
                {
                    lblCarreraFuncion.Text = "Función:";
                    CargarFunciones();
                }
            }
        }

        // =====================================
        // BOTÓN REGISTRAR
        // =====================================
        private async void BtnRegistrar_Click(object sender, RoutedEventArgs e)
        {
            string usuario = txtUsuario.Text.Trim();
            string nombre = txtNombre.Text.Trim();
            string rut = txtRut.Text.Trim();
            string password = txtPassword.Password.Trim();

            if (!(cbTipo.SelectedItem is ComboBoxItem itemTipo))
            {
                MessageBox.Show("Debe seleccionar tipo de usuario.");
                return;
            }

            string tipo = itemTipo.Content.ToString();
            string carreraOFuncion = cbCarreras.SelectedItem?.ToString();

            if (string.IsNullOrEmpty(usuario) ||
                string.IsNullOrEmpty(nombre) ||
                string.IsNullOrEmpty(rut) ||
                string.IsNullOrEmpty(password))
            {
                MessageBox.Show("Debe completar todos los campos.");
                return;
            }

            if (password.Length < 4)
            {
                MessageBox.Show("La contraseña debe tener mínimo 4 caracteres.");
                return;
            }

            var body = new
            {
                usuario,
                nombre,
                rut,
                tipo,
                carrera = carreraOFuncion,
                password
            };

            try
            {
                var json = JsonConvert.SerializeObject(body);
                var content = new StringContent(json, Encoding.UTF8, "application/json");

                var resp = await client.PostAsync("/api/register", content);
                var respStr = await resp.Content.ReadAsStringAsync();

                var result = JsonConvert.DeserializeObject<ApiResponse<object>>(respStr);

                if (resp.IsSuccessStatusCode && result.ok)
                {
                    MessageBox.Show("Usuario registrado con éxito.");

                    var login = new MainWindow();
                    login.Show();
                    this.Close();
                }
                else
                {
                    MessageBox.Show("Error: " + result.msg);
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show("Error conectando API: " + ex.Message);
            }
        }
    }
}
