using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Entity;
using System.Linq;
using System.Net;
using System.Web;
using System.Web.Mvc;
using CrepaDelivery.Models;

namespace CrepaDelivery
{
    [Authorize(Roles = "Administrator")]
    public class CrepeTypesController : Controller
    {
        private ApplicationDbContext db = new ApplicationDbContext();

        // GET: CrepeTypes
        public ActionResult Index()
        {
            return View(db.CrepeTypes.ToList());
        }

        // GET: CrepeTypes/Details/5
        public ActionResult Details(string id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            CrepeType crepeType = db.CrepeTypes.Find(id);
            if (crepeType == null)
            {
                return HttpNotFound();
            }
            return View(crepeType);
        }

        // GET: CrepeTypes/Create
        public ActionResult Create()
        {
            return View();
        }

        // POST: CrepeTypes/Create
        // To protect from overposting attacks, please enable the specific properties you want to bind to, for 
        // more details see http://go.microsoft.com/fwlink/?LinkId=317598.
        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Create([Bind(Include = "type")] CrepeType crepeType)
        {
            if (ModelState.IsValid)
            {
                db.CrepeTypes.Add(crepeType);
                db.SaveChanges();
                return RedirectToAction("Index");
            }

            return View(crepeType);
        }

        // GET: CrepeTypes/Edit/5
        public ActionResult Edit(string id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            CrepeType crepeType = db.CrepeTypes.Find(id);
            if (crepeType == null)
            {
                return HttpNotFound();
            }
            return View(crepeType);
        }

        // POST: CrepeTypes/Edit/5
        // To protect from overposting attacks, please enable the specific properties you want to bind to, for 
        // more details see http://go.microsoft.com/fwlink/?LinkId=317598.
        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Edit([Bind(Include = "type")] CrepeType crepeType)
        {
            if (ModelState.IsValid)
            {
                db.Entry(crepeType).State = EntityState.Modified;
                db.SaveChanges();
                return RedirectToAction("Index");
            }
            return View(crepeType);
        }

        // GET: CrepeTypes/Delete/5
        public ActionResult Delete(string id)
        {
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            CrepeType crepeType = db.CrepeTypes.Find(id);
            if (crepeType == null)
            {
                return HttpNotFound();
            }
            return View(crepeType);
        }

        // POST: CrepeTypes/Delete/5
        [HttpPost, ActionName("Delete")]
        [ValidateAntiForgeryToken]
        public ActionResult DeleteConfirmed(string id)
        {
            CrepeType crepeType = db.CrepeTypes.Find(id);
            db.CrepeTypes.Remove(crepeType);
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
