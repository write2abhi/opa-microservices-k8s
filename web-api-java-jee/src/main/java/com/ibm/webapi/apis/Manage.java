package com.ibm.webapi.apis;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.json.Json;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.jwt.JsonWebToken;

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
		System.out.println("com.ibm.web-api.apis.Manage.manage");
		System.out.println(this.jwtPrincipal);

	//	String principalEmail = this.jwtPrincipal.getClaim("email");
		String principalEmail = this.jwtPrincipal.getClaim("role");
		if (principalEmail.equalsIgnoreCase("admin")) {
			JsonObject output = Json.createObjectBuilder().add("message", "success").build();
			return Response.ok(output).build();
		}
		else {			
			JsonObject output = Json.createObjectBuilder().add("message", "failure").build();
    	return Response.status(Status.FORBIDDEN).entity(output).type(MediaType.APPLICATION_JSON).build();
		}
	}
}
