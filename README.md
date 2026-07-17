# UVL GLSP Diagram Editor

This project provides a diagram editor for the **Universal Variability Language (UVL)**, built using the [Eclipse GLSP](https://github.com/eclipse-glsp/glsp) framework. 
It includes:

- 🖥️ A **Java-based GLSP server** for handling diagram-specific logic
- 🖼️ A **VS Code extension** for integrating the diagram editor into Visual Studio Code
- 📦 **Multiple profiles** supporting UVL base and BP (Behavioral Programming) extensions

UVL is a concise, extensible language for modeling variability in software product lines.
UVL specifies variability models with a tree-like structure to represent the hierarchical structure of variability models.

## Quick Start

**Recommended:** download the prebuilt extension from Open VSX and install it locally via the VS Code Extensions view.

- Open VSX download pages (pick the profile you want and download the `.vsix`):

  - UVL (Base): [Link](https://open-vsx.org/extension/NickRuider/uvl-vscode-extension)
  - UVL (BP): [Link](https://open-vsx.org/extension/NickRuider/uvl-bp-vscode-extension)

- To install a downloaded `.vsix`: open the Extensions view in VS Code, click the ellipsis menu (⋯) and choose "Install from VSIX...", select the downloaded file, then reload the window.

Alternatively, build the extension locally and install the produced VSIX:

- Build (Windows):

```powershell
.\build.ps1 vscode uvl
# or for BP profile
.\build.ps1 vscode uvl-bp
```

- Build (Linux/macOS):

```bash
./build.sh vscode uvl
# or for BP profile
./build.sh vscode uvl-bp
```

After building, open `client/vscode/extension` and install the generated `.vsix` extension or use the existing VS Code run configurations.

**Using a running example:**

For UVL-BP, the extension uses [FMBP-SSE](https://github.com/xXNicksdaXx/FMBP-SSE) to support the _models@run.time_ approach.
To use the BP extension with a running example, clone the FMBP-SSE repository and follow the instructions in its README to set up the SSE server.

This should include setting up the workspace path in the ``config.json`` file so the execution of the behavioral program uses the same UVL model as the diagram editor.

**Connecting GLSP & FMBP:**

Once the FMBP-SSE repository is ready, follow these steps to connect both components for runtime feedback:

1. **Open the model:** In VS Code, open a `.uvl` model file that is configured to be executed (as specified in your FMBP-SSE configuration). The graphical GLSP diagram editor should open automatically.
2. **Start polling:** Opeb the VS Code Command Palette (`Ctrl+Shift+P`or `F1`), search for `uvl` polling command, and execute it to start polling against the FMBP-SSE instance.
3. **Execute the program:** Run your behavioral program via FMBP-SSE.

The connection between FMBP and GLSP is now established, enabling live runtime feedback directly inside the diagram editor.

## Project Structure

The project is organized as follows:

```
├── server/                     Java-based Eclipse GLSP server implementation
│   ├── uvl.metamodel/         UVL metamodel with ANTLR parser
│   ├── uvl.glsp/              GLSP server for UVL diagram editing  
│   └── uvl.bp.glsp/           GLSP server with BP-extension support
├── client/                     TypeScript/JavaScript client applications
│   ├── packages/              Shared packages and components
│   │   ├── common/            Shared types and utilities
│   │   ├── uvl-command-contribution/  VS Code command handlers
│   │   └── uvl-sprotty/       Core diagram rendering
│   ├── extensions/            Framework-specific extensions
│   │   ├── bp-sprotty/        BP diagram rendering (Sprotty)
│   │   └── sse-command-contribution/  SSE-specific commands
│   ├── vscode/                VS Code extension
│   │   ├── extension/         Extension host process
│   │   └── webview/           Webview diagram editor
│   └── workspace/             Example workspace and test files
└── build scripts              Platform-specific build automation
```

## Installation

Both profiles are published on the **Open VSX Registry** and can be installed directly from the VS Code Extensions view or via the command line:

* UVL Diagram Editor (Base): [`NickRuider/uvl-vscode-extension`](https://open-vsx.org/extension/NickRuider/uvl-vscode-extension)
* UVL Diagram Editor with BP Extension: [`NickRuider/uvl-bp-vscode-extension`](https://open-vsx.org/extension/NickRuider/uvl-bp-vscode-extension)

To install via the command line:

```bash
code --install-extension NickRuider/uvl-vscode-extension
# OR
code --install-extension NickRuider/uvl-bp-vscode-extension
```

## Prerequisites

Ensure the following are installed on your system:

- [Node.js](https://nodejs.org/en/) `>=20 <25`
- [Yarn](https://classic.yarnpkg.com/en/docs/install#debian-stable) `>=1.7.0 <2`
- [Java](https://dev.java/) `>=25` (for Eclipse GLSP server)
- [Maven](https://maven.apache.org/) `>=3.6.0`

This project is compatible with VS Code `^1.80.0`.

## Building the Project

The project consists of two main components: the Java-based GLSP server and the TypeScript/JavaScript client applications.

### Quick Build (All Components)

Use the provided build scripts to build both server and client:

**Windows**
```powershell
.\build.ps1
```

**Linux/macOS**
```bash
./build.sh
```

### Build Specific Components

You can build individual components by specifying a target:

#### Java Server Only

```powershell
.\build.ps1 server  # Windows
```

```bash
./build.sh server  # Linux/macOS
```

Or directly with Maven:
```bash
cd server
mvn clean verify
```

#### Client Applications Only

```powershell
.\build.ps1 client  # Windows
```

```bash
./build.sh client  # Linux/macOS
```

Or directly with Yarn:
```bash
cd client
yarn
```

#### VS Code Extension (with embedded server)

Build a complete VS Code extension with an embedded GLSP server:

```powershell
.\build.ps1 vscode              # Default 'uvl' profile
.\build.ps1 vscode uvl          # UVL profile
.\build.ps1 vscode uvl-bp       # UVL with BP extension
```

```bash
./build.sh vscode              # Default 'uvl' profile
./build.sh vscode uvl          # UVL profile
./build.sh vscode uvl-bp       # UVL with BP extension
```

### Build Profiles

The VS Code extension supports multiple profiles:

- **`uvl`**: Base UVL diagram editor (default)
- **`uvl-bp`**: UVL with Behavioral Programming (BP) extension

Each profile can be built independently with different server implementations.

### Clean Build Artifacts

To clean build artifacts from both server and client:

```powershell
.\build.ps1 clean  # Windows
```

```bash
./build.sh clean  # Linux/macOS
```

Or clean specific components:
```bash
cd server && mvn clean      # Clean Java server artifacts
cd client && yarn clean     # Clean client artifacts
```

## Running the Project

### IntelliJ IDEA

The project includes IntelliJ IDEA configuration files in the `.idea/` directory with pre-configured run configurations:

- **Launch UVL GLSP Server**: Starts the Java GLSP server on port 5007. Use this configuration for debugging server-side code.

### VS Code

Use the launch configurations defined in `.vscode/launch.json`. Open the project in Visual Studio Code and navigate to the **Run and Debug** view (`Ctrl + Shift + D`). Choose one of the following launch configurations:

- **Launch UVL GLSP Server**: Starts the standalone Java GLSP server. Use this configuration for debugging server-side code. _Note:_ There is also a run configuration available for JetBrains-IDEs.

- **VS Code Extension Configurations**:
  - **Launch UVL VS Code Extension (Embedded GLSP Server)**: 
    Starts a second VS Code instance with the extension installed. Opens an example workspace containing a `.uvl` file. The GLSP server runs as an embedded process. _Note:_ Server-side debugging is not available in this mode.
  - **Launch UVL VS Code Extension (Excluding GLSP Server)**:
    Similar to the above, but expects the GLSP server to be started externally using the **Launch UVL GLSP Server** configuration. This allows debugging both the client and server code.
  - **Launch UVL VS Code extension (Using external GLSP Server)**: 
    Launches both the UVL GLSP Server and the VS Code extension in external server mode. This enables simultaneous debugging of both the client and server code.

## Packaging the VS Code Extension

To package the VS Code extension as a `.vsix` file:

```bash
cd client
yarn package:uvl              # Default UVL profile
yarn package:uvl-bp           # UVL with BP extension
```

Or use the build scripts:
```powershell
.\build.ps1 vscode uvl        # Windows - UVL profile
.\build.ps1 vscode uvl-bp     # Windows - UVL-BP profile
```

```bash
./build.sh vscode uvl         # Linux/macOS - UVL profile
./build.sh vscode uvl-bp      # Linux/macOS - UVL-BP profile
```

The resulting `.vsix` file will be located in the `client/vscode/extension` directory and can be installed in VS Code using:

```bash
code --install-extension <path-to>.vsix
```

### Extension Profiles

The extension build system uses static profiles configured in `client/vscode/profiles.json`:

- `id`: Unique profile identifier (e.g., `uvl`, `uvl-bp`)
- `name`: Extension identifier for VSIX packaging
- `displayName`: Extension display name in the marketplace
- `outFile`: Output VSIX file name
- `description`: Profile-specific extension description
- `serverJarPath`: Path to the embedded GLSP server jar
- `containerModuleIds`: Sprotty container module IDs for webview
- `commandContributionIds`: VS Code command contribution IDs

## Resources

- [Project GitLab Repository](https://git-st.inf.tu-dresden.de/stgroup/student-projects/2026/ma-nick-ruider)
- [Project GitHub Repository](https://github.com/xXNicksdaXx/uvl-glsp)
- [Eclipse GLSP](https://www.eclipse.org/glsp/)
- [Universal Variability Language (UVL)](https://universal-variability-language.github.io/)
