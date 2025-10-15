package my.dw.ccl.domain;

import lombok.Data;

import java.util.Objects;

@Data
public class Vendor {

    public static String DEFAULT_VENDOR_NAME = "Main Vendor";

    private final String name;

    public Vendor() {
        this.name = DEFAULT_VENDOR_NAME;
    }

    public Vendor(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Vendor: " + name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Vendor vendor = (Vendor) o;

        return Objects.equals(name, vendor.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
