package rpc;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;
import external.TicketMasterAPI;

import java.util.*;
/**
 * Servlet implementation class SearchItem
 */
@WebServlet("/search")
public class SearchItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchItem() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		 allow access only if session exists
		HttpSession session = request.getSession(false); 
		if (session == null) {
			response.setStatus(403);
			return; 
		}
		String userId = session.getAttribute("user_id").toString();
		
		
		double lat = Double.parseDouble(request.getParameter("lat")); 
		double lon = Double.parseDouble(request.getParameter("lon")); // Term can be empty or null.
		String term = request.getParameter("term");
		
		DBConnection connection = DBConnectionFactory.getDBConnection(); 
		List<Item> items = connection.searchItems(lat, lon, term);
		Set<String> favorite = connection.getFavoriteItemIds(userId);
		
//		TicketMasterAPI tmAPI = new TicketMasterAPI(); 
//		List<Item> items = TicketMasterAPI.search(lat, lon, term); 
		
		List<JSONObject> list = new ArrayList<>();
		try {
			for (Item item : items) {
				// Add a thin version of item object 
				JSONObject obj = item.toJSONObject(); 
				list.add(obj);
				// Check if this is a favorite one.
				// This field is required by frontend to correctly display favorite items. 
				obj.put("favorite", favorite.contains(item.getItemId()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		JSONArray array = new JSONArray(list); 
		RpcHelper.writeJsonArray(response, array);
	}

}
