using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;

namespace CrepaDelivery.Models.Helpers
{
    public class DeliveryInfoViewModelRequired
    {
        [Key]
        [Required]
        [Display(Name = "Διεύθυνση Email")]
        [EmailAddress]
        public string UserEmail { get; set; }

        [Required]
        [Display(Name = "Email")]
        [EmailAddress]
        public string DeliverEmail { get; set; }

        [Required]
        [Display(Name = "Όνομα")]
        public string FirstName { get; set; }

        [Required]
        [Display(Name = "Επίθετο")]
        public string LastName { get; set; }

        [Required]
        [Display(Name = "Διεύθυνση")]
        public string Address { get; set; }

        [Required]
        [Display(Name = "Όροφος")]
        public string Floor { get; set; }

        [Required]
        [Phone]
        [Display(Name = "Τηλέφωνο")]
        public string PhoneNumber { get; set; }
    }
}