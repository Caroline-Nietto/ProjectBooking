import java.util.Date;

public class Booking {
    public String firstname;
    public String lastname;
    public int totalprice;
    public boolean depositpaid;

    public DATES bookingdates;

    public String ckeckin;
    public String checkout;

    public static class DATES {

       private final String checkout;
        private final String checkin;

        DATES(String checkout, String checkin){
            this.checkin = checkin;
            this.checkout = checkout;
        }
    }


    public String additionalneeds;
}
