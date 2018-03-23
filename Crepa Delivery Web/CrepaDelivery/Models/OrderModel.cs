using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;

namespace CrepaDelivery.Models
{
    public class OrderModel
    {
        [Key]
        [DisplayName("ID")]
        public int ID { get; set; }

        [DisplayName("Ονοματεπώνυμο")]
        public string FullName { get; set; }

        [DisplayName("Διεύθυνση")]
        public string Address { get; set; }

        [DisplayName("Email Παράδοσης")]
        [EmailAddress]
        public string DeliverEmail { get; set; }

        [DisplayName("Τηλέφωνο")]
        [Phone]
        public string Phone { get; set; }

        [DisplayName("Σύνολο")]
        public string TotalCourtPrice { get; set; }


        [DisplayName("Email Χρήστη")]
        [EmailAddress]
        public string Email { get; set; }

        [DisplayName("Κατάσταση")]
        public int Completed { get; set; }

        [DisplayName("Ημ/νία Παραγγελίας")]
        public DateTime OrderDate { get; set; }



    }
}