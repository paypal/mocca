
# Contribution guidelines

## Reporting a bug or requesting a new feature

Please, first search [Mocca issues](https://github.com/paypal/mocca/issues) and see if there is already an issue covering what you would like to report. If there is, feel free to add comments to it if you believe you have valuable information about it.

If you couldn't find an issue covering what you would like to report, please feel free to open a new issue. If you are reporting a bug, please include the following information:

1. What you expected
1. What happened instead
1. Steps to recreate what happened
1. Error logs (if you have them)

## Contributing with code changes

Follow the instructions below please:

1. First, please read the previous section and make sure there is an issue describing the bug fix or new feature you would like to provide.
1. Before starting writing code, please mention that in the issue comments section. The idea is to make sure there is no one else already working on it, or that that change is really expected for a following release.
1. Fork this repo
1. Checkout `develop` branch
1. Optionally, create your own feature branch on your fork out of `develop` branch
1. Apply your changes
    1. Make sure all modules build and all unit tests pass
    1. Make sure code coverage doesn't drop (add extra unit tests if necessary)
    1. If fixing a bug, make sure you add an unit or functional test to expose the issue
    1. If adding a new feature, make sure you add an unit or functional test to test the feature
    1. If adding a new feature, add end user documentation as well
    1. Add comments to the code explaining your changes if necessary
1. Create a pull request to the upstream `develop` branch (add the issue id in the end of PR and commit name preceded by a hashtag)

## Code style
Make sure to follow the code style of the existing code. That means, for example, four spaces for indentation.

## More information
Read more about best practices in [this github guide](https://guides.github.com/activities/contributing-to-open-source/).
