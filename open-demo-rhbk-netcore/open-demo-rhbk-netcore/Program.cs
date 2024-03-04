using System.Security.Claims;
using Keycloak.AuthServices.Authentication;
using Keycloak.AuthServices.Authorization;
using Keycloak.AuthServices.Common;
using Microsoft.AspNetCore.Authentication.Cookies;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Authentication.OpenIdConnect;
using Microsoft.AspNetCore.Authorization;
using Microsoft.IdentityModel.Protocols.OpenIdConnect;
using Microsoft.IdentityModel.Tokens;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();
builder.Services.AddControllers();

var configuration = builder.Configuration;
var services = builder.Services;

// services.AddKeycloakAuthentication(configuration);

services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme).AddJwtBearer(options =>
{
    options.TokenValidationParameters = new TokenValidationParameters
    {
        ValidateIssuer = true,
        ValidIssuer = "https://rhbk-redhat-demo.apps.ocp4.masales.cloud/realms/redhat-demo"
    };
});

services.AddAuthorization(options =>
    {
    options.AddPolicy("ProtectedPolicy", bd =>
    {
        bd.RequireAuthenticatedUser();
    });
})
    .AddKeycloakAuthorization(configuration);

var app = builder.Build();

app.UseAuthentication().UseCors(bd => bd.AllowAnyOrigin().AllowAnyMethod().AllowAnyHeader());
app.UseAuthorization().UseCors(bd => bd.AllowAnyOrigin().AllowAnyMethod().AllowAnyHeader());

app.MapGet("/", () => "Hello There! I'm a not protected method");
app.MapGet("/protected", () => "Hello There! I'm a protected method from .Net Core Application").RequireAuthorization("ProtectedPolicy");

app.Run();

