package com.kunano.scansell_native.model.Home.product;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "productImg",
        foreignKeys = @ForeignKey(entity = Product.class,
                parentColumns = "productId",
                childColumns = "productIdFk",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("productIdFk")})
public class ProductImg {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private Long imgId;

    private String productIdFk;

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte[] img;


    public ProductImg(String productIdFk, byte[] img) {
        this.imgId = imgId;
        this.productIdFk = productIdFk;
        this.img = img;
    }

    public Long getImgId() {
        return imgId;
    }

    public void setImgId(long imgId) {
        this.imgId = imgId;
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
}
