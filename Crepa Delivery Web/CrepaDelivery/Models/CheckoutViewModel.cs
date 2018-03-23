using CrepaDelivery.Models.Helpers;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace CrepaDelivery.Models
{
    public class CheckoutViewModel
    {

        public DeliveryInfoViewModelRequired DeliveryInfoViewModelRequired { get; set; }
        
        public List<CourtItem> CourtItems { get; set; }
    }
}