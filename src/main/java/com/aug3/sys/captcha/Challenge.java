package com.aug3.sys.captcha;

import java.awt.image.BufferedImage;

public class Challenge {

    private String code;
    private BufferedImage image;
    
    public Challenge(String code, BufferedImage image) {
        super();
        this.code = code;
        this.image = image;
    }

    public String getCode() {
        return code;
    }
    
    public BufferedImage getImage() {
        return image;
    }
    
}
