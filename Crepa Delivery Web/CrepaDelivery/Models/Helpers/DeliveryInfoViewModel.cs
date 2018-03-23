using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;

namespace CrepaDelivery.Models.Helpers
{
    public class DeliveryInfoViewModel
    {
        [Key]
        [Display(Name = "Διεύθυνση Email")]
        [EmailAddress]
        public string UserEmail { get; set; }

        [Display(Name = "Email")]
        [EmailAddress]
        public string DeliverEmail { get; set; }

        [Display(Name = "Όνομα")]
        public string FirstName { get; set; }

        [Display(Name = "Επίθετο")]
        public string LastName { get; set; }

        [Display(Name = "Διεύθυνση")]
        public string Address{ get; set; }

        [Display(Name = "Όροφος")]
        public string Floor { get; set; }

        [Phone]
        [Display(Name = "Τηλέφωνο")]
        public string PhoneNumber { get; set; }
    }
}