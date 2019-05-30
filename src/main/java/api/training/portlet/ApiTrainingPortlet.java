package api.training.portlet;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.util.ParamUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;

import api.training.constants.ApiTrainingPortletKeys;
import api.training.models.Something;

/**
 * @author brian
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=training",
		"com.liferay.portlet.instanceable=true",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + ApiTrainingPortletKeys.ApiTraining,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = Portlet.class
)
public class ApiTrainingPortlet extends MVCPortlet {
	
	private static Log _log;
	Gson gson = new Gson();
	
	public ApiTrainingPortlet() {
		_log = LogFactoryUtil.getLog(this.getClass().getName());
		
	}
	
	@Override
	public void init() throws PortletException {
		_log.info("[API Training Portlet]: Starting...");
		
		super.init();
	}
	
	@Override
    public void render(RenderRequest request, RenderResponse response)
            throws PortletException, IOException {
		final PortletSession psession = request.getPortletSession();
        _log.info("[API Training Portlet] Render!");
        
        String restRes = getRest("https://something-api.abiggeek.com/api/something");
        _log.info("The content is: " + restRes);
        
        try {
        	Something[] mcArray = gson.fromJson(restRes, Something[].class);
        	List<Something> somethings = Arrays.asList(mcArray);
            for(Something something: somethings) {
            	_log.info("a something: " + something.getName());
            }
            request.setAttribute("api_somethings", somethings);
        }
        catch(JsonSyntaxException ex) {
            _log.error(ex);
        }
        
        
        super.render(request, response);
    }
	
	public void createSomething(ActionRequest request, ActionResponse response)
	        throws PortalException, SystemException {
		
		String name = ParamUtil.getString(request, "api_name");
		String age = ParamUtil.getString(request, "api_age");
		_log.info("API Action! the name is: " + name + " and the age is: " + age);
		String json = "{\"name\":\"" + name + "\",\"age\":\"" + age + "\"}";
		_log.info("About to send: " + json);
		
		try {
			postRest("https://something-api.abiggeek.com/api/something", json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	private String getRest(String urlString) throws IOException {
		URL url = new URL(urlString);
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        
        int status = con.getResponseCode();
        
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
        	content.append(inputLine);
        }
        in.close();
        return content.toString();
	}
	
	private void postRest(String urlString, String json) throws IOException {
		URL url = new URL(urlString);
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        con.setRequestProperty("Accept", "application/json");
        con.setDoInput(true);
        con.setDoOutput(true);
        
        OutputStream os = con.getOutputStream();
        os.write(json.getBytes("UTF-8"));
        os.close();
        
        try(BufferedReader br = new BufferedReader(
        		new InputStreamReader(con.getInputStream(), "UTF-8"))) {
        	StringBuilder response = new StringBuilder();
        	String responseLine = null;
    	    while ((responseLine = br.readLine()) != null) {
    	        response.append(responseLine.trim());
    	    }
        }
        
        con.disconnect();
	}
	
}