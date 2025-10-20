# Creators Guide - GitHub Upload Script with 2FA Support
# This script helps upload the Creators Guide app to GitHub with proper 2FA authentication

Write-Host "=== Creators Guide - GitHub Upload Script ===" -ForegroundColor Green
Write-Host ""

# Check if we're in the right directory
if (-not (Test-Path ".git")) {
    Write-Host "Error: This directory is not a Git repository." -ForegroundColor Red
    Write-Host "Please run this script from the Creators_Pain_Points directory." -ForegroundColor Yellow
    exit 1
}

# Get GitHub username
$githubUsername = Read-Host "Enter your GitHub username"

# Get repository name (default to Creators-Guide)
$repoName = Read-Host "Enter repository name (default: Creators-Guide)"
if (-not $repoName) {
    $repoName = "Creators-Guide"
}

Write-Host ""
Write-Host "=== Repository Information ===" -ForegroundColor Cyan
Write-Host "Username: $githubUsername"
Write-Host "Repository: $repoName"
Write-Host ""

# Check if repository already exists
$repoUrl = "https://api.github.com/repos/$githubUsername/$repoName"
$headers = @{
    "Authorization" = "token $env:GITHUB_TOKEN"
    "Accept" = "application/vnd.github.v3+json"
}

try {
    $response = Invoke-RestMethod -Uri $repoUrl -Headers $headers -ErrorAction Stop
    Write-Host "Repository already exists on GitHub." -ForegroundColor Yellow
} catch {
    if ($_.Exception.Response.StatusCode -eq 404) {
        Write-Host "Repository does not exist yet. We'll create it." -ForegroundColor Yellow
    } else {
        Write-Host "Error checking repository: $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "=== GitHub Authentication ===" -ForegroundColor Cyan
Write-Host "For 2FA-enabled accounts, you need to use a Personal Access Token instead of your password." -ForegroundColor Yellow
Write-Host ""

# Check if GITHUB_TOKEN environment variable is set
if ($env:GITHUB_TOKEN) {
    $useExistingToken = Read-Host "GITHUB_TOKEN environment variable found. Use it? (y/n)"
    if ($useExistingToken -eq 'y' -or $useExistingToken -eq 'Y') {
        $githubToken = $env:GITHUB_TOKEN
    } else {
        $githubToken = Read-Host "Enter your GitHub Personal Access Token (with repo permissions)"
    }
} else {
    Write-Host "To create a Personal Access Token:" -ForegroundColor Yellow
    Write-Host "1. Go to GitHub Settings > Developer settings > Personal access tokens > Tokens (classic)" -ForegroundColor Yellow
    Write-Host "2. Click 'Generate new token' > 'Generate new token (classic)'" -ForegroundColor Yellow
    Write-Host "3. Give it a name like 'Creators-Guide-Upload'" -ForegroundColor Yellow
    Write-Host "4. Select 'repo' scope (full control of private repositories)" -ForegroundColor Yellow
    Write-Host "5. Click 'Generate token'" -ForegroundColor Yellow
    Write-Host "6. Copy the token and paste it below (it will be hidden)" -ForegroundColor Yellow
    Write-Host ""
    $githubToken = Read-Host "Enter your GitHub Personal Access Token (with repo permissions)"
}

# Validate token format (should start with ghp_ or be 40 hex chars)
if (-not ($githubToken -match "^ghp_[a-zA-Z0-9]{36}$" -or $githubToken -match "^[a-fA-F0-9]{40}$")) {
    Write-Host "Warning: Token format doesn't look like a standard GitHub token, but proceeding anyway." -ForegroundColor Yellow
}

# Set the token as environment variable for this session
$env:GITHUB_TOKEN = $githubToken

Write-Host ""
Write-Host "=== Creating Repository on GitHub ===" -ForegroundColor Cyan

# Create repository using GitHub API
$repoParams = @{
    name = $repoName
    description = "An Android app that helps creators identify and solve common pain points when selling products, memberships, or access through platforms like Whop, Discord, and Shopify."
    private = $false
    auto_init = $false
}

try {
    $createResponse = Invoke-RestMethod -Uri "https://api.github.com/user/repos" -Method Post -Headers @{
        "Authorization" = "token $githubToken"
        "Accept" = "application/vnd.github.v3+json"
    } -Body ($repoParams | ConvertTo-Json)
    
    Write-Host "Repository created successfully!" -ForegroundColor Green
    $remoteUrl = $createResponse.ssh_url
} catch {
    if ($_.Exception.Response.StatusCode -eq 422) {
        Write-Host "Repository may already exist. Getting existing repository info..." -ForegroundColor Yellow
        try {
            $getResponse = Invoke-RestMethod -Uri "https://api.github.com/repos/$githubUsername/$repoName" -Method Get -Headers @{
                "Authorization" = "token $githubToken"
                "Accept" = "application/vnd.github.v3+json"
            }
            $remoteUrl = $getResponse.ssh_url
            Write-Host "Using existing repository." -ForegroundColor Green
        } catch {
            Write-Host "Error getting repository info: $($_.Exception.Message)" -ForegroundColor Red
            exit 1
        }
    } else {
        Write-Host "Error creating repository: $($_.Exception.Message)" -ForegroundColor Red
        exit 1
    }
}

Write-Host ""
Write-Host "=== Setting up Git Remote ===" -ForegroundColor Cyan

# Add remote origin
try {
    git remote remove origin 2>$null
} catch {
    # Ignore if remote doesn't exist
}

# Add the GitHub remote
git remote add origin $remoteUrl

if ($LASTEXITCODE -eq 0) {
    Write-Host "Remote 'origin' added successfully!" -ForegroundColor Green
} else {
    Write-Host "Error adding remote. Trying with HTTPS URL..." -ForegroundColor Yellow
    $httpsUrl = "https://$githubUsername:$githubToken@github.com/$githubUsername/$repoName.git"
    git remote add origin $httpsUrl
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Failed to add remote." -ForegroundColor Red
        exit 1
    }
}

Write-Host ""
Write-Host "=== Pushing to GitHub ===" -ForegroundColor Cyan

# Push to GitHub
Write-Host "Pushing code to GitHub (this may take a moment)..." -ForegroundColor Yellow

# Set credentials for HTTPS (in case SSH fails)
git config --local credential.helper store
"$([System.Environment]::NewLine)https://$githubUsername:$githubToken@github.com" > "$env:USERPROFILE\.git-credentials"

# Try to push
git push -u origin master

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "=== SUCCESS! ===" -ForegroundColor Green
    Write-Host "Repository successfully uploaded to GitHub!" -ForegroundColor Green
    Write-Host "URL: https://github.com/$githubUsername/$repoName" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Next steps:" -ForegroundColor Yellow
    Write-Host "1. Visit the URL above to view your repository" -ForegroundColor Yellow
    Write-Host "2. You can now share this repository with others" -ForegroundColor Yellow
    Write-Host "3. Consider adding more documentation or release tags" -ForegroundColor Yellow
} else {
    Write-Host ""
    Write-Host "=== ERROR ===" -ForegroundColor Red
    Write-Host "Failed to push to GitHub. Common issues:" -ForegroundColor Yellow
    Write-Host "1. Check your internet connection" -ForegroundColor Yellow
    Write-Host "2. Verify your Personal Access Token has 'repo' permissions" -ForegroundColor Yellow
    Write-Host "3. Ensure you have proper access to create repositories" -ForegroundColor Yellow
    Write-Host "4. Check if repository name is already taken" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "=== Security Note ===" -ForegroundColor Cyan
Write-Host "For security, consider removing the stored credentials:" -ForegroundColor Yellow
Write-Host "Run: git config --local --unset credential.helper" -ForegroundColor Yellow
Write-Host "And delete: $env:USERPROFILE\.git-credentials" -ForegroundColor Yellow