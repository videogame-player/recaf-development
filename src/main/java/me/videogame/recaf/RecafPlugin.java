package me.videogame.recaf;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.videogame.recaf.constants.Constants;
import me.videogame.recaf.http.HttpUtils;
import me.videogame.recaf.intellij.utils.IntellijUtils;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.plugins.JavaPlugin;

import javax.annotation.Nonnull;

public class RecafPlugin implements Plugin<Project> {

    private final String[] repos = new String[]{"https://jitpack.io/", "http://files.minecraftforge.net/maven"};

    @Override
    public void apply(@Nonnull Project project) {
        project.getPluginManager().apply(JavaPlugin.class);
        RepositoryHandler repositories = project.getRepositories();
        for (String repo : repos) {
            repositories.maven(block -> block.setUrl(repo));
        }

        repositories.mavenCentral();

        project.getExtensions().add(Constants.GRADLE_GROUP, new RecafExtension());

        String runnerVersion = HttpUtils.getGithubRepositoryLatestTag("videogame-player/recaf-runner");

        project.getDependencies().add(Constants.RUNTIME_ONLY, "com.github.videogame-player:recaf-runner:" + runnerVersion);

        project.afterEvaluate(this::createTasks);

    }

    private void createTasks(Project project) {
        RecafExtension extension = project.getExtensions().findByType(RecafExtension.class);
        if (extension == null) {
            throw new IllegalStateException("Failed to find the recaf extension (recaf { })");
        }

        if (extension.addRecaf) {
            String recafVersion = extension.recafVersion == null ? HttpUtils.getGithubRepositoryLatestTag("Col-E/Recaf") : extension.recafVersion;
            project.getDependencies().add(Constants.IMPLEMENTATION, "com.github.Col-E:Recaf:" + recafVersion);
        }
        project.getTasks().create(Constants.GEN_INTELLIJ_RUNS, task -> {
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
