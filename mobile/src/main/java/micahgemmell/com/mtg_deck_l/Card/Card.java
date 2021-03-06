package micahgemmell.com.mtg_deck_l.Card;

        import java.util.ArrayList;
        import java.util.List;
        import com.google.gson.annotations.Expose;

//Generated by jsonschema2pojo.org
public class Card {

    @Expose
    private String layout;
    @Expose
    private String type;
    @Expose
    private List<String> types = new ArrayList<String>();
    @Expose
    private List<String> colors = new ArrayList<String>();
    @Expose
    private Integer multiverseid;
    @Expose
    private String name;
    @Expose
    private List<String> names = new ArrayList<String>();
    @Expose
    private List<String> subtypes = new ArrayList<String>();
    @Expose
    private Integer cmc;
    @Expose
    private String rarity;
    @Expose
    private String artist;
    @Expose
    private String power;
    @Expose
    private String toughness;
    @Expose
    private String loyalty;
    @Expose
    private String manaCost;
    @Expose
    private String text;
    @Expose
    private String flavor;
    @Expose
    private String number;
    @Expose
    private String imageName;
    @Expose
    private String magicCardsInfoCode;
    //@Expose
    private String set;
    private String setName;
    private String lowPrice;
    private String medPrice;
    private String highPrice;
    private String foilPrice;
    private boolean PriceHidden;

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public List<String> getColors() {
        return colors;
    }

    public void setColors(List<String> colors) {
        this.colors = colors;
    }

    public Integer getMultiverseid() {
        return multiverseid;
    }

    public void setMultiverseid(Integer multiverseid) {
        this.multiverseid = multiverseid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getNames() { return names; }

    public void setNames(List<String> names) { this.names = names; }

    public List<String> getSubtypes() {
        return subtypes;
    }

    public void setSubtypes(List<String> subtypes) {
        this.subtypes = subtypes;
    }

    public Integer getCmc() {
        return cmc;
    }

    public void setCmc(Integer cmc) {
        this.cmc = cmc;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getToughness() {
        return toughness;
    }

    public void setToughness(String toughness) {
        this.toughness = toughness;
    }

    public String getLoyalty() {
        return loyalty;
    }

    public void setLoyalty(String loyalty) {
        this.loyalty = loyalty;
    }

    public String getManaCost() {
        return manaCost;
    }

    public void setManaCost(String manaCost) {
        this.manaCost = manaCost;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFlavor() {
        return flavor;
    }

    public void setFlavor(String flavor) {
        this.flavor = flavor;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getMagicCardsInfoCode() {
        return magicCardsInfoCode;
    }

    public void setMagicCardsInfoCode(String magicCardsInfoCode) {
        this.magicCardsInfoCode = magicCardsInfoCode;
    }

    public String getSet() {return set;}

    public void setSet(String set) {
        this.set = set;
    }

    public String getSetName() {return setName;}

    public void setSetName(String setName) { this.setName = setName; }

    public String getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(String lowPrice) {
        this.lowPrice = lowPrice;
    }

    public String getMedPrice() {
        return medPrice;
    }

    public void setMedPrice(String medPrice) {
        this.medPrice = medPrice;
    }

    public String getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(String highPrice) {
        this.highPrice = highPrice;
    }

    public String getFoilPrice() {
        return foilPrice;
    }

    public void setFoilPrice(String foilPrice) {
        this.foilPrice = foilPrice;
    }

    public boolean isPriceHidden() {
        return PriceHidden;
    }

    public void setPriceHidden(boolean priceHidden) {
        PriceHidden = priceHidden;
    }


}