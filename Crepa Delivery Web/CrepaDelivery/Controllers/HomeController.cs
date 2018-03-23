using CrepaDelivery.Controllers.HelperClasses;
using CrepaDelivery.Models;
using CrepaDelivery.Models.Helpers;
using Microsoft.AspNet.Identity;
using System;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Linq;
using System.Net;
using System.Web;
using System.Web.Mvc;
using System.Web.Script.Serialization;
using System.Web.Services;

namespace CrepaDelivery.Controllers
{
    public class HomeController : Controller
    {

        private ApplicationDbContext db = new ApplicationDbContext();

        [WebMethod]
        [Authorize]
        public void addItemToCourt(string ingredientsToString, string crepeSize, string price, string quantity)
        {
            ApplicationDbContext db = new ApplicationDbContext();
            CourtItem courtItem = new CourtItem();
            courtItem.Email = User.Identity.Name;
            courtItem.CrepeSize = crepeSize;
            courtItem.Ingredients = ingredientsToString;
            courtItem.Price = Double.Parse(price, CultureInfo.InvariantCulture);
            courtItem.Quantity = Int32.Parse(quantity);
            courtItem.TotalPrice = courtItem.Quantity * courtItem.Price;
            db.CourtItems.Add(courtItem);
            db.SaveChanges();
        }

        [WebMethod]
        public void removeCourtItem(string id)
        {
            CourtItem courtItem = new CourtItem() { ID = Int32.Parse(id) };
            db.CourtItems.Attach(courtItem);
            db.CourtItems.Remove(courtItem);
            db.SaveChanges();
        }

        [WebMethod]
        public string getPartialCartValue()
        {
            return HelperClasses.HelperClasses.getCartValueString(User.Identity.Name);
        }


        public ActionResult Index()
        {
            if (User.IsInRole("Administrator"))
            {
                return RedirectToAction("Index", "Orders");
            }

            CrepeMakeModel crepeMakeModel = new CrepeMakeModel();
            return View(crepeMakeModel.getIngredientsByCategoriesDict());
        }

        public ActionResult Cart()
        {
            return View(GetCourtItemList());
        }

        public List<CourtItem> GetCourtItemList()
        {
            var courtItems = from CourtItem in db.CourtItems
                             where CourtItem.Email == User.Identity.Name
                             select CourtItem;

            return courtItems.ToList();
        }


        public ActionResult DeliveryInfo()
        {
            return View(GetDeliveryInfoViewModel());
        }

        public DeliveryInfoViewModel GetDeliveryInfoViewModel()
        {
            var deliveryInfo = from DeliveryInfo in db.DeliveryInfo
                               where DeliveryInfo.UserEmail == User.Identity.Name
                               select DeliveryInfo;

            DeliveryInfoViewModel deliveryInfoViewModel = new DeliveryInfoViewModel();

            if (deliveryInfo.ToList().Count() > 0)
            {
                DeliveryInfoModel deliveryInfoModel = deliveryInfo.ToList()[0];
                deliveryInfoViewModel.FirstName = deliveryInfoModel.FirstName;
                deliveryInfoViewModel.LastName = deliveryInfoModel.LastName;
                deliveryInfoViewModel.Address = deliveryInfoModel.Address;
                deliveryInfoViewModel.Floor = deliveryInfoModel.Floor;
                deliveryInfoViewModel.PhoneNumber = deliveryInfoModel.PhoneNumber;
            }

            return deliveryInfoViewModel;
        }


        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult DeliveryInfo(DeliveryInfoViewModel deliveryInfoViewModel)
        {
            var deliveryInfo = from DeliveryInfo in db.DeliveryInfo
                               where DeliveryInfo.UserEmail == User.Identity.Name
                               select DeliveryInfo;

            var list = deliveryInfo.ToList();
            DeliveryInfoModel deliveryInfoModel = null;
            if (list.Count() == 0)
            {
                deliveryInfoModel = new Models.DeliveryInfoModel();
                deliveryInfoModel.UserEmail = User.Identity.Name;
                db.DeliveryInfo.Add(deliveryInfoModel);
                db.SaveChanges();
            }
            else
            {
                deliveryInfoModel = list[0];
            }

            if (!String.IsNullOrWhiteSpace(deliveryInfoViewModel.FirstName)) deliveryInfoModel.FirstName = deliveryInfoViewModel.FirstName;
            if (!String.IsNullOrWhiteSpace(deliveryInfoViewModel.LastName)) deliveryInfoModel.LastName = deliveryInfoViewModel.LastName;
            if (!String.IsNullOrWhiteSpace(deliveryInfoViewModel.Address)) deliveryInfoModel.Address = deliveryInfoViewModel.Address;
            if (!String.IsNullOrWhiteSpace(deliveryInfoViewModel.Floor)) deliveryInfoModel.Floor = deliveryInfoViewModel.Floor;
            if (!String.IsNullOrWhiteSpace(deliveryInfoViewModel.PhoneNumber)) deliveryInfoModel.PhoneNumber = deliveryInfoViewModel.PhoneNumber;

            db.SaveChanges();
            return View();
        }

