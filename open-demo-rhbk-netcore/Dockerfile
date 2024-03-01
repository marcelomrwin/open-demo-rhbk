FROM mcr.microsoft.com/dotnet/aspnet:8.0 AS base
USER $APP_UID
#ENV ASPNETCORE_ENVIRONMENT=Development
WORKDIR /app
EXPOSE 8080
EXPOSE 8081

FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
ARG BUILD_CONFIGURATION=Release
WORKDIR /src
COPY ["open-demo-rhbk-netcore.csproj", "./"]
RUN dotnet restore "open-demo-rhbk-netcore.csproj"
COPY . .
WORKDIR "/src/"
RUN dotnet build "open-demo-rhbk-netcore.csproj" -c $BUILD_CONFIGURATION -o /app/build

FROM build AS publish
ARG BUILD_CONFIGURATION=Release
RUN dotnet publish "open-demo-rhbk-netcore.csproj" -c $BUILD_CONFIGURATION -o /app/publish /p:UseAppHost=false
RUN chown -R $APP_UID:$APP_UID /app/publish
RUN chmod -R a+rwX /app/publish/Data

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "open-demo-rhbk-netcore.dll"]
