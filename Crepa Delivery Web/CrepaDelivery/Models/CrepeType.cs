using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;

namespace CrepaDelivery.Models
{
    public class CrepeType
    {
        [Key]
        [DisplayName("Τύπος Κρέπας")]
        public string type { get;  set;}
    }
}