        public ActionResult Checkout()
        {
            CheckoutViewModel checkoutViewModel = new CheckoutViewModel();
            DeliveryInfoViewModelRequired deliveryInfoViewModelRequired = new DeliveryInfoViewModelRequired();

            DeliveryInfoViewModel deliveryInfoModel = GetDeliveryInfoViewModel();
            deliveryInfoViewModelRequired.UserEmail = User.Identity.Name;
            deliveryInfoViewModelRequired.FirstName = deliveryInfoModel.FirstName;
            deliveryInfoViewModelRequired.LastName = deliveryInfoModel.LastName;
            deliveryInfoViewModelRequired.DeliverEmail = deliveryInfoModel.DeliverEmail;
            deliveryInfoViewModelRequired.Address = deliveryInfoModel.Address;
            deliveryInfoViewModelRequired.Floor = deliveryInfoModel.Floor;
            deliveryInfoViewModelRequired.PhoneNumber = deliveryInfoModel.PhoneNumber;

            checkoutViewModel.DeliveryInfoViewModelRequired = deliveryInfoViewModelRequired;
            checkoutViewModel.CourtItems = GetCourtItemList();


            return View(checkoutViewModel);
        }

        [HttpPost]
        public ActionResult Checkout(CheckoutViewModel checkoutViewModel)
        {
            checkoutViewModel.CourtItems = GetCourtItemList();
            OrderModel orderModel = new OrderModel();

            orderModel.FullName = checkoutViewModel.DeliveryInfoViewModelRequired.FirstName + " " + checkoutViewModel.DeliveryInfoViewModelRequired.LastName;
            orderModel.Address = checkoutViewModel.DeliveryInfoViewModelRequired.Address + " " + checkoutViewModel.DeliveryInfoViewModelRequired.Floor;
            orderModel.Phone = checkoutViewModel.DeliveryInfoViewModelRequired.PhoneNumber;
            orderModel.Email = User.Identity.Name;
            orderModel.DeliverEmail = checkoutViewModel.DeliveryInfoViewModelRequired.UserEmail;
            orderModel.TotalCourtPrice = HelperClasses.HelperClasses.getTotalCourtPrice(checkoutViewModel.CourtItems);
            orderModel.OrderDate = DateTime.Now;
            orderModel.Completed = 0;
            db.Orders.Add(orderModel);
            db.SaveChanges();

            foreach (CourtItem courtItem in checkoutViewModel.CourtItems)
            {
                OrderDetailModel orderDetailModel = new OrderDetailModel();
                orderDetailModel.OrderID = orderModel.ID;
                orderDetailModel.Type = courtItem.CrepeSize;
                orderDetailModel.Ingredients = courtItem.Ingredients;
                orderDetailModel.Quantity = courtItem.Quantity;
                orderDetailModel.TotalPrice = courtItem.TotalPrice + "";
                db.OrderDetails.Add(orderDetailModel);
                db.SaveChanges();
            }

            db.CourtItems.RemoveRange(db.CourtItems.Where(x => x.Email == User.Identity.Name));
            db.SaveChanges();

            return RedirectToAction("OrderHistory");
        }


        public ActionResult OrderHistory()
        {
            var orders = from order in db.Orders
                         where order.Email == User.Identity.Name
                         select order;

            return View(orders.ToList());
        }
        
        public ActionResult OrderDetails(string id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            OrderModel orderModel = db.Orders.Find(Int32.Parse(id));

            if (orderModel == null)
            {
                return HttpNotFound();
            }
            return View(orderModel);
        }

        public ActionResult Catalogue()
        {
            var list = db.Crepes.ToList();
            return View(list);
        }

        public ActionResult Offers()
        {
            return View();
        }

        public ActionResult SoftDrinks()
        {
            return View(db.SoftDrinks.ToList());
        }

        [HttpPost]
        public ActionResult SoftDrinks(HttpPostedFileBase file)
        {
            if (file != null && file.ContentLength > 0)
            {
                var fileName = Path.GetFileName(file.FileName);
                var path = Path.Combine(Server.MapPath("~/Resources/"), fileName);
                file.SaveAs(path);
            }

            return RedirectToAction("Index");
        }

        public ActionResult About()
        {
            ViewBag.Message = "Your application description page.";

            return View();
        }

        public ActionResult Contact()
        {
            return View();
        }

        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Contact(ContactViewModel contactViewModel)
        {
            EmailSender emailSender = new EmailSender();
            emailSender.sendEmail(contactViewModel.Name, contactViewModel.Email, contactViewModel.Message);

            return RedirectToAction("Contact");
        }
    }
}