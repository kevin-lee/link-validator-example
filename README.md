# link-validator

A Scala application that validates links in Markdown and HTML documents.

## Overview

link-validator is a tool that extracts and validates links from Markdown and
HTML documents. It helps identify broken links, ensuring your documentation
remains accurate and reliable.

**NOTE:** The current implementation inside the `modules` folder is just example
code used as a placeholder. You should replace it with your own later.

## Features (Plan)

- Extract links from Markdown files
- Extract links from HTML files
- Validate links with configurable options
- Generate reports of broken links
- Support for local and remote files

## Installation

### Using sbt

TBA

## Development

### Requirements

- JDK 17 or higher
- sbt 1.11.4

### Building from source

```bash
sbt compile
```

or just `compile` or `c` in sbt shell.

### Run

This is only for an example of the current example code. You should replace it
with yours later.

```bash
sbt "link-validator-app/run YourNameHere"
```

### Running tests

```bash
sbt test
```

or just `test` or `t` in sbt shell.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file
for details.
