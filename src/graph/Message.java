package graph;

import java.util.Date;

public class Message {
    public final byte[] data;
    public final String asText;
    public final double asDouble;
    public final Date date;

    public Message(String asText){
        this.asText=asText;
        this.date=new Date();
        this.data=asText.getBytes();
        double tempDouble;
        try{
            tempDouble = Double.parseDouble(asText);
        }catch(NumberFormatException e){
            tempDouble = Double.NaN;
        }
        this.asDouble = tempDouble;
        }

    public Message(final double asDouble) {
        this(String.valueOf(asDouble));
    }

    public Message(final byte[] data) {
        this(new String(data));
    }
}
