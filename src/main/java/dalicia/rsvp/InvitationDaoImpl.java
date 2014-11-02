package dalicia.rsvp;

import java.util.List;

import com.google.common.base.Optional;

public class InvitationDaoImpl implements InvitationDao {
    @Override
    public List<Invitation> list() {
        return null;
    }

    @Override
    public Optional<Invitation> load(String code) {
        if (code.equals("marvelousmilnes")) {
            Invitation invitation = new Invitation();
            invitation.setCode(code);
            invitation.setName("Rae & Chris");
            invitation.setMaxGuests(2);
            return Optional.fromNullable(invitation);
        }

        return Optional.absent();
    }

    @Override
    public void save(Invitation invitation) {
    }
}
