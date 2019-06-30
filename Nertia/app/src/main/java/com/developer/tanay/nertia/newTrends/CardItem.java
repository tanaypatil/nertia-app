package com.developer.tanay.nertia.newTrends;

/**
 * Created by Tanay on 10-Jan-18.
 */

public class CardItem {
    private String title;
    private String text;
    //private int imgResId;
    private String card_img_url;

    public String getCard_img_url() {
        return card_img_url;
    }

    public void setCard_img_url(String card_img_url) {
        this.card_img_url = card_img_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /*public int getImgResId() {
        return imgResId;
    }

    public void setImgResId(int imgResId) {
        this.imgResId = imgResId;
    }
    */
}
