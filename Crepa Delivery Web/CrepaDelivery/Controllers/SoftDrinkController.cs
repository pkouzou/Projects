using CrepaDelivery.Models;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace CrepaDelivery.Controllers
{
    public class SoftDrinkController : Controller
    {

        private ApplicationDbContext db = new ApplicationDbContext();

        public ActionResult Index()
        {
            return View(db.SoftDrinks.ToList());
        }


        public ActionResult Create()
        {
            return View();
        }

        [HttpPost]
        public ActionResult Create(SoftDrink softDrink, HttpPostedFileBase file)
        {
            if (file != null && file.ContentLength > 0)
            {
                var fileName = Path.GetFileName(file.FileName);
                var path = Path.Combine(Server.MapPath("~/Resources/"), fileName);
                file.SaveAs(path);
                softDrink.ImagePath = "/Resources/" + fileName;
            }
            else
            {
                softDrink.ImagePath = "/Resources/default_softdrink.png";
            }

            db.SoftDrinks.Add(softDrink);
            db.SaveChanges();

            return RedirectToAction("Index");
        }
    }
}