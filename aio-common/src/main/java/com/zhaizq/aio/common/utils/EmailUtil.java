package com.zhaizq.aio.common.utils;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Arrays;
import java.util.Objects;
import java.util.Properties;

/**
 * @author zhaizq
 * @date 2019-03-07 14:54:54
 * https://www.cnblogs.com/ysocean/p/7666061.html
 */
public class EmailUtil {
    public static SimpleEmail build(String useAddress, String usePassword, String host) throws MessagingException {
        return build(useAddress, usePassword, host, 465, "smtp", "true");
    }

    public static SimpleEmail build(String useAddress, String usePassword, String host, int port) throws MessagingException {
        return build(useAddress, usePassword, host, port, "smtp", "true");
    }

    public static SimpleEmail build(String useAddress, String usePassword, String host, int port, String protocol, String auth) throws MessagingException {
        Properties prop = new Properties();
        prop.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        prop.setProperty("mail.smtp.port", String.valueOf(port));
        prop.setProperty("mail.smtp.socketFactory.port", String.valueOf(port));
        prop.setProperty("mail.smtp.host", host);
        prop.setProperty("mail.smtp.auth", auth);
        prop.setProperty("mail.transport.protocol", protocol);
        Session session = Session.getInstance(prop);
//        session.setDebug(true);
        return new SimpleEmail(useAddress, usePassword, session);
    }

    public static class SimpleEmail {
        private final String useAddress;
        private final String usePassword;
        private final Transport transport;
        private final Session session;

        private SimpleEmail(String useAddress, String usePassword, Session session) throws MessagingException {
            this.useAddress = useAddress;
            this.usePassword = usePassword;
            this.session = session;
            this.transport = session.getTransport();
        }

        public void send(String to, String subject, String message) throws MessagingException {
            send(new String[]{to}, subject, message);
        }

        public void send(String[] to, String subject, String message) throws MessagingException {
            MimeBodyPart body = new MimeBodyPart();
            body.setContent(message, "text/html;charset=UTF-8");
            MimeMultipart mp = new MimeMultipart();
            mp.addBodyPart(body);
            send(to, subject, mp);
        }

        public void send(String to, String subject, MimeMultipart mp) throws MessagingException {
            send(new String[]{to},subject, mp);
        }

        public void send(String[] to, String subject, MimeMultipart mp) throws MessagingException {
            if (!transport.isConnected())
                transport.connect(useAddress, usePassword);

            to = Arrays.stream(to).filter(Objects::nonNull).filter(v -> !v.isEmpty()).toArray(String[]::new);
            if (to.length == 0) return;

            InternetAddress[] internetAddresses = new InternetAddress[to.length];
            for (int i = 0; i < to.length; i++) {
                internetAddresses[i] = new InternetAddress(to[i]);
            }

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(useAddress));
            message.setRecipients(Message.RecipientType.TO, internetAddresses);

            message.setSubject(subject, "UTF-8");
            message.setContent(mp);
            transport.sendMessage(message, message.getAllRecipients());
        }

        public void close() {
            try {
                if (transport != null) transport.close();
            } catch (MessagingException ignore) {}
        }
    }
}