/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web.Servlets;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import dao.SQLDao;
import dao.models.User;
import graphjob.Graph;
import web.Model.Comparator;
import web.Model.ComparatorFactory;

/**
 *
 * @author Stjepan
 */
@WebServlet(name = "ControlServlet", urlPatterns = {"/Control"})
public class ControlServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @SuppressWarnings("unchecked")
	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user=(User)request.getSession().getAttribute("user");
        try {
            int id = user.getUserID();
            String filename = "C:\\pictures\\R" + id + "L24.png";
            File file = new File(filename);
            if (!file.exists()) {
                String jsonData = GetData(id);
                Map<Integer, Double> data = new LinkedHashMap<>();
                Gson gson = new Gson();
                data = gson.fromJson(jsonData, data.getClass());
                Map<Double, String> xLabels = new HashMap<>();
                double i = 0;
                for (Map.Entry<Integer, Double> point : data.entrySet()) {
                    xLabels.put(i++, point.getKey() + "h");
                }
                data.put(23, 0.0);
                Graph graph = new Graph(data, "Regulacija", xLabels);
                graph.Save(filename);
                graph.close();
            }
            String contextPath = getServletContext().getContextPath();
            String token=request.getParameter("token");
            response.sendRedirect(contextPath + "/Graph?measure=R&timeperiod=L24&token="+token);
        } catch (SQLException ex) {
            Logger.getLogger(ControlServlet.class.getName()).log(Level.SEVERE, null, ex);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user=(User)request.getSession().getAttribute("user");
        String dataParam = request.getParameter("data");
        int[] values = GetDataValues(dataParam);
        if (values == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        try {
            int id = user.getUserID();
            SetData(values, id);
            String filename = "C:\\pictures\\R" + id + "L24.png";
            File file = new File(filename);
            Files.deleteIfExists(file.toPath());
            doGet(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(ControlServlet.class.getName()).log(Level.SEVERE, null, ex);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
// </editor-fold>

    /**
     * @param userId user ID of the user for whom to get occupancy data
     * @return occupancy profile data for given user as JSON array
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    private String GetData(int userId) throws SQLException {
        return SQLDao.getOccupancyForUser(userId);
    }

    /**
     * @param data data request parameter
     * @return integer array containing min value of the interval[0], max value
     * of the interval[1] and regulation value[2]
     */
    private int[] GetDataValues(String data) {
        if (data == null) {
            return null;
        }
        String[] changeData = data.split(",");
        if (changeData.length != 3) {
            return null;
        }
        int min, max, value;
        try {
            min = Integer.valueOf(changeData[0]);
            max = Integer.valueOf(changeData[1]);
            value = Integer.valueOf(changeData[2]);
        } catch (NumberFormatException ex) {
            Logger.getLogger(ControlServlet.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return new int[]{min, max, value};
    }

    /**
     * Set new data for the occupancy profile, for the given user according to
     * the values array
     *
     * @param values integer array containing min value of the interval[0], max
     * value of the interval[1] and regulation value[2]
     * @param user user for whom to set the data
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
	private void SetData(int[] values, int userID) throws SQLException {
        int min, max, value;
        min = values[0];
        max = values[1];
        value = values[2];
        String jsonData = GetData(userID);
        Map<String, Double> data = new LinkedHashMap<>();
        Gson gson = new Gson();
        data = gson.fromJson(jsonData, data.getClass());
        Comparator comparator = ComparatorFactory.GetComparator(min, max);
        for (int i = 0; i < 24; i++) {
            if (comparator.Compare(i)) {
                data.put(String.valueOf(i), (double) value);
            }
        }
        jsonData = gson.toJson(data);
        SQLDao.setOccupancyForUser(userID, jsonData);
    }
}
