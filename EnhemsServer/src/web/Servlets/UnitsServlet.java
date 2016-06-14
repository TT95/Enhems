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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.SQLDao;
import dao.models.Unit;
import dao.models.User;

/**
 *
 * @author Stjepan
 */
@WebServlet(name = "UnitsServlet", urlPatterns = {"/Units"})
public class UnitsServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request request
     * @param response response
     * @throws ServletException
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        Set<Unit> rooms = user.getRooms();
        String responseString = "";
        for (Unit room : rooms) {
            responseString += room.getName() + "&";
        }
        responseString = responseString.substring(0, responseString.length()-1);
        responseString = URLEncoder.encode(responseString, "UTF-8");
        response.setContentType("text/plain");
        response.setContentLength(responseString.length());
        try (InputStream in = new ByteArrayInputStream(responseString.getBytes(StandardCharsets.UTF_8)); OutputStream out = response.getOutputStream()) {
            byte[] buf = new byte[1024];
            int count;
            while ((count = in.read(buf)) >= 0) {
                out.write(buf, 0, count);
            }
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request request
     * @param response response
     * @throws ServletException
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }
}
