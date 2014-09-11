GlowRemapper
============

GlowRemapper is a simple Gradle plugin to rename methods on classes in a compiled Java archive.
Provided with a file containing a list of mappings and an input jar, GlowRemapper produces a jar where the mappings have been applied.

All classes which inherit from a class with remapped methods will also have their corresponding methods remapped.

A single task, `remap`, is added, explained below.

Usage example
-------------
mappings.txt:
```
test/ClassName oldMethodName (I)V newMethodName
```

build.gradle:
```
// add dependency
buildscript {
    repositories {
        maven { url "http://repo.glowstone.net/content/groups/public/" }
    }
    dependencies {
        classpath 'net.glowstone:remapper:1.0'
    }
}

// configure remapper
remap {
    mappingFile = file('mappings.txt')
    inputJar = file('build/libs/original.jar')
    outputJar = file('build/libs/remapped.jar')
}
```

All configuration options available:
* `mappingFile`: specifies the file to read mappings from.
* `inputJar`: specifies the input jar to process.
* `inputTask`: a task which produces an archive (e.g. `jar`, `zip`, or `shadowJar`) to use as input.
* `outputJar`: specifies where to write the remapped jar.
* `outputFilename`: specifies the filename of the remapped jar in the same directory as the input.

`mappingFile` and one of `inputJar` or `inputTask` are required. If no `outputJar` or `outputFilename` is specified, one is chosen automatically.