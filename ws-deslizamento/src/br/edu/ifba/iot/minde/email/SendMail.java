package br.edu.ifba.iot.minde.email;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail {

	private static SendMail email = null;

	private SendMail() {

	}

	public static SendMail getInstancia() {
		if (email == null) {
			email = new SendMail();
		}
		return email;
	}

	public static void sendMail(int vibracao, int chuva, int umidade) {
		Properties props = new Properties();
		/** Parâmetros de conexão com servidor Gmail */
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
		final Credenciais auth = new Credenciais();

		Session session = Session.getDefaultInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(auth.getEmail(), auth
								.getSenha());
					}
				});

		/** Ativa Debug para sessão */
		session.setDebug(true);

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(auth.getEmail())); // Remetente

			Address[] toUser = InternetAddress // Destinatário(s)
					.parse("allexandresss@gmail.com, italomirandadenovais@gmail.com");
			message.setRecipients(Message.RecipientType.TO, toUser);
			message.setSubject("[MINDE] Alerta!");// Assunto
			message.setText("Alerta de deslizamento!"
					+ "\n Vibração detectada: " + vibracao + "."
					+ "\n Incidência de Chuva: " + chuva + "%."
					+ "\n Umidade do solo: " + umidade + "%.");
			/** Método para enviar a mensagem criada */
			Transport.send(message);

			System.out.println("Mensagem enviada!");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
}