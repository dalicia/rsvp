package dalicia.rsvp;

import static dalicia.rsvp.JsonHelper.newLenientObjectMapper;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.repackaged.com.google.common.base.Throwables;
import com.google.common.base.Optional;
import dalicia.rsvp.protocol.ErrorResponse;
import dalicia.rsvp.protocol.SuccessResponse;

public class RsvpServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(LoginServlet.class.getName());

    private static final ObjectMapper objectMapper = newLenientObjectMapper();

    private static final InvitationDao invitationDao = new InvitationDaoImpl();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doPost(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        String code = request.getParameter("code");
        if (code == null) {
            response.getWriter().println(objectMapper.writeValueAsString(new ErrorResponse("badRequest", "missing 'code' parameter")));
            return;
        }

        code = code.toLowerCase(Locale.ENGLISH);
        Optional<Invitation> invitation = invitationDao.load(code);

        if (!invitation.isPresent()) {
            log.info("bad code: '" + code + "'");
            response.getWriter().println(objectMapper.writeValueAsString(new ErrorResponse("badCode", "unrecognized code: '" + code + "'")));
            return;
        }

        log.info("got invitation: " + invitation.get());

        String numAttendingStr = request.getParameter("numAttending");
        if (numAttendingStr == null) {
            response.getWriter().println(objectMapper.writeValueAsString(new ErrorResponse("badRequest", "missing 'numAttending' parameter")));
            return;
        }

        final int numAttending;
        try {
            numAttending = Integer.parseInt(numAttendingStr);
        } catch (NumberFormatException e) {
            response.getWriter().println(objectMapper.writeValueAsString(new ErrorResponse("badRequest", "'numAttending' must be an integer")));
            return;
        }

        if (invitation.get().getMaxGuests() < numAttending) {
            response.getWriter().println(objectMapper.writeValueAsString(new ErrorResponse("badRequest", "'numAttending' must be <= max guests (" + invitation.get().getMaxGuests() + ")")));
            return;
        }

        invitation.get().setActualGuests(numAttending);

        try {
            log.info("Saving response {code='" + code + "' numAttending='" + numAttending + "'}");
            invitationDao.saveResponse(code, numAttending);
        } catch (Exception e) {
            sendErrorEmail(e);
            log.log(Level.SEVERE, "failed to save response", e);
            response.getWriter().println(objectMapper.writeValueAsString(new ErrorResponse("rsvpFailed",
                    "Oops, something went wrong -- sorry about that! If it happens again, please get in touch with us by email at <dnault@mac.com> or by phone at 650-242-8376.")));
            return;
        }

        response.getWriter().println(objectMapper.writeValueAsString(new SuccessResponse(invitation.get())));
        sendEmail(invitation.get());
    }

    private void sendErrorEmail(Exception error) {
        sendEmail("RSVP failure: " + error.getMessage(), Throwables.getStackTraceAsString(error));
    }

    private void sendEmail(String subject, String body) {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("dnault@gmail.com", "Dave Nault"));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress("aradia@gmail.com", "Alicia Ong"));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress("dnault@mac.com", "Dave Nault"));
            msg.setSubject(subject);
            msg.setText(body);
            Transport.send(msg);

        } catch (Exception e) {
            log.log(Level.SEVERE, "failed to send email", e);
        }
    }

    private void sendEmail(Invitation invitation) {
        try {
            String subject = "RSVP: " + invitation.getName() + " (" + invitation.getActualGuests() + ")";
            String body = objectMapper.writeValueAsString(invitation.getActualGuests());
            sendEmail(subject, body);
        } catch (Exception e) {
            log.log(Level.SEVERE, "failed to send email", e);
        }
    }
}
