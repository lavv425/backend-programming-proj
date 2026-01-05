package com.booker.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Handles all email notifications for the booking system.
 * Uses MailHog in development for testing emails without sending real ones.
 */
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${mail.from}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends a plain text email.
     * 
     * @param to      recipient email address
     * @param subject email subject
     * @param text    email body
     */
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }

    /**
     * Sends a welcome email after successful registration.
     * 
     * @param to        user's email address
     * @param firstName user's first name for personalization
     */
    public void sendRegistrationConfirmation(String to, String firstName) {
        String subject = "Benvenuto su Booker!";
        String text = String.format(
                "Ciao %s,\n\n" +
                        "La tua registrazione su Booker è stata completata con successo!\n\n" +
                        "Puoi ora effettuare il login e prenotare i tuoi appuntamenti.\n\n" +
                        "Il Team di Booker",
                firstName);

        sendEmail(to, subject, text);
    }

    /**
     * Notifies customer that their appointment has been confirmed.
     * 
     * @param to                  customer's email
     * @param customerName        customer's full name
     * @param professionalName    the professional they're booking with
     * @param serviceName         the service they booked
     * @param appointmentDateTime when the appointment is scheduled
     */
    public void sendAppointmentConfirmation(String to, String customerName, String professionalName,
            String serviceName, LocalDateTime appointmentDateTime) {
        String subject = "Conferma Appuntamento - Booker";
        String formattedDateTime = appointmentDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        String text = String.format(
                "Ciao %s,\n\n" +
                        "Il tuo appuntamento è stato confermato!\n\n" +
                        "Dettagli:\n" +
                        "- Professionista: %s\n" +
                        "- Servizio: %s\n" +
                        "- Data e Ora: %s\n\n" +
                        "Ti aspettiamo!\n\n" +
                        "Il Team di Booker",
                customerName, professionalName, serviceName, formattedDateTime);

        sendEmail(to, subject, text);
    }

    /**
     * Notifies customer about appointment cancellation.
     * 
     * @param to                  customer's email
     * @param customerName        customer's full name
     * @param serviceName         the service that was cancelled
     * @param appointmentDateTime when the appointment was scheduled
     */
    public void sendAppointmentCancellation(String to, String customerName, String serviceName,
            LocalDateTime appointmentDateTime) {
        String subject = "Cancellazione Appuntamento - Booker";
        String formattedDateTime = appointmentDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        String text = String.format(
                "Ciao %s,\n\n" +
                        "Il tuo appuntamento è stato cancellato.\n\n" +
                        "Dettagli:\n" +
                        "- Servizio: %s\n" +
                        "- Data e Ora: %s\n\n" +
                        "Puoi prenotare un nuovo appuntamento quando vuoi.\n\n" +
                        "Il Team di Booker",
                customerName, serviceName, formattedDateTime);

        sendEmail(to, subject, text);
    }

    /**
     * Confirms successful payment to the customer.
     * 
     * @param to           customer's email
     * @param customerName customer's full name
     * @param amount       formatted amount string (e.g., "50.00 EUR")
     * @param serviceName  the service they paid for
     */
    public void sendPaymentConfirmation(String to, String customerName, String amount, String serviceName) {
        String subject = "Conferma Pagamento - Booker";

        String text = String.format(
                "Ciao %s,\n\n" +
                        "Il tuo pagamento è stato completato con successo!\n\n" +
                        "Dettagli:\n" +
                        "- Servizio: %s\n" +
                        "- Importo: %s\n\n" +
                        "Grazie per aver scelto Booker!\n\n" +
                        "Il Team di Booker",
                customerName, serviceName, amount);

        sendEmail(to, subject, text);
    }

    /**
     * Notifies customer that their refund has been processed.
     * 
     * @param to           customer's email
     * @param customerName customer's full name
     * @param amount       formatted refund amount (e.g., "50.00 EUR")
     * @param serviceName  the service they got refunded for
     */
    public void sendRefundConfirmation(String to, String customerName, String amount, String serviceName) {
        String subject = "Conferma Rimborso - Booker";

        String text = String.format(
                "Ciao %s,\n\n" +
                        "Il tuo rimborso è stato elaborato con successo.\n\n" +
                        "Dettagli:\n" +
                        "- Servizio: %s\n" +
                        "- Importo rimborsato: %s\n\n" +
                        "L'importo sarà accreditato sul tuo metodo di pagamento originale entro 5-10 giorni lavorativi.\n\n"
                        +
                        "Il Team di Booker",
                customerName, serviceName, amount);

        sendEmail(to, subject, text);
    }
}
