# quarkus-qutebrowser

This is a fun little project that adds IPC and MCP capabilities to [Qutebrowser](http://qutebrowser.org) using [Quarkus](https://quarkus.io/).
In English:

It provides a CLI that allows you to:

- list tabs
- select tabs
- read the text of a tab

But also, it exposes this functionality to AI agents using the MCP protocol.

# Motivation

I often find myself copying and pasting stuff from the browser to my editor.
Most of the time the copied text requires formatting or some other kind of processing.
AI agents or editor tooling can help with the formatting, but does not help with the copying and pasting.

So, what if the tool or the AI agent could talk to the browser and get the text for me ?
This is exactly the kind of integration this project enables.

# Building

Build the project in native mode:

```bash
./mvnw clean install -Pnative
```

Copy the generated binary to your PATH as `quteb`:

```bash
cp target/quarkus-qutebrowser-${your version here}-runner $HOME/bin/quteb
```

Alternatively you can build in JVM mode and create an alias:

```bash
alias quteb='java -jar /path/to/quarkus-qutebrowser/target/quarkus-app/quarkus-run.jar'
```
This way you can run the CLI with:

# Usage

## CLI

### Listing tabs

```bash
quteb tabs list
```

### Selecting tabs

```bash
quteb tabs select <text to fuzilly match url or title>
```

### Reading tabs

```bash
quteb tabs read <text to fuzilly match url or title>
```

### MCP

To start an MCP server, providing all the above functionality, you can use:

```bash
quteb --mcp
```
