package dalicia.rsvp;

import static com.google.common.base.Throwables.propagate;

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
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Optional;
import dalicia.rsvp.protocol.ErrorResponse;
import dalicia.rsvp.protocol.SuccessResponse;

public class LoginServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(LoginServlet.class.getName());

    private static final ObjectMapper objectMapper = newLenientObjectMapper();

    private static ObjectMapper newLenientObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        mapper.enable(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper;
    }

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

        response.getWriter().println(objectMapper.writeValueAsString(new SuccessResponse(invitation.get())));
//        sendEmail();
    }

    private void sendEmail() {

        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        String msgBody = "...";

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("dnault@gmail.com", "Dave Nault"));
            msg.addRecipient(Message.RecipientType.TO,
                    new InternetAddress("dnault@mac.com", "Dave Nault"));
            msg.setSubject("someone made a request");
            msg.setText(msgBody);
            Transport.send(msg);

        } catch (Exception e) {
            throw propagate(e);
        }
    }
}
