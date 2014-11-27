package dalicia.rsvp;

import static dalicia.rsvp.JsonHelper.newLenientObjectMapper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import dalicia.rsvp.protocol.ErrorResponse;
import dalicia.rsvp.protocol.SuccessResponse;

public class LoginServlet extends HttpServlet {
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

        code = Invitation.canonicalizeCode(code);

        Optional<Invitation> invitation = invitationDao.load(code);

        if (!invitation.isPresent()) {
            log.info("bad code: '" + code + "'");
            response.getWriter().println(objectMapper.writeValueAsString(new ErrorResponse("badCode", "Unrecognized code: '" + code + "'. Having problems logging in? Feel free to email us at daveandalicia2015@icloud.com")));
            return;
        }

        log.info("got invitation: " + invitation.get());

        response.getWriter().println(objectMapper.writeValueAsString(new SuccessResponse(invitation.get())));
    }


}
