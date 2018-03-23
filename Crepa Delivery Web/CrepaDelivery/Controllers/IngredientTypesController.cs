using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Entity;
using System.Linq;
using System.Net;
using System.Web;
using System.Web.Mvc;
using CrepaDelivery.Models;
using CrepaDelivery.Models.Helpers;

namespace CrepaDelivery
{
    [Authorize(Roles = "Administrator")]
    public class IngredientTypesController : Controller
    {
        private ApplicationDbContext db = new ApplicationDbContext();

        // GET: IngredientTypes
        public ActionResult Index()
        {
            return View(db.IngredientTypes.ToList());
        }

        // GET: IngredientTypes/Details/5
        public ActionResult Details(string id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            IngredientType ingredientType = db.IngredientTypes.Find(id);
            if (ingredientType == null)
            {
                return HttpNotFound();
            }
            return View(ingredientType);
        }

        // GET: IngredientTypes/Create
        public ActionResult Create()
        {
            IngredientTypeViewModel ingredientTypeViewModel = new IngredientTypeViewModel();
            ingredientTypeViewModel.CrepeTypes = db.CrepeTypes.ToList();
            return View(ingredientTypeViewModel);
        }

        // POST: IngredientTypes/Create
        // To protect from overposting attacks, please enable the specific properties you want to bind to, for 
        // more details see http://go.microsoft.com/fwlink/?LinkId=317598.
        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Create([Bind(Include = "Type, CrepeCategory")] IngredientType ingredientType)
        {
            if (ModelState.IsValid)
            {
                db.IngredientTypes.Add(ingredientType);
                db.SaveChanges();
                return RedirectToAction("Index");
            }

            return View(ingredientType);
        }

        // GET: IngredientTypes/Edit/5
        public ActionResult Edit(string id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            IngredientType ingredientType = db.IngredientTypes.Find(id);
            if (ingredientType == null)
            {
                return HttpNotFound();
            }
            return View(ingredientType);
        }

        // POST: IngredientTypes/Edit/5
        // To protect from overposting attacks, please enable the specific properties you want to bind to, for 
        // more details see http://go.microsoft.com/fwlink/?LinkId=317598.
        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Edit([Bind(Include = "Type")] IngredientType ingredientType)
        {
            if (ModelState.IsValid)
            {
                db.Entry(ingredientType).State = EntityState.Modified;
                db.SaveChanges();
                return RedirectToAction("Index");
            }
            return View(ingredientType);
        }

        // GET: IngredientTypes/Delete/5
        public ActionResult Delete(string id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            IngredientType ingredientType = db.IngredientTypes.Find(id);
            if (ingredientType == null)
            {
                return HttpNotFound();
            }
            return View(ingredientType);
        }

        // POST: IngredientTypes/Delete/5
        [HttpPost, ActionName("Delete")]
        [ValidateAntiForgeryToken]
        public ActionResult DeleteConfirmed(string id)
        {
            IngredientType ingredientType = db.IngredientTypes.Find(id);
            db.IngredientTypes.Remove(ingredientType);
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
