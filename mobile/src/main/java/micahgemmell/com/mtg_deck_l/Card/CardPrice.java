
package micahgemmell.com.mtg_deck_l.Card;

import com.google.gson.annotations.Expose;

//@Generated("org.jsonschema2pojo")
public class CardPrice {

    @Expose
    private String name;
    @Expose
    private String low;
    @Expose
    private String med;
    @Expose
    private String high;

    /**
     *
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     *     The low
     */
    public String getLow() {
        return low;
    }

    /**
     *
     * @param low
     *     The low
     */
    public void setLow(String low) {
        this.low = low;
    }

    /**
     *
     * @return
     *     The med
     */
    public String getMed() {
        return med;
    }

    /**
     *
     * @param med
     *     The med
     */
    public void setMed(String med) {
        this.med = med;
    }

    /**
     *
     * @return
     *     The high
     */
    public String getHigh() {
        return high;
    }

    /**
     *
     * @param high
     *     The high
     */
    public void setHigh(String high) {
        this.high = high;
    }

}
