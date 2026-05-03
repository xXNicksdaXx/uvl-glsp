'use strict';

const fs = require('fs');
const path = require('path');

const profilesFile = path.resolve(__dirname, 'profiles.json');

function resolveBuildProfile(explicitProfileId) {
    const profileConfig = JSON.parse(fs.readFileSync(profilesFile, 'utf8'));
    const requestedProfileId = explicitProfileId || process.env.UVL_BUILD_PROFILE || profileConfig.defaultProfile;

    const selectedProfile = (profileConfig.profiles || []).find(profile => profile.id === requestedProfileId);
    if (!selectedProfile) {
        const available = (profileConfig.profiles || []).map(profile => profile.id).join(', ');
        throw new Error(`Unknown VS Code build profile '${requestedProfileId}'. Available profiles: ${available}`);
    }

    return {
        ...selectedProfile,
        serverJarAbsolutePath: path.resolve(__dirname, selectedProfile.serverJarPath)
    };
}

module.exports = {
    resolveBuildProfile
};
