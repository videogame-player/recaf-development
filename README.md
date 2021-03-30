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
    id "me.videogame.recaf" version "952870550c"
}
```

Head over [here](https://github.com/videogame-player/recaf-example-plugin) for an example of how to use this in a Recaf workspace.