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
import com.google.common.collect.ImmutableSet;
import dalicia.rsvp.protocol.ErrorResponse;
import dalicia.rsvp.protocol.SuccessResponse;
import org.apache.commons.lang3.StringUtils;

public class RsvpServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(LoginServlet.class.getName());

    private static final ObjectMapper objectMapper = newLenientObjectMapper();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doPost(request, response);
    }

    public static class ResponseDetails {
        private final String whoami;
        private final String email;
        private final boolean coming;

        public ResponseDetails(String whoami, String email, boolean coming) {
            this.whoami = whoami;
            this.email = email;
            this.coming = coming;
        }

        public String getWhoami() {
            return whoami;
        }

        public String getEmail() {
            return email;
        }

        public boolean isComing() {
            return coming;
        }

        @Override
        public String toString() {
            return "Response{" +
                    "whoami='" + whoami + '\'' +
                    ", email='" + email + '\'' +
                    ", coming=" + coming +
                    '}';
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        String whoami = request.getParameter("whoami");
        if (StringUtils.isBlank(whoami)) {
            response.getWriter().println(objectMapper.writeValueAsString(new ErrorResponse("badRequest", "missing 'whoami' parameter")));
            return;
        }

        String email = request.getParameter("email");
        String comingStr = request.getParameter("coming").toLowerCase(Locale.ROOT);
        if (!ImmutableSet.of("true", "false").contains(comingStr)) {
            response.getWriter().println(objectMapper.writeValueAsString(new ErrorResponse("badRequest", "bad/or missing 'coming' parameter")));
            return;
        }

        boolean coming = Boolean.parseBoolean(comingStr);

        ResponseDetails responseDetails = new ResponseDetails(whoami, email, coming);

        try {
            log.info("Emailing response " + responseDetails);
            sendEmail(responseDetails);

        } catch (Exception e) {
            log.log(Level.SEVERE, "failed to email response for " + responseDetails, e);
            response.getWriter().println(objectMapper.writeValueAsString(new ErrorResponse("rsvpFailed",
                    "Oops, something went wrong -- sorry about that! If it happens again, please get in touch with us by email at daveandalicia2015@icloud.com or by phone at 650-242-8376.")));
            return;
        }

        response.getWriter().println(objectMapper.writeValueAsString(new SuccessResponse(responseDetails)));
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
            log.log(Level.SEVERE, "failed to send email, subject: " + subject + "  body: " + body, e);
        }
    }

    private void sendEmail(ResponseDetails responseDetails) {
        try {
            String subject = "RSVP: " + responseDetails.whoami + " (" + (responseDetails.coming ? "YES" : "NO") + ")";
            String body = objectMapper.writeValueAsString(responseDetails);
            sendEmail(subject, body);
        } catch (Exception e) {
            log.log(Level.SEVERE, "failed to send email for " + responseDetails, e);
        }
    }
}
