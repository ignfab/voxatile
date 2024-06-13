# Checkstyle

We use [checkstyle](https://checkstyle.org/) to validate the code-style of the project. If you're already familiar with this tool, skip to the [Usage](#Usage) section.

## What is checkstyle?

Checkstyle is an external tool that goes through source files and validate they comply with a set of rules. It reports any violation indicating the file name and line number, along with the rule name and description, so that the developer can easily fix the issue.

Rules are highly configurable, and we can even create our own ones if needed. The tool uses an XML file as input configuration, and writes its outputs to the console and an XML file to it can be later analysed by another tool.

Read more about the existing checks and how to configure them in the [official documentation](https://checkstyle.org/checks.html).

## Usage

The configuration we use is defined in the [checkstyle.xml](../../checkstyle.xml) file in the project root. It is based upon the [`sun_checks.xml`](https://github.com/checkstyle/checkstyle/blob/master/src/main/resources/sun_checks.xml) file ([official Java code conventions](https://checkstyle.org/styleguides/sun-code-conventions-19990420/CodeConvTOC.doc.html)) and was then modified to fit our needs.

### Tuning rules

In some case, we could face a rule violation that we consider excessive. In this situation, we have 3 solutions:
1. Comply anyway: It may require an extra work on the moment, but it means better code at the end, so it is worth doing.
2. Disable the rule for this part of the code: If you have a good reason not to comply with this rule (i.e. the code is cleaner or easier to understand this way), you can disable it locally using the `@SuppressWarnings` annotation:
   ```java
   @SuppressWarnings("checkstyle:<rule-name>")
   // Example for rule "ParenPad":
   @SuppressWarnings("checkstyle:ParenPad")
   ```
3. Remove the rule: If you think this rule is inappropriate for our needs, and we would never benefit from it, consider disabling it globally by removing it from the configuration file (you can comment it out so we keep track about the fact it was explicitly disabled).

The opposite situation may also happen: If you think something is not currently validated by any checkstyle rule, and should be, do not hesitate to add the corresponding rule to do so!

### Running checkstyle

To validate the code-style, we use the `maven-checkstyle-plugin` to run the checkstyle tool using the [`checkstyle:check` Maven goal, bound to the `verify` phase](Maven.md#check-code-style). A [GitHub workflow](GitHub-workflows.md#checkstyle) also validates automatically the code-style on push and reports any violation using [reviewdog](https://github.com/reviewdog/reviewdog) through [GitHub Checks](https://github.com/reviewdog/reviewdog?tab=readme-ov-file#reporter-github-checks--reportergithub-check).
