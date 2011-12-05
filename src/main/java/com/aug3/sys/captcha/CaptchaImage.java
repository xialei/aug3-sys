package com.aug3.sys.captcha;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class CaptchaImage
 */
public class CaptchaImage extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public CaptchaImage() {
        super();
    }

    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
	    response.setContentType("image/png");
	    CaptchaService cs = new PatchcaCaptchaService();
	    Challenge challenge = cs.getChallenge();

	    HttpSession session = request.getSession();
        session.setAttribute(session.getId(), challenge.getCode());

        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(challenge.getImage(), "png", out);

        out.close();
	}

    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
	    doGet(request, response);
	}

}
