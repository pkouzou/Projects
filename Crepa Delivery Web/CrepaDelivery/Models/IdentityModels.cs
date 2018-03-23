using System.Data.Entity;
using System.Security.Claims;
using System.Threading.Tasks;
using Microsoft.AspNet.Identity;
using Microsoft.AspNet.Identity.EntityFramework;

namespace CrepaDelivery.Models
{
    // You can add profile data for the user by adding more properties to your ApplicationUser class, please visit http://go.microsoft.com/fwlink/?LinkID=317594 to learn more.
    public class ApplicationUser : IdentityUser
    {
        public async Task<ClaimsIdentity> GenerateUserIdentityAsync(UserManager<ApplicationUser> manager)
        {
            // Note the authenticationType must match the one defined in CookieAuthenticationOptions.AuthenticationType
            var userIdentity = await manager.CreateIdentityAsync(this, DefaultAuthenticationTypes.ApplicationCookie);
            // Add custom user claims here
            return userIdentity;
        }
    }

    public class ApplicationDbContext : IdentityDbContext<ApplicationUser>
    {
        public ApplicationDbContext()
            : base("DefaultConnection", throwIfV1Schema: false)
        {
        }

        public static ApplicationDbContext Create()
        {
            return new ApplicationDbContext();
        }

        public System.Data.Entity.DbSet<CrepaDelivery.Models.CrepeType> CrepeTypes { get; set; }

        public System.Data.Entity.DbSet<CrepaDelivery.Models.Ingredient> Ingredients { get; set; }

        public System.Data.Entity.DbSet<CrepaDelivery.Models.IngredientType> IngredientTypes { get; set; }

        public System.Data.Entity.DbSet<CrepaDelivery.Models.Crepe> Crepes { get; set; }

        public System.Data.Entity.DbSet<CrepaDelivery.Models.CourtItem> CourtItems { get; set; }

        public System.Data.Entity.DbSet<CrepaDelivery.Models.DeliveryInfoModel> DeliveryInfo { get; set; }

        public System.Data.Entity.DbSet<CrepaDelivery.Models.SoftDrink> SoftDrinks { get; set; }

        public System.Data.Entity.DbSet<CrepaDelivery.Models.OrderModel> Orders { get; set; }

        public System.Data.Entity.DbSet<CrepaDelivery.Models.OrderDetailModel> OrderDetails { get; set; }

        
    }
}