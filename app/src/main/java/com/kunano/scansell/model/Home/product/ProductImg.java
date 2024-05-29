package com.kunano.scansell.model.Home.product;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

@Entity(tableName = "productImg",
        primaryKeys = {"businessIdFK", "productIdFk"},
        foreignKeys = @ForeignKey(entity = Product.class,
                parentColumns = {"productId", "businessIdFK"},
                childColumns = {"productIdFk", "businessIdFK"},
                onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = {"productIdFk", "businessIdFK"}, unique = true)})
public class ProductImg {

    @NonNull
    private String productIdFk;

    @NonNull
    private Long businessIdFK;

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte[] img;


    @Ignore
    public ProductImg(){
    }
    public ProductImg(String productIdFk, byte[] img, Long businessIdFK) {
        this.productIdFk = productIdFk;
        this.img = img;
        this.businessIdFK = businessIdFK;
    }

    public  String getProductIdFk() {
        return productIdFk;
    }

    public void setProductIdFk( String productIdFk) {
        this.productIdFk = productIdFk;
    }

    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }

    public Long getBusinessIdFK() {
        return businessIdFK;
    }

    public void setBusinessIdFK(Long businessIdFK) {
        this.businessIdFK = businessIdFK;
    }
}
