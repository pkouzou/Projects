using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;

namespace CrepaDelivery.Models
{
    public class CourtItem
    {

        [Key]
        public int ID { get; set; }

        public string Email { get; set; }

        public string CrepeSize { get; set; }

        public string Ingredients { get; set; }

        public double Price { get; set; }

        public int Quantity { get; set; }

        public double TotalPrice { get; set; }
    }
}