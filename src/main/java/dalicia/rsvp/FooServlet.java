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
import java.util.Properties;
import java.util.logging.Logger;

public class FooServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(FooServlet.class.getName());

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().println("hello servlet");

        log.info("yeah, got a request");

        sendEmail();


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
