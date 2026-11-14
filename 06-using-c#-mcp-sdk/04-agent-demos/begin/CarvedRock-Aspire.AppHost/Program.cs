var builder = DistributedApplication.CreateBuilder(args);

var carvedrockdb = builder.AddPostgres("postgres")
                          .AddDatabase("CarvedRockPostgres");

// added from https://github.com/DuendeSoftware/demo.duendesoftware.com
// add project reference to Duende IdentityServer Demo
// add service defaults reference to Duende IdentityServer Demo
// update package refs in IdSrv to use central package management and latest versions
// update Program.cs in IdSrv to add service defaults and hostingextensions to include mapdefaultendpoints
// update logic in program.cs to not use serilog and use service defaults logging (so everything is visible
// in aspire dashboard)
var idsrv = builder.AddProject<Projects.Duende_IdentityServer_Demo>("idsrv");

var api = builder.AddProject<Projects.CarvedRock_Api>("api")
    .WithReference(carvedrockdb)
    .WaitFor(carvedrockdb)
    .WithEnvironment("Auth__Authority", idsrv.GetEndpoint("https")) // <-- this overrides appsettings!
    .WithHttpHealthCheck("/health");

var mailpit = builder.AddMailPit("smtp");

builder.AddProject<Projects.CarvedRock_WebApp>("webapp")
    .WithReference(api)
    .WithReference(mailpit)
    .WaitFor(mailpit)
    .WaitFor(api)
    .WithHttpHealthCheck("/health");

var mcp = builder.AddProject<Projects.CarvedRock_Mcp>("mcp")
    .WithReference(api)
    .WithEnvironment("AuthServer", idsrv.GetEndpoint("https")) // <-- this overrides appsettings!
    .WithHttpHealthCheck("/health");

builder.AddMcpInspector("mcp-inspector")
    //.WithEnvironment("OAUTH_CLIENT_ID", "something-that-wont-work")
    .WithMcpServer(mcp, path: "");

builder.Build().Run();
