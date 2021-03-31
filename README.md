# recaf-development

A simple gradle plugin to make creating recaf plugins simpler.

## How to use?

Simple!

In your settings.gradle file put at the top

```groovy
pluginManagement {
    repositories {
        mavenLocal()
        maven { url "https://jitpack.io/" }
        maven { url "http://files.minecraftforge.net/maven" }
        gradlePluginPortal()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id.equals("me.videogame.recaf")) {
                useModule("com.github.videogame-player:recaf-development:${requested.version}")
            }
        }
    }
}
```

and then in your build.gradle put in the `plugins` block:
```groovy
plugins {
    id "me.videogame.recaf" version "1.1"
}
```

Head over [here](https://github.com/videogame-player/recaf-example-plugin) for an example of how to use this in a Recaf workspace.

Extension:
```groovy
recaf {
    mainClass = "com.example.MyPlugin" // Location the plugin use for recaf-runner (Required) (If not present running recaf WILL faiil)
    addRecaf = true // Adds the Recaf dependency to the project (Not required) (Default value: true)
    recafVersion = "2.18.3" // If you want to use a different version specify here (Not required)
}
```