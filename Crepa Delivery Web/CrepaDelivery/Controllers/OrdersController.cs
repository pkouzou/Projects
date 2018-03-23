using CrepaDelivery.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using System.Web.Services;

namespace CrepaDelivery.Controllers
{
    public class OrdersController : Controller
    {
        private ApplicationDbContext db = new ApplicationDbContext();
        // GET: Orders
        public ActionResult Index()
        {
            var pending = db.Orders.ToList().Where(x => x.Completed == 0).ToList();
            var completed = db.Orders.ToList().Where(x => x.Completed == 1).ToList();
            pending.AddRange(completed);
            return View(pending);
        }


        [WebMethod]
        [Authorize]
        public void changeOrderStatus(string orderID, string status)
        {
            db.Orders.Find(Int32.Parse(orderID)).Completed = Int32.Parse(status);
            db.SaveChanges();
        }
    }
}