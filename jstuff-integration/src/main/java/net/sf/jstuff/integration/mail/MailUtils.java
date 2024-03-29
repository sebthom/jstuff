/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.mail;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.*;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.activation.FileTypeMap;
import javax.mail.Authenticator;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.eclipse.jdt.annotation.NonNull;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class MailUtils {
   public static class Mail implements Serializable {
      private static final long serialVersionUID = 1L;

      public File[] attachments;
      public String[] emailBCC;
      public String[] emailCC;
      public String emailFrom;
      public String emailReturnReceiptTo;
      public String[] emailTo;
      public boolean isPlainTextMessage = true;
      public String message;
      public String subject;
   }

   public static class MailServer implements Serializable {
      private static final long serialVersionUID = 1L;

      public String smtpHostname;
      public String smtpPassword;
      public int smtpPort = 25;
      public String smtpUsername;
   }

   public static void sendMail(final @NonNull Mail mail, final @NonNull MailServer mailServer) throws AddressException, MessagingException {
      Args.notNull("mail", mail);
      Args.notNull("mailServer", mailServer);
      Args.notNull("mailServer.smtpHostname", mailServer.smtpHostname);

      final var props = new Properties();
      props.put("mail.smtp.host", asNonNullUnsafe(mailServer.smtpHostname));
      props.put("mail.smtp.dsn.notify", "FAILURE");
      props.put("mail.smtp.port", Integer.toString(mailServer.smtpPort));
      final Session session;
      if (Strings.isEmpty(mailServer.smtpUsername)) {
         session = Session.getDefaultInstance(props, null);
      } else {
         props.put("mail.smtp.auth", "true");
         final var auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
               return new PasswordAuthentication(//
                  mailServer.smtpUsername, //
                  Strings.emptyIfNull(mailServer.smtpPassword) //
               );
            }
         };
         session = Session.getInstance(props, auth);
      }
      final var msg = new MimeMessage(session);
      msg.setFrom(new InternetAddress(mail.emailFrom));
      msg.setSubject(mail.subject);
      if (mail.emailTo != null) {
         for (final String item : mail.emailTo) {
            msg.addRecipient(RecipientType.TO, new InternetAddress(item));
         }
      }
      if (mail.emailCC != null) {
         for (final String item : mail.emailCC) {
            msg.addRecipient(RecipientType.CC, new InternetAddress(item));
         }
      }
      if (mail.emailBCC != null) {
         for (final String item : mail.emailBCC) {
            msg.addRecipient(RecipientType.BCC, new InternetAddress(item));
         }
      }
      if (mail.emailReturnReceiptTo != null) {
         msg.setHeader("Return-Receipt-To", mail.emailReturnReceiptTo);
      }
      final var mp = new MimeMultipart();
      final var text = new MimeBodyPart();
      text.setDisposition(Part.INLINE);
      text.setContent(mail.message, mail.isPlainTextMessage ? "text/plain" : "text/html");
      mp.addBodyPart(text);

      if (mail.attachments != null) {
         final var fileTypeMap = new FileTypeMap() {
            @Override
            public String getContentType(final File file) {
               return getContentType(file.getName());
            }

            @Override
            public String getContentType(final String fileName) {
               if (fileName.toLowerCase().endsWith(".gif"))
                  return "image/gif";
               if (fileName.toLowerCase().endsWith(".png"))
                  return "image/png";
               if (fileName.toLowerCase().endsWith(".pdf"))
                  return "application/pdf";
               return "application/octet-stream";
            }
         };

         for (final File file : mail.attachments) {
            final var filePart = new MimeBodyPart();
            final var fds = new FileDataSource(file);
            fds.setFileTypeMap(fileTypeMap);
            final var dh = new DataHandler(fds);
            filePart.setFileName(file.getName());
            filePart.setDisposition(Part.ATTACHMENT);
            filePart.setDescription("Attached file: " + file.getName());
            filePart.setDataHandler(dh);
            mp.addBodyPart(filePart);
         }
      }
      msg.setContent(mp);
      msg.setSentDate(new Date());
      msg.saveChanges();

      Transport.send(msg);

      /*Transport transport = session.getTransport("smtp");
       transport.connect(smtpHostname, smtpUsername, smtpPassword);
       transport.sendMessage(msg, msg.getAllRecipients());
       transport.close();*/
   }
}
