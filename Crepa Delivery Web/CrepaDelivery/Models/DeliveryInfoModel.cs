using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;

namespace CrepaDelivery.Models
{
    public class DeliveryInfoModel
    {
        [Key]
        [EmailAddress]
        public string UserEmail { get; set; }

        [EmailAddress]
        public string DeliverEmail { get; set; }

        public string FirstName { get; set; }

        public string LastName { get; set; }

        public string Address { get; set; }

        public string Floor { get; set; }

        [DataType(DataType.PhoneNumber)]
        public string PhoneNumber { get; set; }
    }
}