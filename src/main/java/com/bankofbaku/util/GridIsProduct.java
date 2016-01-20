package com.bankofbaku.util;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

@JsonDeserialize(using = GridIsProductDeserializer.class)
public class GridIsProduct implements java.io.Serializable {
    private static final long serialVersionUID = 7625295026454607175L;
    private boolean isProduct;

    public boolean isProduct() {
        return isProduct;
    }

    public void setProduct(boolean isProduct) {
        this.isProduct = isProduct;
    }

    public GridIsProduct(boolean isProduct) {
        this.isProduct = isProduct;
    }

    public GridIsProduct() {
    }
}