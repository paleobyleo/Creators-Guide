@echo off
title Creators Guide - GitHub Upload Script

echo ====================================================
echo     Creators Guide - GitHub Upload Script
echo ====================================================
echo.

REM Check if we're in the right directory
if not exist ".git" (
    echo Error: This directory is not a Git repository.
    echo Please run this script from the Creators_Pain_Points directory.
    pause
    exit /b 1
)

echo Please enter your GitHub information:
echo.

set /p githubUsername="Enter your GitHub username: "
if "%githubUsername%"=="" (
    echo Error: Username cannot be empty.
    pause
    exit /b 1
)

set /p repoName="Enter repository name (default: Creators-Guide): "
if "%repoName%"=="" set repoName=Creators-Guide

echo.
echo Repository Information:
echo Username: %githubUsername%
echo Repository: %repoName%
echo.

echo GitHub Authentication:
echo For 2FA-enabled accounts, you need to use a Personal Access Token instead of your password.
echo.

echo To create a Personal Access Token:
echo 1. Go to GitHub Settings ^> Developer settings ^> Personal access tokens ^> Tokens (classic)
echo 2. Click 'Generate new token' ^> 'Generate new token (classic)'
echo 3. Give it a name like 'Creators-Guide-Upload'
echo 4. Select 'repo' scope (full control of private repositories)
echo 5. Click 'Generate token'
echo 6. Copy the token and paste it below (it will be hidden)
echo.

set githubToken=
set /p githubToken="Enter your GitHub Personal Access Token (with repo permissions): "
if "%githubToken%"=="" (
    echo Error: Personal Access Token cannot be empty.
    pause
    exit /b 1
)

echo.
echo Setting up Git remote...
echo.

REM Remove existing origin if it exists
git remote remove origin >nul 2>&1

REM Add the GitHub remote using HTTPS with token
set remoteUrl=https://%githubUsername%:%githubToken%@github.com/%githubUsername%/%repoName%.git
git remote add origin %remoteUrl%

if %errorlevel% neq 0 (
    echo Error: Failed to add remote.
    pause
    exit /b 1
)

echo Remote 'origin' added successfully!
echo.

echo Pushing to GitHub...
echo This may take a moment...
echo.

git push -u origin master

if %errorlevel% equ 0 (
    echo.
    echo ====================================================
    echo     SUCCESS! Repository uploaded to GitHub!
    echo ====================================================
    echo Repository URL: https://github.com/%githubUsername%/%repoName%
    echo.
    echo Next steps:
    echo 1. Visit the URL above to view your repository
    echo 2. You can now share this repository with others
    echo 3. Consider adding more documentation or release tags
    echo.
    echo Security Note:
    echo The credentials were stored temporarily. 
    echo Consider running 'git config --local --unset credential.helper'
    echo to remove stored credentials.
) else (
    echo.
    echo ====================================================
    echo     ERROR: Failed to push to GitHub
    echo ====================================================
    echo Common issues:
    echo 1. Check your internet connection
    echo 2. Verify your Personal Access Token has 'repo' permissions
    echo 3. Ensure you have proper access to create repositories
    echo 4. Check if repository name is already taken
)

echo.
pause