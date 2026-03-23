"use strict";

const fs = require("fs");
const path = require("path");
const { execSync } = require("child_process");

const extensionDir = path.resolve(__dirname, "..");
const pkgPath = path.resolve(extensionDir, "package.json");
const profilesPath = path.resolve(__dirname, "..", "..", "profiles.json");

function pushCsv(values, csv) {
    if (!csv) {
        return;
    }
    values.push(...String(csv).split(",").map(part => part.trim()).filter(Boolean));
}

function parseEnvArg(value, args) {
    if (!value) {
        return;
    }
    const normalized = String(value).trim();
    if (normalized.startsWith("profile=")) {
        pushCsv(args.profileIds, normalized.slice("profile=".length));
    }
}

function parseArgs(argv) {
    const args = {
        dryRun: false,
        packageAll: false,
        profileIds: []
    };

    for (let index = 0; index < argv.length; index += 1) {
        const token = argv[index];

        if (token === "--dry-run") {
            args.dryRun = true;
            continue;
        }
        if (token === "--all") {
            args.packageAll = true;
            continue;
        }
        if (token === "--profile") {
            pushCsv(args.profileIds, argv[index + 1]);
            index += 1;
            continue;
        }
        if (token.startsWith("--profile=")) {
            pushCsv(args.profileIds, token.slice("--profile=".length));
            continue;
        }
        if (token.startsWith("--variants=") || token.startsWith("--profiles=")) {
            const prefix = token.startsWith("--profiles=") ? "--profiles=" : "--variants=";
            pushCsv(args.profileIds, token.slice(prefix.length));
            continue;
        }
        if (token === "--env") {
            parseEnvArg(argv[index + 1], args);
            index += 1;
            continue;
        }
        if (token.startsWith("--env=")) {
            parseEnvArg(token.slice("--env=".length), args);
            continue;
        }
        if (token.startsWith("profile=")) {
            parseEnvArg(token, args);
            continue;
        }
        if (token === "--help" || token === "-h") {
            console.log("Usage: node build-extension.js [--env profile=id] [--profile=id] [--all] [--dry-run]");
            process.exit(0);
        }
        args.profileIds.push(token);
    }

    args.profileIds = Array.from(new Set(args.profileIds));
    return args;
}

function getPackagingProfiles(config, requestedProfileIds) {
    const profiles = Array.isArray(config.profiles) ? config.profiles : [];
    if (profiles.length === 0) {
        throw new Error("No profiles configured. Add 'profiles' entries to client/vscode/profiles.json.");
    }

    if (!requestedProfileIds || requestedProfileIds.length === 0) {
        return profiles;
    }

    const selected = profiles.filter(profile => requestedProfileIds.includes(profile.id));
    if (selected.length !== requestedProfileIds.length) {
        const missing = requestedProfileIds.filter(id => !selected.some(profile => profile.id === id));
        const available = profiles.map(profile => profile.id).join(", ");
        throw new Error(`Unknown profile(s): ${missing.join(", ")}. Available profiles: ${available}`);
    }
    return selected;
}

function resolveRequestedProfileIds(args, config) {
    if (args.packageAll) {
        return [];
    }

    const ids = [...args.profileIds];
    if (ids.length === 0) {
        const profileFromEnv = process.env.UVL_PACKAGE_PROFILE || process.env.UVL_BUILD_PROFILE;
        pushCsv(ids, profileFromEnv);
    }
    if (ids.length === 0 && config.defaultProfile) {
        ids.push(config.defaultProfile);
    }

    return Array.from(new Set(ids));
}

function validatePackagingProfiles(profiles) {
    const names = new Set();
    const outputs = new Set();

    for (const profile of profiles) {
        if (!profile.id || !profile.name || !profile.displayName || !profile.outFile) {
            throw new Error(`Profile '${profile.id || "<unknown>"}' must define id, name, displayName and outFile for packaging.`);
        }

        if (names.has(profile.name)) {
            throw new Error(`Duplicate extension name '${profile.name}' across profiles.`);
        }
        names.add(profile.name);

        if (outputs.has(profile.outFile)) {
            throw new Error(`Duplicate outFile '${profile.outFile}' across profiles.`);
        }
        outputs.add(profile.outFile);
    }
}

const options = parseArgs(process.argv.slice(2));
const originalPkg = fs.readFileSync(pkgPath, "utf8");
const templatePkg = JSON.parse(originalPkg);
const config = JSON.parse(fs.readFileSync(profilesPath, "utf8"));
const requestedProfileIds = resolveRequestedProfileIds(options, config);
const packagingProfiles = getPackagingProfiles(config, requestedProfileIds);

validatePackagingProfiles(packagingProfiles);

try {
    console.log(`Packaging ${packagingProfiles.length} VSIX profile(s)...`);

    for (const profile of packagingProfiles) {
        console.log(`\nPackaging '${profile.id}' -> ${profile.outFile}`);
        const pkg = {
            ...templatePkg,
            name: profile.name,
            displayName: profile.displayName,
            description: profile.description || templatePkg.description
        };

        fs.writeFileSync(pkgPath, `${JSON.stringify(pkg, null, 2)}\n`, "utf8");

        if (!options.dryRun) {
            const outFile = String(profile.outFile).replace(/"/g, '\\"');
            execSync(`npx vsce package --yarn --out "${outFile}"`, {
                cwd: extensionDir,
                stdio: "inherit"
            });
        }
    }

    if (options.dryRun) {
        console.log("\nDry run complete. No VSIX files were generated.");
    } else {
        console.log("\nAll VSIX profiles were packaged successfully.");
    }
} catch (error) {
    console.error("\nPackaging failed:", error.message);
    process.exitCode = 1;
} finally {
    fs.writeFileSync(pkgPath, originalPkg, "utf8");
    console.log("Restored extension/package.json");
}