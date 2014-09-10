package net.glowstone.remapper;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * The main class for the remapper plugin.
 */
public class RemapperPlugin implements Plugin<Project> {

    @Override
    public void apply(final Project project) {
        project.getExtensions().create("remapper", RemapperExtension.class);
        project.task("remap").doFirst(new RemapperAction());
    }

}
