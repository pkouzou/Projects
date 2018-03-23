using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace CrepaDelivery.Models.Helpers
{
    public class HelperClasses
    {
    }

    public class IngredientTypeList
    {
        private List<List<SelectListItem>> list;
        private List<string> listLabels;
        private List<IngredientType> ingredientTypes;

        public IngredientTypeList(List<IngredientType> ingredientTypes)
        {
            this.ingredientTypes = ingredientTypes;
            createList();
        }

        public void createList()
        {
            list = new List<List<SelectListItem>>();
            listLabels = new List<string>();
            
            for (int i = 0; i < ingredientTypes.Count(); i++)
            {
                addIngredientType(ingredientTypes[i]);
            }

        }

        public void addIngredientType(IngredientType ingredientType)
        {
            for (int i = 0; i < list.Count(); i++)
            {
               if (listLabels[i].Equals(ingredientType.CrepeCategory))
                {
                    list[i].Add(new SelectListItem { Text = ingredientType.Type, Value = ingredientType.Type });
                    return;
                }
            }
            list.Add(new List<SelectListItem>());
            listLabels.Add(ingredientType.CrepeCategory);
            list.Last().Add(new SelectListItem { Text = ingredientType.Type, Value = ingredientType.Type});
        }
        
        public List<List<SelectListItem>> getList()
        {
            return list;
        }

        public List<string> getLabels()
        {
            return listLabels;
        }
    }


    public class CrepeMakeModel
    {
        private Dictionary<string, Dictionary<string, List<Ingredient>>> ingredientsByCategories;

        public CrepeMakeModel()
        {
            ingredientsByCategories = new Dictionary<string, Dictionary<string, List<Ingredient>>>();
            addIngredients();
        }

        public void addIngredients()
        {
            ApplicationDbContext db = new ApplicationDbContext();
            List<Ingredient> ingredients = db.Ingredients.ToList();
            for (int i = 0; i < ingredients.Count(); i++)
            {
                addIngredient(ingredients[i]);
            }
        }

        public void addIngredient(Ingredient ingredient)
        {
            if (!ingredientsByCategories.ContainsKey(ingredient.CrepeCategory))
            {
               ingredientsByCategories.Add(ingredient.CrepeCategory, new Dictionary<string, List<Ingredient>>());
            }
            if (!ingredientsByCategories[ingredient.CrepeCategory].ContainsKey(ingredient.IngredientType))
            {
                ingredientsByCategories[ingredient.CrepeCategory].Add(ingredient.IngredientType, new List<Ingredient>());
            }
            ingredientsByCategories[ingredient.CrepeCategory][ingredient.IngredientType].Add(ingredient);
        }

        public Dictionary<string, Dictionary<string, List<Ingredient>>> getIngredientsByCategoriesDict()
        {
            return ingredientsByCategories;
        }
        
    }
    
    public class CrepeOrderModel
    {
        public List<Crepe> CrepesOrder { get; set; }
    }

}