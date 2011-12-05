package com.aug3.sys.captcha;

import java.awt.Color;
import java.util.Random;

import org.patchca.color.GradientColorFactory;
import org.patchca.color.RandomColorFactory;
import org.patchca.color.SingleColorFactory;
import org.patchca.filter.predefined.CurvesRippleFilterFactory;
import org.patchca.filter.predefined.DiffuseRippleFilterFactory;
import org.patchca.filter.predefined.MarbleRippleFilterFactory;
import org.patchca.filter.predefined.WobbleRippleFilterFactory;
import org.patchca.service.Captcha;
import org.patchca.service.ConfigurableCaptchaService;

public class PatchcaCaptchaService implements CaptchaService {
    
    @Override
    public Challenge getChallenge() {
        Captcha captcha = newCaptcha();
        return new Challenge(captcha.getChallenge(), captcha.getImage());
    }
    
    private Captcha newCaptcha() {
        ConfigurableCaptchaService cs = new ConfigurableCaptchaService();
        Random random = new Random();
        int i = random.nextInt(3);
        switch (i) {
            case 0:
                cs.setColorFactory(new RandomColorFactory());
                break;
            case 1:
                cs.setColorFactory(new GradientColorFactory());
                break;
            default:
                cs.setColorFactory(new SingleColorFactory(new Color(20, 119, 6)));
        }
        i = random.nextInt(4);
        switch (i) {
            case 0:
                cs.setFilterFactory(new CurvesRippleFilterFactory(cs.getColorFactory()));
                break;
            case 1:
                cs.setFilterFactory(new MarbleRippleFilterFactory());
                break;
            case 2:
                cs.setFilterFactory(new WobbleRippleFilterFactory());
                break;
            default:
                cs.setFilterFactory(new DiffuseRippleFilterFactory());
        }
        return cs.getCaptcha();
    }

}
