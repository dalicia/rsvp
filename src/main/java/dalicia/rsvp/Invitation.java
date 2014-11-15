package dalicia.rsvp;

public class Invitation {
    private String code;
    private String name;

    private int primaryGuests;
    private boolean additionalGuestAllowed;

    private Integer seatsReserved;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
}
