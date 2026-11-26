$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Parent $PSScriptRoot
$envPath = Join-Path $projectRoot '.env'
if (-not (Test-Path -LiteralPath $envPath -PathType Leaf)) {
    [Console]::Error.WriteLine('Atlassian MCP requires a project-root .env file.')
    exit 1
}

$variables = @{}
foreach ($line in Get-Content -LiteralPath $envPath) {
    $trimmedLine = $line.Trim()
    if ($trimmedLine -match '^(?:export\s+)?([A-Za-z_][A-Za-z0-9_]*)\s*=\s*(.*)$') {
        $name = $matches[1]
        $value = $matches[2].Trim()
        if ($value.Length -ge 2) {
            $firstCharacter = $value[0]
            $lastCharacter = $value[$value.Length - 1]
            if ((($firstCharacter -eq '"') -and ($lastCharacter -eq '"')) -or (($firstCharacter -eq "'") -and ($lastCharacter -eq "'"))) {
                $value = $value.Substring(1, $value.Length - 2)
            }
        }
        $variables[$name] = $value
    }
}

$authorization = $variables['ATLASSIAN_AUTHORIZATION']
if ([string]::IsNullOrWhiteSpace($authorization)) {
    $apiKey = $variables['ATLASSIAN_API_KEY']
    if (-not [string]::IsNullOrWhiteSpace($apiKey)) {
        $authorization = "Bearer $apiKey"
    } else {
        $email = $variables['ATLASSIAN_EMAIL']
        $apiToken = $variables['ATLASSIAN_API_TOKEN']
        if (-not [string]::IsNullOrWhiteSpace($email) -and -not [string]::IsNullOrWhiteSpace($apiToken)) {
            $credentialBytes = [Text.Encoding]::UTF8.GetBytes("${email}:${apiToken}")
            $encodedCredentials = [Convert]::ToBase64String($credentialBytes)
            $authorization = "Basic $encodedCredentials"
        }
    }
}

if ([string]::IsNullOrWhiteSpace($authorization)) {
    [Console]::Error.WriteLine(
        'Set ATLASSIAN_AUTHORIZATION, ATLASSIAN_API_KEY, or both ATLASSIAN_EMAIL and ATLASSIAN_API_TOKEN in the project-root .env file.')
    exit 1
}

$npx = Get-Command 'npx.cmd' -ErrorAction SilentlyContinue
if ($null -eq $npx) {
    [Console]::Error.WriteLine('Atlassian MCP requires Node.js and npx.cmd on PATH.')
    exit 1
}

$temporaryDirectory = Join-Path $env:LOCALAPPDATA 'AtlassianMcp'
$null = New-Item -ItemType Directory -Path $temporaryDirectory -Force
$headerFile = Join-Path $temporaryDirectory ("headers-" + [Guid]::NewGuid().ToString('N') + '.txt')

try {
    [IO.File]::WriteAllText(
        $headerFile,
        "Authorization: $authorization",
        [Text.UTF8Encoding]::new($false))
    & $npx.Source --yes 'mcp-remote@0.1.38' 'https://mcp.atlassian.com/v2/mcp' '--header-file' $headerFile
    exit $LASTEXITCODE
} finally {
    Remove-Item -LiteralPath $headerFile -Force -ErrorAction SilentlyContinue
}
