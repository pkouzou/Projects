using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Mail;
using System.Web;

namespace CrepaDelivery.Controllers.HelperClasses
{
    public class EmailSender
    {
        public void sendEmail(string name, string emailFrom, string messageContent)
        {
            var fromAddress = new MailAddress("pkouz@hotmail.com", "From Name");
            var toAddress = new MailAddress("", "To Name");
            const string fromPassword = "";
            string subject = "Feedback from " + emailFrom;
            string body = "Ο/H χρήστης <" + name + "> σας γράφει:\n\n" + messageContent + "\n\n\nΤο μήνυμα στάλθηκε από το Email: <" + emailFrom + ">.";

            var smtp = new SmtpClient
            {
                Host = "smtp.live.com",
                Port = 587,
                EnableSsl = true,
                DeliveryMethod = SmtpDeliveryMethod.Network,
                UseDefaultCredentials = false,
                Credentials = new NetworkCredential(fromAddress.Address, fromPassword)
            };
            using (var message = new MailMessage(fromAddress, toAddress)
            {
                Subject = subject,
                Body = body
            })
            {
                smtp.Send(message);
            }

        }
    }
}