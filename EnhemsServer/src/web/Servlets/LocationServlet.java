package web.Servlets;

import fingerprints.WifiLocation;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Teo on 6/2/2017.
 * This servlet is used for wifi localization of user.
 * It should receive user scanned RSS of Access points
 * around him and should return name of the room he is in.
 *
 */
@WebServlet(name = "LocationServlet", urlPatterns = {"/Location"})
public class LocationServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
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
        String rsss=(String)request.getParameter("rsss");
        JSONObject json;
        Map<String,Integer> rsssUser = new HashMap<>();
        try {
            json = new JSONObject(rsss);
            Iterator<?> keys = json.keys();
            while( keys.hasNext() ) {
                String key = (String)keys.next();
                Integer rss = (Integer) json.get(key);
                rsssUser.put(key, rss);
            }
        } catch (JSONException ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        String room = WifiLocation.locate(rsssUser);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/plain");
        response.setContentLength(room.length());
        try (InputStream in = new ByteArrayInputStream(room.getBytes(StandardCharsets.UTF_8)); OutputStream out = response.getOutputStream()) {
            byte[] buf = new byte[1024];
            int count;
            while ((count = in.read(buf)) >= 0) {
                out.write(buf, 0, count);
            }
        }

    }

}
