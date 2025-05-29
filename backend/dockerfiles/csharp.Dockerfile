FROM esolang/csharp:2.4.0
WORKDIR /usr/src/myapp
ARG FILE_DIRECTORY_ARG
ARG FILENAME_ARG
COPY ${FILE_DIRECTORY_ARG} /usr/src/myapp
ENV FILENAME_ENV=${FILENAME_ARG}
CMD ["sh", "-c", "sleep 1 && csharp ${FILENAME_ENV}.cs"]





#FROM mcr.microsoft.com/dotnet/sdk:6.0 AS build
#WORKDIR /app
#RUN dotnet new console
#COPY MyFile.cs Program.cs
#RUN dotnet publish -c Release -o out
#
#FROM mcr.microsoft.com/dotnet/runtime:6.0
#WORKDIR /app
#COPY --from=build /app/out .
#ENTRYPOINT ["dotnet", "app.dll"]