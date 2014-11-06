package dalicia.rsvp;

import java.util.List;

import com.google.common.base.Optional;

public interface InvitationDao {
    List<Invitation> list();

    Optional<Invitation> load(String code);

    void saveResponse(String code, int numAttending);
}
