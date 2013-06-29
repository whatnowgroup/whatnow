using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SalsaNowWP8.ViewModel
{
    public class Event
    {
        public Event()
        {
        }

        public long ID { get; set; }
        public string Name { get; set; }
        public string Address { get; set; }
        public Double Longitude { get; set; }
        public Double Latitude { get; set; }
        public string Description { get; set; }
    }
}
