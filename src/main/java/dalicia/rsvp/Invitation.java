package dalicia.rsvp;

public class Invitation {
    private String code;
    private String name;
    private int maxGuests;
    private Integer actualGuests;

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

    public int getMaxGuests() {
        return maxGuests;
    }

    public void setMaxGuests(int maxGuests) {
        this.maxGuests = maxGuests;
    }

    public Integer getActualGuests() {
        return actualGuests;
    }

    public void setActualGuests(Integer actualGuests) {
        this.actualGuests = actualGuests;
    }
}
