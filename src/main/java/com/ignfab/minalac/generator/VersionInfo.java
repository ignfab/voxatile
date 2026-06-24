package com.ignfab.minalac.generator;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Class managing information string about map generation.
 */
public final class VersionInfo {

    private static final String VERSION = "Development version";
    private static final String GIT_COMMIT;
    private static final String GIT_BRANCH;
    private static final String GIT_REMOTE;

    static {

        // Git information
        Properties prop = new Properties();
        try {
            InputStream resource = MinalacGenerator.class.getClassLoader().getResourceAsStream("git.properties");
            if (resource != null)
                prop.load(resource);
        } catch (IOException ex) {
        }
        GIT_COMMIT = prop.getProperty("git.commit.id", "");
        String branch = prop.getProperty("git.branch", "");
        if (branch.equals(GIT_COMMIT))
            branch = "";
        GIT_BRANCH = branch;
        GIT_REMOTE = prop.getProperty("git.remote.origin.url");
    }

    private VersionInfo() {}

    /**
     * @return a string containing version information in markdown format.
     */
    public static String asMarkdown() {
        String result = VERSION;

        if (GIT_COMMIT.isBlank())
            result = "%n%nNo git information%n".formatted();
        else {
            result = "%n%nGit information%n".formatted();
            if (!GIT_REMOTE.isBlank())
                result += "* remote: %s%n".formatted(GIT_REMOTE);
            if (!GIT_BRANCH.isBlank())
                result += "* branch: %s%n".formatted(GIT_BRANCH);
            result += "* commit: %s".formatted(GIT_COMMIT);
        }
        return result;
    }

    /**
     * @return a string containing version information in text format.
     */
    public static String asText() {
        String result = VERSION;

        if (GIT_COMMIT.isBlank())
            result += "%nNo git information%n".formatted();
        else {
            result += "%nGit information:%n".formatted();
            if (!GIT_REMOTE.isBlank())
                result += "  remote: %s%n".formatted(GIT_REMOTE);
            if (!GIT_BRANCH.isBlank())
                result += "  branch: %s%n".formatted(GIT_BRANCH);
            result += "  commit: %s%n".formatted(GIT_COMMIT);
        }
        return result;
    }
}
