# Audit-log hook: appends one line per agent event to docs/pipeline-log.md
# Usage: Log.ps1 <EventName>   (the event JSON arrives on stdin)
param([string]$EventName = "unknown")

# 1. Read the JSON that VS Code sends us
$inputJson = [Console]::In.ReadToEnd()

# 2. Pull out the tool name, if there is one
$tool = ""
if ($inputJson -match '"(?:tool_name|toolName)"\s*:\s*"([^"]*)"') {
    $tool = $Matches[1]
}

# 3. Make sure the log file exists
$logFile = "docs/pipeline-log.md"
if (-not (Test-Path "docs")) { New-Item -ItemType Directory -Path "docs" | Out-Null }
if (-not (Test-Path $logFile)) { "# Pipeline Audit Log`n" | Out-File -FilePath $logFile -Encoding utf8 }

# 4. Append a timestamped line
$timestamp = (Get-Date).ToUniversalTime().ToString("o")
$line = "- $timestamp | $EventName"
if ($tool) { $line += " | $tool" }
Add-Content -Path $logFile -Value $line