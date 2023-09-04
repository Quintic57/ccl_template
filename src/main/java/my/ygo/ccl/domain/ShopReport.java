package my.ygo.ccl.domain;

import lombok.Data;

@Data
public class ShopReport {

    private final StringBuilder reportBody;

    private Double subtotal;

    private Integer cardCount;

    public ShopReport() {
        this(new StringBuilder());
    }

    public ShopReport(final StringBuilder reportBody) {
        this.reportBody = reportBody;
    }

}
