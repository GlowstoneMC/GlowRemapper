package net.glowstone.remapper;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;

import java.io.File;

/**
 * The action to execute for the remap task.
 */
public class RemapperAction implements Action<Task> {

    @Override
    public void execute(Task task) {
        final Project project = task.getProject();
        final RemapperExtension ext = (RemapperExtension) project.getExtensions().getByName("remapper");

        final File mapsFile = project.file(ext.getMapsFile());
        System.out.println("Remaps file is: " + mapsFile);
    }

}
