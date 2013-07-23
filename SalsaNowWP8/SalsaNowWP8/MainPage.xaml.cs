using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Threading.Tasks;
using System.Windows;
using Facebook;
using Facebook.Client;
using SalsaNowWP8.ViewModel;

namespace SalsaNowWP8
{
    public partial class MainPage
    {
        public MainPage()
        {
            InitializeComponent();
        }

        private FacebookSession _session;


        private async Task Authenticate()
        {
            try
            {
                _session = await App.FacebookSessionClient.LoginAsync("user_about_me,read_stream");
                App.AccessToken = _session.AccessToken;
                App.FacebookId = _session.FacebookId;
                App.isAuthenticated = true;

                Debugger.Log(3, "warning", _session.FacebookId);
                NavigationService.Navigate(new Uri("/Views/ContentPage.xaml", UriKind.Relative));
            }
            catch (InvalidOperationException ex)
            {
                var message = "Login failed, " + ex.Message;
                MessageBox.Show(message);
            }
        }

        async private void Button_Click(object sender, RoutedEventArgs e)
        {
            if (App.isAuthenticated)
                return;
            App.isAuthenticated = true;
            await Authenticate();
            await GetAllEvents();
        }

        private async Task GetAllEvents()
        {
            if (!App.isAuthenticated)
                return;

            var fb = new FacebookClient(App.AccessToken);
            fb.GetCompleted += (o, e) =>
            {
                if (e.Error != null)
                {
                    Dispatcher.BeginInvoke(() => MessageBox.Show(e.Error.Message));
                    return;
                }

                var result = (IDictionary<string, object>)e.GetResultData();
                var data = (IEnumerable<object>)result["data"];
                Dispatcher.BeginInvoke(() =>
                {
                    foreach (var item in data)
                    {
                        var friend = (IDictionary<string, object>)item;
                        var f = new Friend
                        {
                            ID = (string)friend["id"], 
                            Name = (string)friend["name"]
                        };
                        f.PictureUri = new Uri(string.Format("https://graph.facebook.com/{0}/picture?type={1}&access_token={2}",
                            f.ID, "square", App.AccessToken));
                        FacebookData.Friends.Add(f);
                        Debugger.Log(3, "", f.ID);
                    }
                });

                fb.GetTaskAsync("/me/friends");

            };
        }
    }
}