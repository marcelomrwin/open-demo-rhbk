package com.redhat.developers.open.demo;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("/")
@ApplicationScoped
public class DashboardResource {

    @Inject
    SecurityIdentity securityIdentity;

    @Inject
    Template dashboard;

    @Inject
    Template loginForm;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance getDashboard() {
        String username = securityIdentity.getPrincipal()==null?null:securityIdentity.getPrincipal().getName();
        return dashboard.data("user", username);
    }

    @GET
    @Path("/login")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance getLoginForm() {
        return loginForm.instance();
    }

    @POST
    @Path("/login/form")
    @Produces(MediaType.TEXT_HTML)
    public Response performLogin(@FormParam("username") String username,
                                 @FormParam("password") String password,
                                 @Context UriInfo uriInfo) {
        if (validateLogin(username, password)) {
            QuarkusSecurityIdentity.Builder builder = QuarkusSecurityIdentity.builder();
            builder.addRole("user");
            builder.setPrincipal(() -> username);
            securityIdentity = builder.build();

            return Response.seeOther(uriInfo.getBaseUriBuilder().path(DashboardResource.class)
                    .path("/").build()).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity(loginForm.data("error", "Invalid username or password")).build();
        }
    }

    @POST
    @Path("/logout")
    @Produces(MediaType.TEXT_HTML)
    public Response performLogout(@Context UriInfo uriInfo) {
        QuarkusSecurityIdentity.Builder builder = QuarkusSecurityIdentity.builder();
        builder.setAnonymous(true);
        securityIdentity = builder.build();
        dashboard.data("user",null);

        return Response.seeOther(uriInfo.getBaseUriBuilder().path(DashboardResource.class)
                .path("/").build()).build();
    }

    private boolean validateLogin(String username, String password) {
        //queries the database and does lots of fun things
        return true;
    }
}
