using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;
using System.Xml.Linq;

namespace CrepaDelivery.Models
{
    public class Ingredient
    {
        [Key]
        [Required]
        [DisplayName("Όνομα")]
        public string Name { get; set; }

        [Required]
        [DisplayName("Τύπος Κρέπας")]
        public string CrepeCategory { get; set; }

        [Required]
        [DisplayName("Τύπος Συστατικού")]
        public string IngredientType { get; set; }

        [Required]
        [DisplayName("Τιμή (σε " + "\u20AC" + ")")]
        public string Price { get; set; }
    }
}