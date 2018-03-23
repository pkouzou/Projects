using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;

namespace CrepaDelivery.Models.Helpers
{
    public class ContactViewModel
    {
        [Required]
        [Display(Name = "Όνομα")]
        public string Name { get; set; }

        [Required]
        [Display(Name = "Email")]
        [EmailAddress]
        public string Email { get; set; }

        [Required]
        [Display(Name = "Μήνυμα")]
        public string Message { get; set; }
    }
}