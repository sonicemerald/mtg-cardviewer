package micahgemmell.com.mtg_deck_l.Card;

        import java.util.ArrayList;
        import java.util.List;
        import com.google.gson.annotations.Expose;

//Generated by jsonschema2pojo.org
public class Set {

//    Expose
    private String name;
 //   Expose
    private String code;
   // Expose
    private String releaseDate;
    //Expose
    private String border;
    //Expose
    private String type;
    //@Expose
    private List<List<String>> booster = new ArrayList<List<String>>();
    @Expose
    private List<Card> cards = new ArrayList<Card>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getBorder() {
        return border;
    }

    public void setBorder(String border) {
        this.border = border;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<List<String>> getBooster() {
        return booster;
    }

    public void setBooster(List<List<String>> booster) {
        this.booster = booster;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

}



