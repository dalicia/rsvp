package dalicia.rsvp;

import java.util.Date;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.common.base.Optional;

public class InvitationDaoImpl implements InvitationDao {
    @Override
    public List<Invitation> list() {
        return null;
    }

    @Override
    public Optional<Invitation> load(String code) {
        Invitation invitation = null;

        if (code.equals("marvelousmilnes")) {
            invitation = new Invitation();
            invitation.setCode(code);
            invitation.setName("Rae & Chris");
            invitation.setMaxGuests(2);
        }

        if (invitation != null) {
            try {
                DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
                Entity response = datastore.get(KeyFactory.createKey("Response", invitation.getCode()));
                invitation.setActualGuests(((Number) response.getProperty("numAttending")).intValue());
            } catch (EntityNotFoundException e) {
                // no worries, just means they haven't responded yet!
            }
        }

        return Optional.fromNullable(invitation);
    }

    @Override
    public void saveResponse(String code, int numAttending) {
        Entity response = new Entity("Response", code);
        response.setProperty("timestamp", new Date());
        response.setProperty("numAttending", numAttending);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(response);
    }
}
