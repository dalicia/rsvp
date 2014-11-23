package dalicia.rsvp;

import java.util.Locale;

import com.google.common.base.CharMatcher;

public class Invitation {
    private String code;
    private String name;

    private int primaryGuests;
    private boolean additionalGuestAllowed;

    private Integer seatsReserved;


    public Invitation code(String code) {
        setCode(code);
        return this;
    }

    public Invitation plusOne() {
        setAdditionalGuestAllowed(true);
        return this;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = canonicalizeCode(code);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAdditionalGuestAllowed() {
        return additionalGuestAllowed;
    }

    public void setAdditionalGuestAllowed(boolean additionalGuestAllowed) {
        this.additionalGuestAllowed = additionalGuestAllowed;
    }

    public int getPrimaryGuests() {
        return primaryGuests;
    }

    public void setPrimaryGuests(int primaryGuests) {
        this.primaryGuests = primaryGuests;
    }

    public Integer getSeatsReserved() {
        return seatsReserved;
    }

    public void setSeatsReserved(Integer seatsReserved) {
        this.seatsReserved = seatsReserved;
    }

    public static String canonicalizeCode(String code) {
        code = code.toLowerCase(Locale.ENGLISH);
        code = CharMatcher.JAVA_LETTER_OR_DIGIT.retainFrom(code);
        return code;
    }
}
