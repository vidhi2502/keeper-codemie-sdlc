# Safety-guard hook: runs BEFORE every tool call and blocks dangerous actions.
# Exit code 2 = block the action and show the message to the agent. Exit 0 = allow.

# 1. Read the JSON that VS Code sends us
$inputText = [Console]::In.ReadToEnd().ToLower()

# 2. Patterns we never allow
$blockedPatterns = @(
    'git\s+push.*--force',            # force push
    'git\s+push.*\s-f(\s|\\|")',      # force push (short flag)
    'git\s+add.*\.env',               # committing secret files
    'rm\s+-rf\s+/',                   # destructive delete
    'git\s+push\s+\S+\s+main(\s|\\|")'  # pushing straight to main
)

# 3. Check each pattern
foreach ($pattern in $blockedPatterns) {
    if ($inputText -match $pattern) {
        [Console]::Error.WriteLine("Blocked by guard hook: force-push, push to main, secret file or destructive command.")
        exit 2
    }
}
exit 0