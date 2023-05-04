# How to release Mocca

## Introduction

This document explains how to release a new Mocca version.

## Pre-requirements

1. All instructions documented here are MacOS and bash specific. Adjust them accordingly if you use a different OS and/or shell.
1. You must have:
   1. Admin rights to Mocca GitHub repo.
   1. A [Nexus Repository Manager](https://oss.sonatype.org/#welcome) account with access to PayPal artifacts.

# Release steps

1. Working from master branch
   1. Run javadoc task to all non-test modules individually and make sure they all work
   1. Set the new version in `build.gradle`
   1. Run `./gradlew clean build` and make sure it succeeds
   1. Make sure version is correct in end user document
      1. If necessary, do a Search & Replace (there are many occurrences)
   1. Add new version in `docs/RELEASE_NOTES.md` file
   1. Commit `Setting version to x`
   1. Push your changes (`git push upstream master`)
1. Go to Mocca repo in GitHub
   1. Create a new release and tag from master branch
      1. New release title and tag name should be the new version
      1. Add sections `New Features and enhancements` and `Bug fixes` from release notes (in GitHub Markdown format) to Release description
      1. Create the new release and make sure the GitHub action `Release Publishing` is automatically triggered and succeeds
1. Manual sonatype release
   1. Go to [Nexus Repository Manager](https://oss.sonatype.org/#welcome)
   1. Go to `Staging Repositories`
   1. Make it sure it has all modules and all of them have jars, javadoc and sources, all signed
   1. Close the Mocca staging repository
   1. Release the Mocca staging repository
   1. Wait a couple of hours and make sure new artifacts version show at http://search.maven.org/#search|ga|1|g:com.paypal.mocca
1. Working from master branch
   1. Set the new SNAPSHOT version in `build.gradle`
   1. Run `./gradlew clean build` and make sure it succeeds
   1. Commit `Preparing for version <next new version>`
   1. Push your changes (`git push upstream master`)