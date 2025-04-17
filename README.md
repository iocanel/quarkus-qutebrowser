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

The project can be build with: 

```bash
./mvnw clean install
```

To run the generated jar, you can use:

```bash
java -jar target/quarkus-app/quarkus-run.jar
```

To simplify things you can create an alias in your shell:

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
quteb mcp
```
