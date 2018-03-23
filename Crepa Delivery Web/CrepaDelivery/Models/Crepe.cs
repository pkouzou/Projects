using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;

namespace CrepaDelivery.Models
{
    public class Crepe
    {
        [Key]
        [Required]
        [DisplayName("ID")]
        public int ID { get; set; }

        [DisplayName("Υλικά")]
        public string Ingredients { get; set; }

        [DisplayName("Τύπος Κρέπας")]
        public string Type { get; set; }

        [DisplayName("Τιμή (σε " + "\u20AC" + ") - Κανονική")]
        public string NormalPrice { get; set; }

        [DisplayName("Τιμή (σε " + "\u20AC" + ") - XL")]
        public string XLPrice { get; set; }
        
    }
}