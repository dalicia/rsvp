package dalicia.rsvp;

import static com.google.common.base.Preconditions.checkNotNull;
import static dalicia.rsvp.Invitation.canonicalizeCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.repackaged.com.google.common.collect.Iterables;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

public class InvitationDaoImpl implements InvitationDao {
    @Override
    public List<Invitation> list() {
        return null;
    }

    private final ImmutableMap<String, Invitation> invitationsByCode;

    public InvitationDaoImpl() {
        List<Invitation> guestList = new ArrayList<>();

        guestList.add(guests("Char", "Brian").code("Venn"));
        guestList.add(guests("Kristina", "Tse Wei").code("Beatty"));
        guestList.add(guests("Kate", "Summer").code("TwentyDogs"));
        guestList.add(guests("Steph", "Sei Wei").code("Counterpoint"));
        guestList.add(guests("Donna", "Eugene").code("PajamaParty")); // christina's parents
        guestList.add(guests("Ching Ching", "Charles").code("SweetTooth"));
        guestList.add(guests("Yvonne", "Donald").code("dsong88"));
        guestList.add(guests("Andrina").plusOne().code("SisterAct"));
        guestList.add(guests("Karen", "Tom").code("BuckyBadger"));
        guestList.add(guests("Laura", "Steve", "Susan", "Robert").code("Arsenal"));
        guestList.add(guests("Diane", "Dan").code("DanPan"));
        guestList.add(guests("Phil").plusOne().code("PhabulousPhil"));

        guestList.add(guests("Andrea", "Justin").code("GoEarth"));
        guestList.add(guests("Julia", "Matt").code("DoctorDoctor"));
        guestList.add(guests("Rae", "Chris").code("MarvelousMilnes"));
        guestList.add(guests("Huong", "Tom", "Sammy", "Spencer").code("Monger"));

        guestList.add(guests("Sophie", "Yuwen").code("RichTable"));
        guestList.add(guests("Diana", "Steve").code("MmmmRamen"));
        guestList.add(guests("Jason").plusOne().code("HeyMrDJ"));

        guestList.add(guests("Tracy").code("TracyYee"));
        guestList.add(guests("Sharon").code("SharonYee"));
        guestList.add(guests("Iris", "Charles").code("Tsang"));
        guestList.add(guests("Siu Wan", "Len Yan").code("NYNY")); // NY aunt & uncle
        guestList.add(guests("Jenny", "Jenny's Husband").code("TimGunn")); // NY cousin
        guestList.add(guests("Brian").code("101101"));  // NY cousin
        guestList.add(guests("Mary Ann").code("SocalGal"));
        guestList.add(guests("Mary Ann's Mom").code("NorcalMom"));
        guestList.add(guests("Grandma").code("Pawpaw"));

        // todo: revisit after dust settles
        // guestList.add(guests("Alvin").code("Cthulhu"));
        // guestList.add(guests("Claudia").code("Firaga"));

        guestList.add(guests("Renee", "Ben").code("Sharknado"));
        guestList.add(guests("Mark").plusOne().code("Elky"));
        guestList.add(guests("Lovina").plusOne().code("What'sUpDoc"));
        guestList.add(guests("Mike").plusOne().code("CrackShot"));   // Lawrence

        guestList.add(guests("Aunt Beth").plusOne().code("Montessori"));
        guestList.add(guests("Peter").plusOne().code("He++"));
        guestList.add(guests("Ellen").plusOne().code("GobletOfFire"));


        guestList.add(guests("Name").code("test1"));
        guestList.add(guests("Name").plusOne().code("test1+1"));
        guestList.add(guests("Name1", "Name2").code("test2"));
        guestList.add(guests("Name1", "Name2", "Name3", "Name4").code("test4"));

        this.invitationsByCode = ImmutableMap.copyOf(indexByCode(guestList));
    }

    public static void main(String[] args) {
        new InvitationDaoImpl().printGuestList();
    }

    public void printGuestList() {
        int primaryGuests = 0;
        int plusOnes = 0;
        for (Invitation i : invitationsByCode.values()) {
            if (i.getCode().startsWith("test")) {
                continue;
            }

            primaryGuests += i.getPrimaryGuests();
            if (i.isAdditionalGuestAllowed()) {
                plusOnes++;
            }
        }
        System.out.println("named guests: " + primaryGuests);
        System.out.println("plus ones: " + plusOnes);
        System.out.println("parties: " + invitationsByCode.size());
    }

    private Map<String, Invitation> indexByCode(Iterable<Invitation> invitations) {
        Map<String, Invitation> index = new HashMap<>();
        for (Invitation i : invitations) {
            checkNotNull(i.getCode(), "missing code for " + i.getName());
            index.put(i.getCode(), i);
        }
        return index;
    }

    @Override
    public Optional<Invitation> load(String code) {
        Invitation invitation = invitationsByCode.get(canonicalizeCode(code));

        if (invitation != null) {
            try {
                DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
                Entity response = datastore.get(KeyFactory.createKey("Response", invitation.getCode()));
                invitation.setSeatsReserved(((Number) response.getProperty("numAttending")).intValue());
            } catch (EntityNotFoundException e) {
                // no worries, just means they haven't responded yet!
            }
        }

        return Optional.fromNullable(invitation);
    }

    private static String join(String... names) {
        if (names.length == 1) {
            return names[0];
        }

        return Joiner.on(", ").join(Iterables.limit(Arrays.asList(names), names.length - 1)) + " & " + names[names.length - 1];
    }

    private Invitation guests(String... names) {
        Invitation i = new Invitation();
        i.setName(join(names));
        i.setPrimaryGuests(names.length);
        i.setAdditionalGuestAllowed(false);
        return i;
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
