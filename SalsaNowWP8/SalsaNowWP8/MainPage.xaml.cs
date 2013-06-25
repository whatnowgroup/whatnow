using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Navigation;
using System.Threading.Tasks;
using Microsoft.Phone.Controls;
using Microsoft.Phone.Shell;
using Microsoft.Phone.Maps;
using System.IO;
using Newtonsoft.Json.Linq;
using System.Diagnostics;
using Windows.Devices.Geolocation;
using System.Device.Location;
using System.Windows.Media;
using Microsoft.Phone.Maps.Controls;
using System.Windows.Shapes;
using SalsaNowWP8.Resources;
using Facebook.Client;
using Microsoft.Phone.Maps.Toolkit;

namespace SalsaNowWP8
{
    public partial class MainPage : PhoneApplicationPage
    {
        // Constructor
        public MainPage()
        {
            InitializeComponent();

            // Sample code to localize the ApplicationBar
            //BuildLocalizedApplicationBar();
            //showMyLocationOnMap();
            getSomething();
        }

        private FacebookSession session;

        private async Task Authenticate()
        {
            string message = String.Empty;
            try
            {
                session = await App.FacebookSessionClient.LoginAsync("user_about_me,read_stream");
                App.AccessToken = session.AccessToken;
                App.FacebookId = session.FacebookId;

                Debugger.Log(3, "warning", session.FacebookId);

                var rootFrame = Application.Current.RootVisual as PhoneApplicationFrame;
                if (rootFrame != null)
                    rootFrame.GoBack();
            }
            catch (InvalidOperationException ex)
            {
                message = "Login failed, " + ex.Message;
                MessageBox.Show(message);
            }
        }

        async private void facebookBtn_Click(object sender, RoutedEventArgs e)
        {
            if (!App.isAuthenticated)
            {
                App.isAuthenticated = true;
                await Authenticate();
            }
        }

        // Map stuff
        private void addCoordinateToMap(string name, string address, double latitude, double longitude)
        {
            //Ellipse shape = new Ellipse();
            //shape.Fill = new SolidColorBrush(Colors.Blue);
            //shape.Height = 16;
            //shape.Width = 16;
            //shape.Opacity = 80;

            
            //overlay.Content = shape;
            //overlay.PositionOrigin = new Point(0.5, 0.5);
            //overlay.GeoCoordinate = new GeoCoordinate(latitude, longitude);

            //UserLocationMarker marker = (UserLocationMarker)this.FindName("UserLocationMarker");
            //marker.GeoCoordinate = Map.Center;

            Pushpin pushpin = new Pushpin();
            pushpin.Content = name + " @ " + address;
            pushpin.Tap += say;

            MapOverlay overlay = new MapOverlay();
            overlay.Content = pushpin;
            overlay.GeoCoordinate = new GeoCoordinate(latitude, longitude);

            MapLayer layer = new MapLayer();
            layer.Add(overlay);

            Map.Layers.Add(layer);
        }

        async private void say(object sender, RoutedEventArgs e)
        {
            MessageBox.Show("hih");
        }

        private async void getSomething()
        {
            string url = "http://123.243.70.36:9000/events";
            var httpWebRequest = HttpWebRequest.CreateHttp(url);
            try
            {
                string response = await httpRequest(httpWebRequest);
                string response1 = response.Replace("\\", "");
                Debugger.Log(3, "Warning", response1);
                JArray arr = JArray.Parse(response);
                foreach (var e in arr)
                {
                    addCoordinateToMap((string)e["eventName"], (string)e["address"], Double.Parse((string)e["latitude"]), Double.Parse((string)e["longitude"]));
                }
            }
            catch (WebException ex)
            {
                ;
            }
        }

        // Sample code for building a localized ApplicationBar
        //private void BuildLocalizedApplicationBar()
        //{
        //    // Set the page's ApplicationBar to a new instance of ApplicationBar.
        //    ApplicationBar = new ApplicationBar();

        //    // Create a new button and set the text value to the localized string from AppResources.
        //    ApplicationBarIconButton appBarButton = new ApplicationBarIconButton(new Uri("/Assets/AppBar/appbar.add.rest.png", UriKind.Relative));
        //    appBarButton.Text = AppResources.AppBarButtonText;
        //    ApplicationBar.Buttons.Add(appBarButton);

        //    // Create a new menu item with the localized string from AppResources.
        //    ApplicationBarMenuItem appBarMenuItem = new ApplicationBarMenuItem(AppResources.AppBarMenuItemText);
        //    ApplicationBar.MenuItems.Add(appBarMenuItem);
        //}
        private void Map_Loaded(object sender, RoutedEventArgs e)
        {
            MapsSettings.ApplicationContext.ApplicationId = "<applicationid>";
            MapsSettings.ApplicationContext.AuthenticationToken = "<authenticationtoken>";
        }

        public async Task<string> httpRequest(HttpWebRequest request)
        {
            string received;

            using (var response = (HttpWebResponse)(await Task<WebResponse>.Factory.FromAsync(request.BeginGetResponse, request.EndGetResponse, null)))
            {
                using (var responseStream = response.GetResponseStream())
                {
                    using (var sr = new StreamReader(responseStream))
                    {
                        received = await sr.ReadToEndAsync();
                    }
                }
            }
            return received;
        }

    }
}