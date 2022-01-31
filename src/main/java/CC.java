import java.math.BigDecimal;
import java.math.RoundingMode;

public class CC {
    private int rank;
    private String name, ticker;
    private BigDecimal price;

    public CC() {
    }

    public CC(int rank, String name, String ticker, BigDecimal price) {
        this.rank = rank;
        this.name = name;
        this.ticker = ticker;
        this.price = price;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price.setScale(1, RoundingMode.HALF_UP);
    }
}
