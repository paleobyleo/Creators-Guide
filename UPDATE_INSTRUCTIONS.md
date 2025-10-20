# Update Management Setup Instructions

This document explains how to set up automatic update checking for the Creators Guide app.

## Overview

The app includes a built-in update checking mechanism that can notify users when new versions are available on GitHub. To enable this feature, you need to:

1. Create a GitHub repository for the app
2. Configure the GitHub repository information in the app
3. Upload releases to GitHub with APK files

## Steps to Enable Update Checking

### 1. Create a GitHub Repository

1. Go to GitHub and create a new repository named `Creators-Guide`
2. Do NOT initialize the repository with a README, .gitignore, or license
3. Note down your GitHub username/organization name

### 2. Configure GitHub Repository Information

1. Open `app/src/main/java/com/leo/creators_guide/utils/GitHubConfig.kt`
2. Update the following values:
   ```kotlin
   // GitHub username/organization
   const val OWNER = "YOUR_ACTUAL_GITHUB_USERNAME"
   
   // Repository name
   const val REPO = "Creators-Guide"
   
   // Set this to true to enable update checks
   const val ENABLE_UPDATE_CHECKS = true
   ```

### 3. Upload Releases to GitHub

When you create a new release:

1. Go to your GitHub repository
2. Click on "Releases" > "Draft a new release"
3. Create a new tag (e.g., v1.1, v1.2, etc.)
4. Add release notes describing the changes
5. Attach the APK file to the release
6. Publish the release

The app will automatically detect new releases and notify users.

## How It Works

The update checking mechanism:

1. Checks the current app version using the package manager
2. Queries the GitHub API for the latest release of your repository
3. Compares version numbers
4. If a newer version is available, shows a notification to the user
5. Provides a direct link to download the APK

## Testing Update Notifications

To test the update notification feature:

1. Set `ENABLE_UPDATE_CHECKS = true` in GitHubConfig.kt
2. Make sure your GitHub repository has at least one release
3. Run the app and verify that the update notification appears (if an update is available)
4. If no update is available, you'll see a message confirming you're on the latest version

## Disabling Update Checks

To disable update checks, simply set:
```kotlin
const val ENABLE_UPDATE_CHECKS = false
```

This is the default setting to prevent errors during development.

## Troubleshooting

If update checking isn't working:

1. Verify that `ENABLE_UPDATE_CHECKS` is set to `true`
2. Check that `OWNER` and `REPO` are set to the correct values
3. Ensure your GitHub repository is public (private repositories require authentication)
4. Verify that the latest release has an APK file attached
5. Check the app logs for any error messages related to network requests