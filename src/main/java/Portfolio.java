import java.math.BigDecimal;
import java.math.RoundingMode;

public class Portfolio {
    private CC crypto;
    private BigDecimal volume, initialpricePerCoin;
    public Portfolio() {
    }

    public Portfolio(CC crypto, BigDecimal volume, BigDecimal initialpricePerCoin) {
        this.crypto = crypto;
        this.volume = volume;
        this.initialpricePerCoin = initialpricePerCoin;
    }

    public CC getCrypto() {
        return crypto;
    }

    public void setCrypto(CC crypto) {
        this.crypto = crypto;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume.setScale(1, RoundingMode.HALF_UP);
    }

    public BigDecimal getInitialpricePerCoin() {
        return initialpricePerCoin;
    }

    public void setInitialpricePerCoin(BigDecimal initialpricePerCoin) {
        this.initialpricePerCoin = initialpricePerCoin.setScale(1, RoundingMode.HALF_UP);
    }

    public BigDecimal initalTotalAssetValue(){
        return this.initialpricePerCoin.multiply(this.volume);
    }

    @Override
    public String toString() {
        String ticker = "[" + this.crypto.getTicker()+ "]";
        return String.format("%-10s %-10s %10s", ticker, volume.toString(), "$" + initialpricePerCoin.toString() );
    }


}

