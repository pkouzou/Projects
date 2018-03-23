using CrepaDelivery.Models.Helpers;
using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Entity;
using System.Globalization;
using System.Linq;
using System.Net;
using System.Web;
using System.Web.Mvc;
using System.Web.Services;

namespace CrepaDelivery.Models
{
    public class CrepesController : Controller
    {
        private ApplicationDbContext db = new ApplicationDbContext();

        [WebMethod]
        [Authorize]
        public void addCrepeToCrepes(string crepeType, string ingredientsToString, string normalPrice, string XLPrice)
        {
            ApplicationDbContext db = new ApplicationDbContext();
            Crepe crepe = new Crepe();
            crepe.Ingredients = ingredientsToString;
            crepe.Type = crepeType;
            crepe.NormalPrice = normalPrice;
            crepe.XLPrice = XLPrice;
            db.Crepes.Add(crepe);
            db.SaveChanges();
        }

        // GET: Crepes
        public ActionResult Index()
        {
            return View(db.Crepes.ToList());
        }

        // GET: Crepes/Details/5
        public ActionResult Details(int? id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            Crepe crepe = db.Crepes.Find(id);
            if (crepe == null)
            {
                return HttpNotFound();
            }
            return View(crepe);
        }

        // GET: Crepes/Create
        public ActionResult Create()
        {
            CrepeMakeModel crepeMakeModel = new CrepeMakeModel();
            return View(crepeMakeModel.getIngredientsByCategoriesDict());
        }

        // POST: Crepes/Create
        // To protect from overposting attacks, please enable the specific properties you want to bind to, for 
        // more details see http://go.microsoft.com/fwlink/?LinkId=317598.
        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Create([Bind(Include = "ID,Type,NormalPrice,XLPrice")] Crepe crepe)
        {
            if (ModelState.IsValid)
            {
                db.Crepes.Add(crepe);
                db.SaveChanges();
                return RedirectToAction("Index");
            }

            return View(crepe);
        }

        // GET: Crepes/Edit/5
        public ActionResult Edit(int? id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            Crepe crepe = db.Crepes.Find(id);
            if (crepe == null)
            {
                return HttpNotFound();
            }
            return View(crepe);
        }

        // POST: Crepes/Edit/5
        // To protect from overposting attacks, please enable the specific properties you want to bind to, for 
        // more details see http://go.microsoft.com/fwlink/?LinkId=317598.
        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Edit([Bind(Include = "ID,Type,NormalPrice,XLPrice")] Crepe crepe)
        {
            if (ModelState.IsValid)
            {
                db.Entry(crepe).State = EntityState.Modified;
                db.SaveChanges();
                return RedirectToAction("Index");
            }
            return View(crepe);
        }

        // GET: Crepes/Delete/5
        public ActionResult Delete(int? id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            Crepe crepe = db.Crepes.Find(id);
            if (crepe == null)
            {
                return HttpNotFound();
            }
            return View(crepe);
        }

        // POST: Crepes/Delete/5
        [HttpPost, ActionName("Delete")]
        [ValidateAntiForgeryToken]
        public ActionResult DeleteConfirmed(int id)
        {
            Crepe crepe = db.Crepes.Find(id);
            db.Crepes.Remove(crepe);
            db.SaveChanges();
            return RedirectToAction("Index");
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }
    }
}
