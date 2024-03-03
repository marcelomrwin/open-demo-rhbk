package com.redhat.developers.open.demo;

import java.util.Set;

import io.quarkus.oidc.UserInfo;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.annotations.cache.NoCache;
//import jakarta.ws.rs.core.UriInfo;
//import org.eclipse.microprofile.jwt.JsonWebToken;

@Path("/")
@ApplicationScoped
public class DashboardResource {

    @Inject
    SecurityIdentity securityIdentity;

    @Inject
    JsonWebToken jwt;
    @Inject
    UserInfo userInfo;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @NoCache
    public String unprotectedMethod() {
        return "Hello There! I'm a not protected method";
    }

    @GET
    @Path("/protected")
    @Produces(MediaType.APPLICATION_JSON)
    @NoCache
    @Authenticated
    public User protectedMethod() {
        return new User(securityIdentity);
    }

    @GET
    @Path("/chain")
    @Produces(MediaType.APPLICATION_JSON)
    @NoCache
    @RolesAllowed({"user"})
    public Response chain() {
        return Response.ok().build();
    }

    public static class User {

        private final String userName;
        private final Set<String> roles;

        User(SecurityIdentity identity) {
            this.userName = identity.getPrincipal().getName();
            this.roles = identity.getRoles();
        }

        public String getUserName() {
            return userName;
        }

        public Set<String> getRoles() {
            return roles;
        }
    }
}
