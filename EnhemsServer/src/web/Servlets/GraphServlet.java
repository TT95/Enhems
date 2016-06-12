/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web.Servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletContext;
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
@WebServlet(name = "Graph", urlPatterns = {"/Graph"})
public class GraphServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     *
     * @param request
     * @param response
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String measure = request.getParameter("measure");
        String timePeriod = request.getParameter("timeperiod");
        String roomName = request.getParameter("room");
        String id = SQLDao.getUnit(roomName).getId() + "";
        if(roomName == null) {
        	response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
        if (!(measure.equals("P") || measure.equals("Ptot") || measure.equals("Idiff") || measure.equals("Idir"))) {//not public data
            User user=(User)request.getSession().getAttribute("user");
            if (user == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            if (measure.equals("R")) {
                id = String.valueOf(user.getUserID());
            }
        }
        String filename = measure + id + timePeriod + ".png";
        filename = "C:\\pictures\\" + filename;
        ServletContext cntx = getServletContext();
        String mime = cntx.getMimeType(filename);
        File file = new File(filename);
        if (!file.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        long size = file.length();
        response.setContentType(mime);
        response.setContentLength((int) size);
        try (FileInputStream in = new FileInputStream(file); OutputStream out = response.getOutputStream()) {
            byte[] buf = new byte[1024];
            int count;
            while ((count = in.read(buf)) >= 0) {
                out.write(buf, 0, count);
            }
        }
    }

    /**
     *
     * @param request
     * @param response
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

// </editor-fold>
}
