/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web.Servlets;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import dao.SQLDao;
import dao.models.User;
import web.Model.Token;
import web.Model.TokenRep;

/**
 *
 * @author Stjepan
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/Login"})
public class LoginServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     *
     *
     * //
     * <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
     * /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        Token token;
        if (user == null) {
            String userName = request.getParameter("username");
            String pass = request.getParameter("pass");
            if (userName == null || pass == null) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            try {
                user = doLogin(userName, pass);
            } catch (SQLException ex) {
                Logger.getLogger(LoginServlet.class.getName()).log(Level.SEVERE, null, ex);
                user = null;
            }
            if (user == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            String tokenString = UUID.randomUUID().toString();
            token = new Token(tokenString, user.getUserID());
            TokenRep.Add(token);
        }
        else{
            token=TokenRep.Find(request.getParameter("token"));
        }
        response.setContentType("text/plain");
        response.setContentLength(token.getToken().length());
        try (InputStream in = new ByteArrayInputStream(token.getToken().getBytes(StandardCharsets.UTF_8)); OutputStream out = response.getOutputStream()) {

            byte[] buf = new byte[1024];
            int count;
            while ((count = in.read(buf)) >= 0) {
                out.write(buf, 0, count);
            }
        }
    }

    // </editor-fold>
    private User doLogin(String username, String password) throws IOException, SQLException {
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost postRequest = new HttpPost("https://www.fer.unizg.hr/login.php");

        List<NameValuePair> params = new ArrayList<>(2);
        params.add(new BasicNameValuePair("loginname", username));
        params.add(new BasicNameValuePair("loginpassw", password));
        postRequest.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

        HttpResponse postResponse = httpclient.execute(postRequest);
        String location = postResponse.getHeaders("Location")[0].getValue();
        if (location.contains("loginfailed")) {// FERweb login failed           
            return null;
        }

        return SQLDao.getUser(username);
    }
}
