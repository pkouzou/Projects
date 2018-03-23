using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace CrepaDelivery.Models.Helpers
{
    public class ViewModels
    {

    }

    public class IngredientTypeViewModel
    {
        public IngredientType IngredientType { get; set; }

        public List<CrepeType> CrepeTypes { get; set; }
    }

    public class IngredientViewModel
    {
        public Ingredient Ingredient { get; set; }

        public List<IngredientType> IngredientTypes { get; set; }
    }

    public class CrepeMakePartialViewModel
    {
        public string CrepeCategoryID { get; set; }
        public Dictionary<string, List<Ingredient>> IngredientsDictionary { get; set; }

        public List<Ingredient> IngredientsAdded { get; set; }

    }

    public class CrepeMakeViewModel
    {
       
        public CrepeMakeViewModel()
        {
            //CrepeMakeModel = new CrepeMakeModel();
            //CrepeOrderModel = new CrepeOrderModel();
            //CrepeOrderModel.CrepesOrder = new List<Crepe>();
        }


        public CrepeMakeModel CrepeMakeModel { get; set; }

        public CrepeOrderModel CrepeOrderModel { get; set; }
    }

    public class MyAccountViewModel
    {
        public LoginViewModel LoginViewModel { get; set; }

        public RegisterViewModel RegisterViewModel { get; set; }
    }
    

}