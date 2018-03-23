using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;

namespace CrepaDelivery.Models
{
    public class OrderDetailModel
    {
        [Key]
        [DisplayName("ID")]
        public int ID { get; set; }

        [DisplayName("ID Παραγγελίας")]
        public int OrderID { get; set; }

        [DisplayName("Τύπος")]
        public string Type { get; set; }

        [DisplayName("Υλικά")]
        public string Ingredients { get; set; }

        [DisplayName("Ποσότητα")]
        public int Quantity { get; set; }

        [DisplayName("Σύνολο")]
        public string TotalPrice { get; set; }

    }
}