using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;

namespace CrepaDelivery.Models
{
    public class SoftDrink
    {
        [Key]
        public int ID { get; set; }

        [Required]
        [DisplayName("Όνομα")]
        public string Name { get; set; }

        [Required]
        [DisplayName("Τιμή (σε " + "\u20AC" + ")")]
        public string Price { get; set; }

        [DisplayName("Εικόνα")]
        public string ImagePath { get; set; }


    }
}