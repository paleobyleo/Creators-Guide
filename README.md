# Creators Guide

An Android app that helps creators identify and solve common pain points when selling products, memberships, or access through platforms like Whop, Discord, and Shopify.

## Features

- **100+ Pain Points**: Analyzed from top Whop, Discord, and Shopify apps
- **Categorized Solutions**: Grouped by 13 categories including Payments, Retention, Analytics, Community, Automation, Upsells, Refunds, Content Creation, Marketing, Technical Issues, Time Management, Customer Support, and Legal & Compliance
- **Detailed Cost Information**: Each solution includes estimated costs, implementation time, required skills, and tools/platforms
- **Interactive UI**: Filter by category or view top 20 most frequent pain points
- **Expandable Details**: Show/hide solutions and cost information for each pain point

## Categories

1. Payments
2. Retention
3. Analytics
4. Community
5. Automation
6. Upsells
7. Refunds
8. Content Creation
9. Marketing
10. Technical Issues
11. Time Management
12. Customer Support
13. Legal & Compliance

## Installation

1. Clone the repository
2. Open in Android Studio
3. Build and run the app

## Uploading to GitHub with 2FA

This repository includes scripts to help you upload the project to GitHub, even if you have Two-Factor Authentication (2FA) enabled.

### Prerequisites

1. A GitHub account with 2FA enabled (if applicable)
2. A Personal Access Token with `repo` permissions

### Creating a Personal Access Token

1. Go to GitHub Settings > Developer settings > Personal access tokens > Tokens (classic)
2. Click 'Generate new token' > 'Generate new token (classic)'
3. Give it a name like 'Creators-Guide-Upload'
4. Select 'repo' scope (full control of private repositories)
5. Click 'Generate token'
6. Copy the token (you won't see it again)

### Uploading with Scripts

#### Option 1: PowerShell Script (Recommended for Windows 10/11)

Run the PowerShell script:
```powershell
.\upload_to_github.ps1
```

#### Option 2: Batch Script (For older Windows versions)

Run the batch script:
```cmd
upload_to_github.bat
```

Both scripts will guide you through:
1. Entering your GitHub username
2. Confirming the repository name
3. Entering your Personal Access Token
4. Automatically creating the repository and pushing the code

### Manual Upload (Alternative)

If you prefer to upload manually:

1. Create a new repository on GitHub (do NOT initialize with README)
2. Add the remote origin:
   ```bash
   git remote add origin https://github.com/YOUR_USERNAME/REPO_NAME.git
   ```
3. Push the code:
   ```bash
   git push -u origin master
   ```

When prompted for credentials, use your GitHub username and Personal Access Token (not your password).

## Automatic Update Management

The app includes a built-in update checking mechanism that can notify users when new versions are available on GitHub. To enable this feature:

1. Create a GitHub repository for the app
2. Configure the GitHub repository information in `app/src/main/java/com/leo/creators_guide/utils/GitHubConfig.kt`
3. Set `ENABLE_UPDATE_CHECKS = true`
4. Upload releases to GitHub with APK files

For detailed instructions, see [UPDATE_INSTRUCTIONS.md](UPDATE_INSTRUCTIONS.md).

## Technologies Used

- Kotlin
- Jetpack Compose
- Android SDK

## License

MIT License

Copyright (c) 2025 Paleo by Leo

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.