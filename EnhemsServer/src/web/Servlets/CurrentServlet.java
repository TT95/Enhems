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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.SQLDao;
import dao.models.User;

/**
 *
 * @author Stjepan
 */
@WebServlet(name = "CurrentServlet", urlPatterns = {"/Current"})
public class CurrentServlet extends HttpServlet {

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
        String roomName = request.getParameter("room");
        if(roomName == null) {
        	response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        String[] currentValues;
        try {
            currentValues = GetCurrentValues(roomName);
        } catch (SQLException ex) {
            Logger.getLogger(CurrentServlet.class.getName()).log(Level.SEVERE, null, ex);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        String result = roomName;
        for (String currentValue : currentValues) {
            result += "&" + currentValue;
        }
        result = URLEncoder.encode(result, "UTF-8");
        response.setContentType("text/plain");
        response.setContentLength(result.length());
        try (InputStream in = new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8)); OutputStream out = response.getOutputStream()) {
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

    // </editor-fold>
    /**
     * @param roomID roomID of the room to get current data values
     * @return String array of current data values for the given room
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    private String[] GetCurrentValues(String roomName) throws SQLException {
        return SQLDao.attributeValues(roomName);
    }
}
