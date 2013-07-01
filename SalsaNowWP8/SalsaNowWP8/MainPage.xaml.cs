using System;
using System.Collections.Generic;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Threading.Tasks;
using Microsoft.Phone.Controls;
using Microsoft.Phone.Maps;
using System.IO;
using Newtonsoft.Json.Linq;
using System.Diagnostics;
using System.Device.Location;
using System.Windows.Media;
using Microsoft.Phone.Maps.Controls;
using Facebook.Client;
using System.Windows.Media.Imaging;
using SalsaNowWP8.ViewModel;

namespace SalsaNowWP8
{
    public partial class MainPage : PhoneApplicationPage
    {

        private static bool DetailPageLoaded = false;
        private TranslateTransform move = new TranslateTransform();
        private Dictionary<Image, Event> PinToEventMap = new Dictionary<Image, Event>();

        // Constructor
        public MainPage()
        {
            InitializeComponent();

            // Sample code to localize the ApplicationBar
            //BuildLocalizedApplicationBar();
            //showMyLocationOnMap();
        //    setupMap();
            setupDetailPanel();
            ReadEvents();
        }

        private void setupDetailPanel()
        {
            DetailPanel.ManipulationDelta += (s, e) =>
            {
                move.X = e.DeltaManipulation.Translation.X;
                move.Y = e.DeltaManipulation.Translation.Y;
            };

            DetailPanel.ManipulationDelta += (s, e) =>
            {
                if (move.Y > 3)
                    GotoOnlyMapState();
            };
        }

        private void setupMap()
        {
            //VisualStateManager.GoToState(this, "OnlyMap", true);
            Map.Tap += (a, b) => {
                    foreach (VisualStateGroup g in VisualStateManager.GetVisualStateGroups(LayoutRoot))
                    {
                        if (g.Name == "Normal" && g.CurrentState.Equals("OnlyMap"))
                            return;
                    }
                    GotoOnlyMapState();
            };
        }

        private void GotoOnlyMapState()
        {
            VisualStateManager.GoToState(this, "OnlyMap", true);
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
            if (App.isAuthenticated) 
                return;
            App.isAuthenticated = true;
            await Authenticate();
        }

        // Map stuff
        private void addCoordinateToMap(Event e)
        {
            var image = new Image { Source = new BitmapImage(new Uri("/Assets/pin_sq_down.32.png", UriKind.Relative)) };
            image.Opacity = .5;
            image.Tap += TapOnPushpinEvent;

            PinToEventMap.Add(image, e);

            MapOverlay overlay = new MapOverlay();
            overlay.Content = image;
            overlay.GeoCoordinate = new GeoCoordinate(e.Latitude, e.Longitude);

            MapLayer layer = new MapLayer();
            layer.Add(overlay);

            Map.Layers.Add(layer);
         
        }

        async private void TapOnPushpinEvent(object sender, RoutedEventArgs e)
        {
            VisualStateManager.GoToState(this, "TapOnPush", true);
            Event wnEvent;
            if (!PinToEventMap.ContainsKey((Image) sender)) 
                return;
            wnEvent = PinToEventMap[(Image)sender];
            DetailPanelEventName.Text = wnEvent.Name + " @ " + wnEvent.Address;

            BitmapImage image = new BitmapImage();

            DetailPanelEventImage.Source = new BitmapImage(new Uri("/Assets/pin_sq_down.32.png", UriKind.Relative));
        }

        private async void ReadEvents()
        {
            string url = Constants.EventsSource;
            var httpWebRequest = HttpWebRequest.CreateHttp(url);
            try
            {
                string response = await httpRequest(httpWebRequest);
                JArray arr = JArray.Parse(response);
                foreach (var e in arr)
                {
                    Event wnEvent = new Event();

                    wnEvent.ID = (long)e["id"];
                    wnEvent.Name = (string)e["eventName"];
                    wnEvent.Address = (string)e["address"];
                    wnEvent.Longitude = Double.Parse((string)e["longitude"]);
                    wnEvent.Latitude = Double.Parse((string)e["latitude"]);

                    addCoordinateToMap(wnEvent);
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