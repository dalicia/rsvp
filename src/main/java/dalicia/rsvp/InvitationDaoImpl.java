package dalicia.rsvp;

import static com.google.common.base.Preconditions.checkNotNull;
import static dalicia.rsvp.Invitation.canonicalizeCode;

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
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

public class InvitationDaoImpl implements InvitationDao {
    @Override
    public List<Invitation> list() {
        return null;
    }

    private final ImmutableMap<String, Invitation> invitationsByCode;

    public InvitationDaoImpl() {
        List<Invitation> guestList = ImmutableList.of(
                guests("Char", "Brian").code("Venn"),
                guests("Kristina", "Tse Wei").code("Beatty"),
                guests("Kate", "Summer").code("TwentyDogs"),
                guests("Steph", "Sei Wei").code("Counterpoint"),
                guests("Donna", "Eugene").code("PajamaParty"), // christina's parents
                guests("Ching Ching", "Charles").code("SweetTooth"),
                guests("Yvonne", "Donald").code("dsong88"),
                guests("Andrina").plusOne().code("SisterAct"),
                guests("Karen", "Tom").code("BuckyBadger"),
                guests("Laura", "Steve", "Susan", "Robert").code("Arsenal"),
                guests("Diane", "Dan").code("DanPan"),
                guests("Phil").plusOne().code("PhabulousPhil"),

                guests("Andrea", "Justin").code("GoEarth"),
                guests("Julia", "Matt").code("DoctorDoctor"),
                guests("Rae", "Chris").code("MarvelousMilnes"),
                guests("Huong", "Tom", "Sammy", "Spencer").code("Monger"),

                guests("Sophie", "Yuwen").code("RichTable"),
                guests("Diana", "Steve").code("MmmmRamen"),
                guests("Jason").plusOne().code("HeyMrDJ"),

                guests("Tracy").code("TracyYee"),
                guests("Sharon").code("SharonYee"),
                guests("Iris", "Charles").code("Tsang"),
                guests("Siu Wan", "Len Yan").code("NYNY"), // NY aunt & uncle
                guests("Jenny", "Jenny's Husband").code("TimGunn"), // NY cousin
                guests("Brian").code("101101"),  // NY cousin
                guests("Mary Ann").code("SocalGal"),
                guests("Mary Ann's Mom").code("NorcalMom"),
                guests("Grandma").code("Pawpaw"),

                // todo: revisit after dust settles
                guests("Alvin").code("Cthulhu"),
                guests("Claudia").code("Firaga"),

                guests("Renee", "Ben").code("Sharknado"),
                guests("Mark").plusOne().code("Elky"),
                guests("Lovina").plusOne().code("What'sUpDoc"),
                guests("Mike").plusOne().code("CrackShot"),   // Lawrence

                guests("Aunt Beth").plusOne().code("Montessori"),
                guests("Peter").plusOne().code("He++"),
                guests("Ellen").plusOne().code("GobletOfFire"),


                guests("Name").code("test1"),
                guests("Name").plusOne().code("test1+1"),
                guests("Name1", "Name2").code("test2"),
                guests("Name1", "Name2", "Name3", "Name4").code("test4")
        );

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
