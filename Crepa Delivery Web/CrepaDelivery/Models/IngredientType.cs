using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;

namespace CrepaDelivery.Models
{
    public class IngredientType
    {
        [Key]
        [Display(Name = "Τύπος Συστατικού")]
        public string Type { get; set; }

        [Display(Name = "Τύπος Κρέπας")]
        public string CrepeCategory { get; set; }
    }
}