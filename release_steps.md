# Release steps

1. Working from a feature branch (out of develop) in your fork:
   1. Run javadoc task to all non-test modules individually and make sure they all work
   1. Rev up root build.gradle file to the release version
   1. Build it and make sure it works (build includes all tests)
   1. Make sure version is correct in end user document
      1. If necessary, do a Search & Replace (there are many occurrences)
   1. Update release notes
   1. Commit `Setting version to x`
   1. Push from local feature branch to origin feature branch (`git push origin <branch name>`)
1. Send and merge PR from origin feature branch to upstream develop
1. Send and merge PR from upstream develop to upstream master (PR must be merged with `Rebase Merge`)
1. Delete develop branch, since it will be out of sync with master (commits in master will have different hash number)
1. Create a new Release and tag
   1. Tag name should be the version name
   1. Release title should be left blank
   1. Add sections `New Features and enhancements` and `Bug fixes` from release notes (in GitHub Markdown format) to Release description
1. Close milestone
   1. Set is due date to today
1. Deploy artifacts to Maven Central
   1. Go to TravisCI (click on its badge on the README file)
   1. Click on `More Options -> Trigger Build`
   1. Set `master` as branch
   1. Set `Releasing <version number>` in `CUSTOM COMMIT MESSAGE` field
   1. Copy and paste the content of [.travis_release.yml](.travis_release.yml) in `CUSTOM CONFIG` field
1. Manual sonatype release
   1. Go to Staging Repositories and find the Mocca repo
   1. Make it sure it has all modules and all of them have jars, javadoc and sources, all signed
   1. Close the repository
   1. Release the repository
   1. Make sure new artifacts version shows at Sonatye Nexus Repository Manager
   1. Wait a couple of hours and make sure new artifacts version show at http://search.maven.org/#search|ga|1|g:com.paypal.mocca
1. Recreate develop branch from master branch
1. Working from a feature branch (out of develop) in your fork:
   1. Rev up root build.gradle file to the next SNAPSHOT version
   1. Change version in the end user document (there are many occurrences, do a Search & Replace)
   1. Build the whole project and make sure it builds fine
   1. Add new version empty section in release notes
   1. Commit `Setting version to x`
   1. Push from feature branch to origin feature branch (`git push origin <branch name>`)
   1. Send and merge PR from origin feature branch to upstream develop
1. Create new milestone
   1. Add issues to new milestone (if any)