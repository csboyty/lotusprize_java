package com.zhongyi.lotusprize.web.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.util.Config;

@Controller
public class CaptchaController {


    @Autowired
    @Qualifier("kaptchaConfig")
    private Config kaptchaConfig;

    private Producer kaptchaProducer = null;

    @PostConstruct
    public void setup() {
        this.kaptchaProducer = kaptchaConfig.getProducerImpl();

    }

    /**
     * @param @param  req
     * @param @param  resp
     * @param @throws ServletException
     * @param @throws IOException    参数类型
     * @return void    返回类型
     * @throws
     * @Title: captcha
     * @Description: 刷出验证码图片
     */
    @RequestMapping(value = "/captcha.jpg", method = RequestMethod.GET)
    public void captcha(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Set standard HTTP/1.1 no-cache headers.
        resp.setHeader("Cache-Control", "no-store, no-cache");
        // Set standard HTTP/1.0 no-cache headers.
        resp.setHeader("Pragma", "no-cache");
        resp.setDateHeader("Expires", 0);

        // return a jpeg
        resp.setContentType("image/jpeg");

        // create the text for the image
        String capText = this.kaptchaProducer.createText();

        // store the text in the session
        Session s = SecurityUtils.getSubject().getSession();
        
        s.setAttribute(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY, capText);

        BufferedImage bi = this.kaptchaProducer.createImage(capText);
        try(ServletOutputStream out = resp.getOutputStream()){
        	ImageIO.write(bi, "jpg", out);
        }
        
    }


}
