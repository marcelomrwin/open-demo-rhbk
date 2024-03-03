using Keycloak.AuthServices.Authentication;
using Keycloak.AuthServices.Common;

var authenticationOptions = new KeycloakAuthenticationOptions
{
    AuthServerUrl = "https://rhbk-redhat-demo.apps.ocp4.masales.cloud",
    Realm = "redhat-demo",
    Resource = "open-demo-rhbk-dotnet",
    SslRequired = "false",
    VerifyTokenAudience = false,
    Credentials = new KeycloakClientInstallationCredentials
    {
        Secret = "T9sASMkGwkPLo0VK7NcL8PYpKweoMcAx"
    }
};

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();
builder.Services.AddControllers();
builder.Services.AddKeycloakAuthentication(authenticationOptions);

var app = builder.Build();

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseAuthentication();
app.UseAuthorization();

app.MapGet("/", () => "Hello There! I'm a not protected method");
app.MapGet("/protected", () => "Hello There! I'm a protected method from .Net Core Application").RequireAuthorization();

app.Run();

