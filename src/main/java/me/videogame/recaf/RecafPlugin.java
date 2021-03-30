package me.videogame.recaf;

import me.videogame.recaf.constants.Constants;
import me.videogame.recaf.intellij.utils.IntellijUtils;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.plugins.JavaPlugin;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class RecafPlugin implements Plugin<Project> {

    private final String[] repos = new String[]{"https://jitpack.io/", "http://files.minecraftforge.net/maven"};

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void apply(@Nonnull Project project) {
        project.getPluginManager().apply(JavaPlugin.class);
        RepositoryHandler repositories = project.getRepositories();
        for (String repo : repos) {
            repositories.maven(block -> block.setUrl(repo));
        }

        try {
            InputStream is = this.getClass().getResourceAsStream(Constants.RESOURCES_RUNNER_JAR_LOCATION);

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            buffer.flush();
            byte[] fileData = buffer.toByteArray();
            is.close();
            File f = new File(project.getGradle().getGradleUserHomeDir(), Constants.RUNNER_JAR_LOCATION);
            f.getParentFile().mkdirs();
            f.createNewFile();
            try (FileOutputStream fos = new FileOutputStream(f)) {
                fos.write(fileData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        repositories.mavenCentral();

        project.getExtensions().add(Constants.GRADLE_GROUP, new RecafExtension());

        Map<String, String> fileTreeMap = new HashMap<>();

        fileTreeMap.put(Constants.DIR, project.getGradle().getGradleUserHomeDir().getAbsolutePath() + "/" + Constants.RUNNER_JAR_LOCATION);
        fileTreeMap.put(Constants.INCLUDE, Constants.RUNNER_JAR_NAME);
        project.getDependencies().add(Constants.IMPLEMENTATION, project.fileTree(fileTreeMap));

        project.afterEvaluate(this::createTasks);
    }

    private void createTasks(Project project) {
        project.getTasks().create(Constants.GEN_INTELLIJ_RUNS, task -> {
            RecafExtension extension = project.getExtensions().findByType(RecafExtension.class);
            if (extension == null) {
                throw new IllegalStateException("Failed to find the recaf extension (recaf { })");
            }
            task.setGroup(Constants.GRADLE_GROUP);
            task.doLast(block -> {
                try {
                    IntellijUtils.INSTANCE.createRunConfig(extension);
                } catch (Exception e) {
                    System.err.println("Failed to create Intellij Run Configuration");
                    e.printStackTrace();
                }
            });
        });
    }
}
