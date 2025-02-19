# GitHub workflows

We use [GitHub workflows](https://github.com/features/actions) to automate CI/CD tasks. If you're already familiar with this tool, skip to the [Usage](#Usage) section.

## What is a GitHub workflow?

GitHub workflows are a set of instructions to do on a particular event. The main event that triggers workflows is the `push` event. This means that every time commits are pushed to the repository, associated workflows run.

Usually, a single workflow fulfills a single task (e.g. building, running unit tests, deploying...). Each workflow may have multiple jobs, and each job may have multiple steps. Workflows may output artifacts, and interact with GitHub (posting comments, reviewing PRs...).

The definition of the workflows are located in the `.github/workflows` directory, and are using YAML syntax, with special `${{ expressions }}`.

## Usage

We have 6 workflows:
- [`build.yml`](#build-jar): Builds the executable JAR and uploads it as an artifact, then reports build status. Fails if the project does not compile.
- [`checkstyle.yml`](#checkstyle): Ensures that the source code complies with checkstyle rules. Always succeeds, but creates a failed check with code-style violation (if any).
- [`commits.yml`](#commits): Validate commit messages, to enforce conventional commits and some other rules. Only triggers in pull requests, and fails if it contains invalid commits.
- [`github-pages.yml`](#github-pages): Deploys generated Javadoc to GitHub Pages. Only triggers on `main` branch, and should never fail because Javadoc generation is already validated in PRs.
- [`javadoc.yml`](#javadoc): Generates a static HTML documentation from Javadoc Comments, and deploys it to GitHub Pages. Fails if Javadoc has any error.
- [`unit-tests.yml`](#unit-tests): Runs unittests. Fails if any unit test do not pass.

### Build JAR

> [!TIP]
> See the [`build.yml`](../../.github/workflows/build.yml) file.

This workflow triggers when a push on any branch modifies the main source files.

It consists of a first job, [building the JAR](Maven.md#create-an-executable-jar) and preparing the report message, following by a second job, either posting this report as a PR comment or as a commit comment (depending on whether there is or not an open PR for the corresponding branch). Only one of the "reporting" job is executed, the other being skipped. Note: Only one PR comment is created, it is later edited if new builds are made.

If the project fails to build (compilation failure, missing dependency, packaging issue...), the workflow will fail, but the status will still be reported, with the build logs to help troubleshooting the problem.

Otherwise, the workflow will upload the `Generator.jar` file as an artifact, and include the download URL in the report message. You can [download and run the JAR](../usage/Run.md#download-workflow-artifact) to test it locally.

### checkstyle

> [!TIP]
> See the [`checkstyle.yml`](../../.github/workflows/checkstyle.yml) file.

This workflow triggers when a push on any branch modifies any source files, or the checkstyle rules.

It consists of a single job, running the [checkstyle](Checkstyle.md) tool and then using [reviewdog](https://github.com/reviewdog/reviewdog) to report possible rule violation through [GitHub Checks](https://github.com/reviewdog/reviewdog?tab=readme-ov-file#reporter-github-checks--reportergithub-check).

The workflow never fails (except if the tools themselves fail, which is unlikely to happen). It instead creates a status check, with results (either success or failure) linked to their source files.

### Commits

> [!TIP]
> See the [`commits.yml`](../../.github/workflows/commits.yml) file.

This workflow triggers when a pull request from any branch is (re)opened or synchronized.

It consists of a single job, listing all commits in the PR, and running a small script validating each of them against the following rules:
- The message follows the conventional commits principles;
- The type of the commit is listed in the `ALLOWED_TYPES` variable (see at the beginning of the workflow for the full list);
- The same message is not found twice (or more);
- The message does not contain "WIP" or "Work in progress" (ignoring case).

The workflow fails if any of these rules is violated by one or more commit. Violation are reported through status annotations in the check, to better help the user fix the Git history.

### GitHub-Pages

> [!TIP]
> See the [`github-pages.yml`](../../.github/workflows/github-pages.yml) file.

This workflow triggers when a push on the `main` branch modifies the Java main source files.

It consists of a single job, deploying generated Javadoc to GitHub Pages. The reason the workflow only deploys `main` branch is the result of a technical limitation on GitHub's side: We can't deploy a "preview site" for a pull request. To maintain the Javadoc from the `main` branch available on GitHub Pages, this must be the only existing deployment.

The workflow should never fail because Javadoc generation is validated by the [`javadoc.yml`](#javadoc) workflow that triggers in pull requests.

### Javadoc

> [!TIP]
> See the [`javadoc.yml`](../../.github/workflows/javadoc.yml) file.

This workflow triggers when a push on any branch other than `main` modifies the Java main source files.

It consists of a single job, running the [Javadoc](Javadoc.md) tool to generate the static HTML documentation, and then uploading it as workflow artifact.

The workflow fails if the Javadoc tool itself fails, meaning there are errors in Javadoc Comments, and static HTML documentation cannot be generated.

### Unit-Tests

> [!TIP]
> See the [`unit-tests.yml`](../../.github/workflows/unit-tests.yml) file.

This workflow triggers when a push on any branch modifies the Java main or test source files.

It consists of a single job, executing the [unit tests](JUnit.md). It currently does not report details other than in the workflow logs.

The workflow fails if any unit test does not pass, or if the project does not compile.

### Parameters tests

> [!TIP]
> See the [`parameters-tests.yml`](../../.github/workflows/parameters-tests.yml) file.

This workflow triggers when a push on any branch modifies example files or Java main source files.

It consists of a single job, executing the `generate.sh` script multiple times to find errors present in example files. It tries every possible combination of parameters.

The workflow fails if any example file is incorrect, or if the project does not compile.
