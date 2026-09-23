# ============================================================
# export-token.ps1 (Prompt 2.1)
# Extrait un access_token depuis Keycloak via password grant.
#
# Utilisation :
#   .\export-token.ps1 -User carol -Password viewer123
#   .\export-token.ps1 -User bob   -Password manager123
#   .\export-token.ps1 -User alice -Password admin123
#
# Variables d'env optionnelles (defauts hote) :
#   $env:KEYCLOAK   = http://localhost:8081
#   $env:REALM      = stock-app
#   $env:CLIENT_ID  = stock-api
# ============================================================
[CmdletBinding()]
param(
    [Parameter(Position = 0, Mandatory = $true)][string]$User,
    [Parameter(Position = 1, Mandatory = $false)][string]$Password,
    [string]$ClientId = "$(if ($env:CLIENT_ID) { $env:CLIENT_ID } else { 'stock-api' })",
    [string]$Realm    = "$(if ($env:REALM)     { $env:REALM }     else { 'stock-app' })",
    [string]$Base     = "$(if ($env:KEYCLOAK)  { $env:KEYCLOAK }  else { 'http://localhost:8081' })"
)

$ErrorActionPreference = 'Stop'

if (-not $Password) {
    $Password = Read-Host ('Mot de passe de ' + $User) -AsSecureString
    $bstr = [System.Runtime.InteropServices.Marshal]::SecureStringToBSTR($Password)
    try { $Password = [System.Runtime.InteropServices.Marshal]::PtrToStringBSTR($bstr) }
    finally { [System.Runtime.InteropServices.Marshal]::ZeroFreeBSTR($bstr) }
}

$body = @{
    client_id = $ClientId
    username  = $User
    password  = $Password
    grant_type = 'password'
}

$tokenUrl = "$Base/realms/$Realm/protocol/openid-connect/token"

try {
    $resp = Invoke-RestMethod -Method Post -Uri $tokenUrl -Body $body -ContentType 'application/x-www-form-urlencoded'
} catch {
    Write-Error ("Echec du token grant : " + $_.Exception.Message)
    exit 1
}

if (-not $resp.access_token) {
    Write-Error "Pas de access_token dans la reponse."
    exit 1
}

Write-Host '----------------------------------------------------'
Write-Host ('Token (user=' + $User + ') :')
Write-Host $resp.access_token
Write-Host '----------------------------------------------------'
Write-Host ('expires_in=' + $resp.expires_in + 's  token_type=' + $resp.token_type)
