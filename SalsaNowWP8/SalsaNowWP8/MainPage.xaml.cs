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

        private async void showMyLocationOnMap()
        {
            //Geolocator myLocator = new Geolocator();
            //Geoposition myPosition = await myLocator.GetGeopositionAsync();
            //Geocoordinate myCoordinate = myPosition.Coordinate;
            //GeoCoordinate myGeoCoordinate = CoordinateConverter.ConvertGeocoordinate(myCoordinate);
            //Map.Center = myGeoCoordinate;
            //Map.ZoomLevel = 13;

            //Ellipse myPoint = new Ellipse();
            //myPoint.Fill = new SolidColorBrush(Colors.Blue);
            //myPoint.Height = 20;
            //myPoint.Width = 20;
            //myPoint.Opacity = 50;

            //MapOverlay myLocationOverlay = new MapOverlay();
            //myLocationOverlay.Content = myPoint;
            //myLocationOverlay.PositionOrigin = new Point(0.5, 0.5);
            //myLocationOverlay.GeoCoordinate = myGeoCoordinate;

            //MapLayer myLocationLayer = new MapLayer();
            //myLocationLayer.Add(myLocationOverlay);

            //Map.Layers.Add(myLocationLayer);

        }

        private void addCoordinateToMap(double latitude, double longitude)
        {
            Ellipse shape = new Ellipse();
            shape.Fill = new SolidColorBrush(Colors.Blue);
            shape.Height = 16;
            shape.Width = 16;
            shape.Opacity = 80;

            MapOverlay overlay = new MapOverlay();
            overlay.Content = shape;
            overlay.PositionOrigin = new Point(0.5, 0.5);
            overlay.GeoCoordinate = new GeoCoordinate(latitude, longitude);

            MapLayer layer = new MapLayer();
            layer.Add(overlay);

            Map.Layers.Add(layer);
        }

        private async void getSomething()
        {
            string url = "http://ec2-54-218-30-210.us-west-2.compute.amazonaws.com:9000/tasks";
            var httpWebRequest = HttpWebRequest.CreateHttp(url);
            string response = await httpRequest(httpWebRequest);
            string response1 = response.Replace("\\", "");
            Debugger.Log(3, "Warning", response1);
            JArray arr = JArray.Parse(response);
            foreach (var e in arr)
            {
                addCoordinateToMap(Double.Parse((string)e["latitude"]), Double.Parse((string)e["longtitude"]));
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