param(
    [string]$target = "",
    [string]$profile = "uvl"
)

$SERVER_DIR = "server"
$CLIENT_DIR = "client"

function Invoke-BuildServer {
    Write-Host "Building server..."
    Push-Location $SERVER_DIR
    mvn --batch-mode clean verify
    Pop-Location
}

function Invoke-BuildClient {
    Write-Host "Building client..."
    Push-Location $CLIENT_DIR
    yarn
    Pop-Location
}

function Invoke-BuildVscode {
    Write-Host "Building VS Code extension for profile '$profile'..."
    Push-Location $CLIENT_DIR
    yarn "package:$profile"
    Pop-Location
}

function Invoke-CleanServer {
    Write-Host "Cleaning server..."
    Push-Location $SERVER_DIR
    mvn clean
    Pop-Location
}

function Invoke-CleanClient {
    Write-Host "Cleaning client..."
    Push-Location $CLIENT_DIR
    yarn clean
    Pop-Location
}

switch ($target) {
    "server" {
        Invoke-BuildServer
    }
    "client" {
        Invoke-BuildClient
    }
    "vscode" {
        Invoke-BuildServer
        Invoke-BuildClient
        Invoke-BuildVscode
    }
    "clean" {
        Invoke-CleanServer
        Invoke-CleanClient
    }
    "" {
        Invoke-BuildServer
        Invoke-BuildClient
    }
    default {
        Write-Host "Usage: .\build.ps1 [server|client|vscode|clean] [profile]"
        exit 1
    }
}