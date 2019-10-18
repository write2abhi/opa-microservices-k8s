package com.ibm.webapi.apis;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.json.Json;

import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.UnsupportedEncodingException;


@RequestScoped
@Path("/v1")
public class Manage {

	@Inject
  private JsonWebToken jwtPrincipal;

	@POST
	@Path("/manage")
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponses(value = {
			@APIResponse(responseCode = "200", description = "Manage application", 
				content = @Content(mediaType = "application/json", schema = @Schema(implementation = ManageResponse.class))),			
			@APIResponse(responseCode = "401", description = "Not authorized") })
	@Operation(summary = "Manage application", description = "Manage application")
	public Response manage() {

		try {
			System.out.println("com.ibm.web-api.apis.Manage.manage");
			System.out.println(this.jwtPrincipal);

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost postRequest = new HttpPost("http://opa.opa:8181/v1/data/httpapi/authz/allow");
			String rawToken = this.jwtPrincipal.getRawToken();

			StringEntity input = new StringEntity("{ \"input\": { \"method\": \"POST\",\"path\": [\"web-api\", \"v1\", \"manage\"],\"token\": \"" + rawToken + "\"}}");
			input.setContentType("application/json");
			postRequest.setEntity(input);
			HttpResponse httpResponse = httpClient.execute(postRequest);
			String response = EntityUtils.toString(httpResponse.getEntity());

			System.out.println(response);
			Gson gson = new Gson();
			OPAResponse opaResponse = gson.fromJson(response,OPAResponse.class);

			System.out.println(opaResponse.result);
			//	String principalEmail = this.jwtPrincipal.getClaim("email");
			//String principalEmail = this.jwtPrincipal.getClaim("role");
			if (opaResponse.result) {
				JsonObject output = Json.createObjectBuilder().add("message", "success").build();
				return Response.ok(output).build();
			} else {
				JsonObject output = Json.createObjectBuilder().add("message", "failure").build();
				return Response.status(Status.FORBIDDEN).entity(output).type(MediaType.APPLICATION_JSON).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.FORBIDDEN).entity(e.getMessage()).type(MediaType.APPLICATION_JSON).build();
		}

	}
}
