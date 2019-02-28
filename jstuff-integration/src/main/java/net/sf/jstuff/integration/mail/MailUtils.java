/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.mail;

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

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
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

   public static void sendMail(final Mail mail, final MailServer mailServer) throws AddressException, MessagingException {
      Args.notNull("mail", mail);
      Args.notNull("mailServer", mailServer);

      final Properties props = new Properties();
      props.put("mail.smtp.host", mailServer.smtpHostname);
      props.put("mail.smtp.dsn.notify", "FAILURE");
      props.put("mail.smtp.port", Integer.toString(mailServer.smtpPort));
      final Session session;
      if (Strings.isEmpty(mailServer.smtpUsername)) {
         session = Session.getDefaultInstance(props, null);
      } else {
         props.put("mail.smtp.auth", "true");
         final Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
               return new PasswordAuthentication(//
                  mailServer.smtpUsername, //
                  mailServer.smtpPassword == null ? "" : mailServer.smtpPassword//
               );
            }
         };
         session = Session.getInstance(props, auth);
      }
      final MimeMessage msg = new MimeMessage(session);
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
      final MimeMultipart mp = new MimeMultipart();
      final MimeBodyPart text = new MimeBodyPart();
      text.setDisposition(Part.INLINE);
      text.setContent(mail.message, mail.isPlainTextMessage ? "text/plain" : "text/html");
      mp.addBodyPart(text);

      final FileTypeMap fileTypeMap = new FileTypeMap() {
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

      if (mail.attachments != null) {
         for (final File file : mail.attachments) {
            final MimeBodyPart file_part = new MimeBodyPart();
            final FileDataSource fds = new FileDataSource(file);
            fds.setFileTypeMap(fileTypeMap);
            final DataHandler dh = new DataHandler(fds);
            file_part.setFileName(file.getName());
            file_part.setDisposition(Part.ATTACHMENT);
            file_part.setDescription("Attached file: " + file.getName());
            file_part.setDataHandler(dh);
            mp.addBodyPart(file_part);
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
