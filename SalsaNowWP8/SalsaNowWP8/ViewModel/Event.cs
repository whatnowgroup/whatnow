using System;

namespace SalsaNowWP8.ViewModel
{
    /**
     * {"id":4,"name":"Salsa","address":"Ivy","latitude":-33.86663,"longitude":151.207264,"attending":false,"description":"","shortDescription":"","rating":0.0,"friends":[]}
     */
    public class Event
    {
        public long ID { get; set; }
        public string Name { get; set; }
        public string Address { get; set; }
        public Double Longitude { get; set; }
        public Double Latitude { get; set; }
        public string ShortDescription { get; set; }
        public string Description { get; set; }
        public bool Attending { get; set; }
        public Double Rating { get; set; }
        
        public string DisplayText { get; set; }

        public void PopulateDisplayProperties()
        {
            if (string.IsNullOrEmpty(Name))
                DisplayText = Address;
            else
                DisplayText = Name + " @ " + Address;
        }
    }
}
