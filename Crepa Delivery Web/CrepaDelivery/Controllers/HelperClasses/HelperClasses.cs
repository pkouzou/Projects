using CrepaDelivery.Models;
using Microsoft.AspNet.Identity;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Security;

namespace CrepaDelivery.Controllers.HelperClasses
{
    public static class HelperClasses
    {

        public static string getTotalCourtPrice(List<CourtItem> courtItems)
        {
            double totalPrice = 0;
            foreach (CourtItem courtItem in courtItems)
            {
                totalPrice += courtItem.TotalPrice;
            }

            return doubleToString(totalPrice);
        }
        

        public static string doubleToString(double price)
        {
            string priceString = price + "";

            if (priceString.Split(',').Length < 2)
            {
                priceString += ",00";
            }
            else if (priceString.Split(',')[1].Length < 2)
            {
                priceString += "0";
            }

            return priceString;
        }

        public static string doubleToString(string price)
        {
            string priceString = price.Replace(".", ",");

            if (priceString.Split(',').Length < 2)
            {
                priceString += ",00";
            }
            else if (priceString.Split(',')[1].Length < 2)
            {
                priceString += "0";
            }

            return priceString;
        }

        public static List<string> getCrepeTypeList()
        {
            ApplicationDbContext db = new ApplicationDbContext();
            List<string> crepeTypes = new List<string>();
            foreach (CrepeType crepeType in db.CrepeTypes.ToList())
            {
                crepeTypes.Add(crepeType.type);
            }
            return crepeTypes;
        }

        public static string getCartValueString(string email)
        {
            double cartValue = 0;

            using (ApplicationDbContext db = new ApplicationDbContext())
            {
                foreach (CourtItem courtItem in db.CourtItems.ToList().Where(x => x.Email.Equals(email)))
                {
                    cartValue += courtItem.Price * courtItem.Quantity;
                }
            }
            return doubleToString(cartValue);
        }
    }
}