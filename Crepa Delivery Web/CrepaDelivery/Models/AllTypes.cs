using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace CrepaDelivery.Models
{
    public class AllTypes
    {
        public List<CrepeType> CrepeTypes { get; set; }

        public List<IngredientType> IngredientTypes { get; set; }
    }
}