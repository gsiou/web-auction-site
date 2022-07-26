package ru.shanalotte.servlets;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@WebServlet("/image/*")
public class ImageServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		ApplicationContext applicationContext = (AnnotationConfigApplicationContext) config.getServletContext().getAttribute("springcontext");
		final AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
		beanFactory.autowireBean(this);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String fileName = determineFileName(request);
		serveFileToUser(request, response, fileName);
	}

	private String determineFileName(HttpServletRequest request) throws UnsupportedEncodingException {
		return URLDecoder.decode(request.getPathInfo().substring(1), StandardCharsets.UTF_8);
	}

	private void serveFileToUser(HttpServletRequest request, HttpServletResponse response, String fileName) throws ServletException, IOException {
		File file = new File(getServletContext().getInitParameter("images.location"), fileName);
		response.setHeader("Content-Type", getServletContext().getMimeType(fileName));
		response.setHeader("Content-Length", String.valueOf(file.length()));
		response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");
		try{
			Files.copy(file.toPath(), response.getOutputStream());
		}
		catch(IOException e){
			RequestDispatcher disp = getServletContext().getRequestDispatcher("/404.html");
			disp.forward(request, response);
		}
		catch(Exception e){
			e.printStackTrace(System.err);
			RequestDispatcher disp = getServletContext().getRequestDispatcher("/404.html");
			disp.forward(request, response);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
